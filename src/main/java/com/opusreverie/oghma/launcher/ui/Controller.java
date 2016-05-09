package com.opusreverie.oghma.launcher.ui;

import com.opusreverie.oghma.launcher.common.LauncherException;
import com.opusreverie.oghma.launcher.converter.Decoder;
import com.opusreverie.oghma.launcher.domain.AvailabilityRelease;
import com.opusreverie.oghma.launcher.domain.LauncherVersion;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.InstallProgressEvent;
import com.opusreverie.oghma.launcher.io.LocalReleaseRepository;
import com.opusreverie.oghma.launcher.io.ReleaseInstaller;
import com.opusreverie.oghma.launcher.io.file.DirectoryResolver;
import com.opusreverie.oghma.launcher.io.file.FileHandler;
import com.opusreverie.oghma.launcher.io.file.FileSystemInitializer;
import com.opusreverie.oghma.launcher.io.http.LauncherVersionService;
import com.opusreverie.oghma.launcher.io.http.ReleaseService;
import com.opusreverie.oghma.launcher.ui.component.CssListCell;
import com.opusreverie.oghma.launcher.ui.component.Notifier;
import com.opusreverie.oghma.launcher.ui.component.Notifier.NotificationType;
import com.opusreverie.oghma.launcher.ui.component.OghonDrawer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import rx.Subscription;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * UI controller that coordinates between UI components and services layer.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class Controller implements Initializable {

    private static final String       VERSION                = "1.0.0";
    private static final String       OGHMA_SELECTED_RELEASE = "oghma.selected.release";
    private final        Set<Release> downloaded             = new HashSet<>();
    private final ReleaseInstaller installer;
    @FXML
    Canvas                        picoCanvas;
    @FXML
    Button                        playButton;
    @FXML
    Button                        downloadButton;
    @FXML
    Button                        cancelDownloadButton;
    @FXML
    Pane                          pane;
    @FXML
    Label                         versionLabel;
    @FXML
    ComboBox<AvailabilityRelease> versions;
    @FXML
    HBox                          notificationBox;
    @FXML
    ImageView                     connectivityIcon;
    @FXML
    ImageView                     newIcon;
    private Notifier notifier;

    private OghonDrawer oghonDrawer;

    private transient Subscription activeInstallation;

    private DirectoryResolver dirResolver;

    private LocalReleaseRepository releaseRepository;

    public Controller() {
        dirResolver = DirectoryResolver.create();
        releaseRepository = new LocalReleaseRepository(new Decoder(), dirResolver, new FileHandler());
        installer = new ReleaseInstaller(releaseRepository, dirResolver);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final String serviceUri = System.getProperty("oghma.backend.url", "oghma.io/backend");

        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        oghonDrawer = new OghonDrawer(picoCanvas);
        notifier = new Notifier(notificationBox);

        oghonDrawer.drawDefault();
        setComboStyle();
        newIcon.managedProperty().bind(newIcon.visibleProperty());
        Tooltip tt = new Tooltip("found new releases available to download");
        Tooltip.install(newIcon, tt);

        playButton.managedProperty().bind(playButton.visibleProperty());
        playButton.setOnAction(evt -> playVersion(versions.getSelectionModel().getSelectedItem()));
        downloadButton.managedProperty().bind(downloadButton.visibleProperty());
        downloadButton.setOnAction(evt -> startDownload(versions.getSelectionModel().getSelectedItem()));
        cancelDownloadButton.managedProperty().bind(cancelDownloadButton.visibleProperty());
        cancelDownloadButton.setOnAction(evt -> cancelDownload(versions.getSelectionModel().getSelectedItem()));

        try {
            new FileSystemInitializer(dirResolver).setUpFileSystemStructure();
            Collection<Release> downloaded = releaseRepository.findAvailableReleases(notifier);
            setDownloadedReleases(downloaded);
            setSelectableReleases(Collections.emptyList());
        }
        catch (final LauncherException e) {
            notifier.notify(e.toString(), NotificationType.ERROR);
        }

        updateConnectivity(false);
        new ReleaseService(serviceUri).getReleasesWithRetry().thenAccept(this::setAvailableReleases);
        new LauncherVersionService(serviceUri).getLatestVersionWithRetry().thenAccept(this::setLauncherVersionStatus);

        versions.setOnAction(evt -> selectRelease(versions.getSelectionModel().getSelectedItem()));
    }

    private void selectRelease(final AvailabilityRelease release) {
        if (release == null) return;

        long[][] pico = oghonDrawer.fromPicoString(release.getRelease().getOghon());
        oghonDrawer.drawPico(pico);

        // Play/Download
        setUIDownloadState(false, !release.isDownloaded(), release.isDownloaded(), false);
    }

    private void playVersion(final AvailabilityRelease release) {
        if (!release.isDownloaded()) notifier.notify("Release not available to play", NotificationType.ERROR);

        final File jarFile = dirResolver.getReleaseBinary(release.getRelease().getVersion());
        try {
            new ProcessBuilder("java", "-jar", jarFile.getAbsolutePath()).start();
            preferences().put(OGHMA_SELECTED_RELEASE, release.getRelease().getVersion());
//            System.exit(0);
        }
        catch (IOException e) {
            final String errorMsg = MessageFormat.format("Could not start version. Reason: [{0}]", e.getMessage());
            notifier.notify(errorMsg, NotificationType.ERROR);
        }
    }

    private void startDownload(final AvailabilityRelease release) {
        if (release == null) return;
        Platform.runLater(() -> notifier.notify("Starting download " + release, NotificationType.INFO));
        setUIDownloadState(true, false, false, true);
        preferences().put(OGHMA_SELECTED_RELEASE, release.getRelease().getVersion());
        activeInstallation = installer.install(release.getRelease())
                .subscribe(this::updateProgress, ex -> downloadFailed(release, ex), () -> downloadCompleted(release));
    }

    private void updateProgress(final InstallProgressEvent prog) {
        Platform.runLater(() -> cancelDownloadButton.setText(MessageFormat.format("cancel ({0})", prog.getFormattedPercentage())));
    }

    private void cancelDownload(final AvailabilityRelease release) {
        if (release == null) return;
        Optional.ofNullable(activeInstallation).ifPresent(Subscription::unsubscribe);
        Platform.runLater(() -> notifier.notify("Cancelling download " + release, NotificationType.INFO));
        setUIDownloadState(false, true, false, false);
    }

    private void downloadCompleted(final AvailabilityRelease release) {
        release.setDownloaded(true);
        Platform.runLater(() -> notifier.notify("Download completed " + release, NotificationType.INFO));
        setUIDownloadState(false, false, true, false);
    }

    private void downloadFailed(final AvailabilityRelease release, final Throwable ex) {
        Platform.runLater(() -> notifier.notify(MessageFormat.format("Download failed for {0} - reason: {1}", release, ex.getMessage()), NotificationType.ERROR));
        setUIDownloadState(false, true, false, false);
    }

    private void setDownloadedReleases(final Collection<Release> downloaded) {
        this.downloaded.clear();
        this.downloaded.addAll(downloaded);
    }

    private void setLauncherVersionStatus(final LauncherVersion version) {
        if (!VERSION.equalsIgnoreCase(version.getVersion())) {
            Platform.runLater(() -> {
                versionLabel.setVisible(true);
                versionLabel.setText("New launcher available from www.oghma.io");
            });
        }
    }

    private void setAvailableReleases(final Collection<Release> available) {
        Platform.runLater(() -> {
            updateConnectivity(true);
            setSelectableReleases(available);
        });
    }

    private void setUIDownloadState(boolean cancelVisible, boolean downloadVisible, boolean playVisible, boolean versionsDisabled) {
        Platform.runLater(() -> {
            cancelDownloadButton.setVisible(cancelVisible);
            downloadButton.setVisible(downloadVisible);
            playButton.setVisible(playVisible);
            versions.setDisable(versionsDisabled);
        });
    }

    private void deleteRelease(final Release release) {
        try {
            releaseRepository.deleteRelease(release);
            downloaded.remove(release);
        }
        catch (final IOException e) {
            notifier.notify("Could not remove release " + release + ". Reason: " + e + " Please do so manually",
                    NotificationType.ERROR);
        }
    }

    /**
     * Merges downloaded and available lists.
     */
    private Set<AvailabilityRelease> setSelectableReleases(final Collection<Release> available) {

        final ReleaseListBuilder presenter = new ReleaseListBuilder();

        // Delete out of date snapshot downloads.
        presenter.findExpiredSnapshots(downloaded, available)
                .forEach(this::deleteRelease);

        // Merge downloaded and available.
        final Set<AvailabilityRelease> selectable = presenter.merge(downloaded, available);

        Platform.runLater(() -> {
            versions.setItems(FXCollections.observableArrayList(selectable));
            selectReleasePreference();
        });

        return selectable;
    }

    private void selectReleasePreference() {
        if (!versions.getItems().isEmpty()) {
            final String preferredVersion = preferences().get(OGHMA_SELECTED_RELEASE, null);
            final AvailabilityRelease toSelect = versions.getItems().stream()
                    .filter(ar -> Objects.equals(preferredVersion, ar.getRelease().getVersion()))
                    .findFirst().orElse(versions.getItems().get(0));
            versions.getSelectionModel().select(toSelect);
        }
    }

    private void updateConnectivity(boolean connectivity) {
        final Tooltip tt = new Tooltip(connectivity ? "has connectivity" : "no connectivity to download new versions");
        Tooltip.install(connectivityIcon, tt);
        final String iconPath = MessageFormat.format("/icon/{0}connectivity.png", connectivity ? "" : "no_");
        connectivityIcon.setImage(new Image(this.getClass().getResourceAsStream(iconPath)));
        connectivityIcon.setVisible(true);
    }

    private void setComboStyle() {
        versions.setButtonCell(new CssListCell<>("#FFFFFF"));
    }

    private Preferences preferences() {
        return Preferences.userRoot().node(this.getClass().getName());
    }

}

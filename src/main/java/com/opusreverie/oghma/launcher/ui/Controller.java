package com.opusreverie.oghma.launcher.ui;

import com.opusreverie.oghma.launcher.common.LauncherException;
import com.opusreverie.oghma.launcher.converter.Decoder;
import com.opusreverie.oghma.launcher.domain.AvailabilityRelease;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.FileHandler;
import com.opusreverie.oghma.launcher.io.FileSystemInitializer;
import com.opusreverie.oghma.launcher.io.LocalReleaseRepository;
import com.opusreverie.oghma.launcher.io.ReleaseInstaller;
import com.opusreverie.oghma.launcher.io.download.ProgressEvent;
import com.opusreverie.oghma.launcher.io.file.DirectoryResolver;
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

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

/**
 * UI controller that coordinates between UI components and services layer.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class Controller implements Initializable {

    @FXML
    Canvas picoCanvas;

    @FXML
    Button playButton;

    @FXML
    Button downloadButton;

    @FXML
    Button cancelDownloadButton;

    @FXML
    Pane pane;

    @FXML
    Label versionLabel;

    @FXML
    ComboBox<AvailabilityRelease> versions;

    @FXML
    HBox notificationBox;

    @FXML
    ImageView connectivityIcon;

    @FXML
    ImageView newIcon;

    private final Set<Release> downloaded = new HashSet<>();

    private ReleaseInstaller installer;

    private Notifier notifier;

    private OghonDrawer oghonDrawer;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final Path oghmaAppData = Paths.get(System.getProperty("user.home"), ".oghma");
        final String serviceUri = System.getProperty("oghma.backend.url", "oghma.io/backend");

        DirectoryResolver dirResolver = new DirectoryResolver(oghmaAppData);
        LocalReleaseRepository releaseRepository = new LocalReleaseRepository(new Decoder(), dirResolver, new FileHandler());
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        oghonDrawer = new OghonDrawer(picoCanvas);
        installer = new ReleaseInstaller(oghmaAppData, releaseRepository);
        notifier = new Notifier(notificationBox);

        oghonDrawer.drawDefault();
        setComboStyle();
        newIcon.managedProperty().bind(newIcon.visibleProperty());
        Tooltip tt = new Tooltip("found new releases available to download");
        Tooltip.install(newIcon, tt);

        playButton.managedProperty().bind(playButton.visibleProperty());
        downloadButton.managedProperty().bind(downloadButton.visibleProperty());
        downloadButton.setOnAction(evt -> startDownload(versions.getSelectionModel().getSelectedItem()));
        cancelDownloadButton.managedProperty().bind(cancelDownloadButton.visibleProperty());
        cancelDownloadButton.setOnAction(evt -> cancelDownload(versions.getSelectionModel().getSelectedItem()));

        try {
            new FileSystemInitializer().setUpFileSystemStructure(oghmaAppData);
            Collection<Release> downloaded = releaseRepository.findAvailableReleases(notifier);
            setSelectableReleases(downloaded, Collections.emptyList());
        } catch (final LauncherException e) {
            notifier.notify(e.toString(), NotificationType.ERROR);
        }

        updateConnectivity(false);
        new ReleaseService(serviceUri).getReleasesWithRetry().thenAccept(this::setAvailableReleases);

        versions.setOnAction(evt -> selectRelease(versions.getSelectionModel().getSelectedItem()));
    }

    private void selectRelease(final AvailabilityRelease release) {
        if (release == null) return;

        long[][] pico = oghonDrawer.fromPicoString(release.getRelease().getOghon());
        oghonDrawer.drawPico(pico);

        // Play/Download
        playButton.setVisible(release.isDownloaded());
        downloadButton.setVisible(!release.isDownloaded());
        cancelDownloadButton.setVisible(false);
    }

    private void startDownload(final AvailabilityRelease release) {
        if (release == null) return;
        Platform.runLater(() -> {
            notifier.notify("Starting download " + release, NotificationType.INFO);
            cancelDownloadButton.setVisible(true);
            downloadButton.setVisible(false);
            versions.setDisable(true);
        });
        installer.install(release.getRelease())
                .subscribe(this::updateProgress, ex -> downloadFailed(release, ex), () -> downloadCompleted(release));
    }

    private void updateProgress(final ProgressEvent prog) {
        Platform.runLater(() -> cancelDownloadButton.setText(MessageFormat.format("cancel ({0})", prog.getFormattedPercentage())));
    }

    private void cancelDownload(final AvailabilityRelease release) {
        if (release == null) return;
        Platform.runLater(() -> {
            notifier.notify("Cancelling download " + release, NotificationType.INFO);
            cancelDownloadButton.setVisible(false);
            downloadButton.setVisible(true);
            versions.setDisable(false);
        });
    }

    private void downloadCompleted(final AvailabilityRelease release) {
        release.setDownloaded(true);
        Platform.runLater(() -> {
            notifier.notify("Download completed " + release, NotificationType.INFO);
            cancelDownloadButton.setVisible(false);
            downloadButton.setVisible(false);
            playButton.setVisible(true);
            versions.setDisable(false);
        });
    }

    private void downloadFailed(final AvailabilityRelease release, final Throwable ex) {
        Platform.runLater(() -> {
            notifier.notify(MessageFormat.format("Download failed for {0} - reason: {2}", release, ex.getMessage()), NotificationType.ERROR);
            cancelDownloadButton.setVisible(false);
            downloadButton.setVisible(true);
            versions.setDisable(false);
        });
    }

    private void setDownloadedReleases(final Collection<Release> downloaded) {
        this.downloaded.clear();
        this.downloaded.addAll(downloaded);
    }

    private void setAvailableReleases(final Collection<Release> available) {
        Platform.runLater(() -> {
            updateConnectivity(true);
            setSelectableReleases(downloaded, available);
        });
    }

    /**
     * Merges downloaded and available lists.
     */
    private Set<AvailabilityRelease> setSelectableReleases(final Collection<Release> downloaded, final Collection<Release> available) {
        setDownloadedReleases(downloaded);

        // Merge
        final Set<AvailabilityRelease> selectable = new LinkedHashSet<>();

        downloaded.stream().map(AvailabilityRelease::downloaded).forEach(selectable::add);

        available.stream()
                .filter(a -> !downloaded.contains(a))
                .map(AvailabilityRelease::available)
                .forEach(selectable::add);

        Platform.runLater(() -> {
            versions.setItems(FXCollections.observableArrayList(selectable));
            if (!selectable.isEmpty()) versions.getSelectionModel().select(0);
        });

        return selectable;
    }

    private void updateConnectivity(boolean connectivity) {
        Tooltip tt = new Tooltip(connectivity ? "has connectivity" : "no connectivity to download new versions");
        Tooltip.install(connectivityIcon, tt);
        final String iconPath = MessageFormat.format("/icon/{0}connectivity.png", connectivity ? "" : "no_");
        connectivityIcon.setImage(new Image(this.getClass().getResourceAsStream(iconPath)));
    }

    private void setComboStyle() {
        versions.setButtonCell(new CssListCell<>("#FFFFFF"));
    }

}

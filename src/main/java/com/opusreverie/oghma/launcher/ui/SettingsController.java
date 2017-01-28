package com.opusreverie.oghma.launcher.ui;

import com.opusreverie.oghma.launcher.ui.component.StyleUtil;
import io.lyra.oghma.common.OghmaConst;
import io.lyra.oghma.common.config.StandardConfigOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UI settings controller that coordinates between UI components and services layer.
 *
 * @author Cian.
 */
public class SettingsController extends BaseLauncherController implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private ComboBox<String> cboResolutions;

    @FXML
    private CheckBox chkFullscreen;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        chkFullscreen.setSelected(isFullscreen());
        chkFullscreen.setOnMouseClicked(__ -> updateFullscreenOption(chkFullscreen.isSelected()));

        final List<String> resolutionStrings = findDisplayModes().stream().map(dm -> dm.getWidth() + "," + dm.getHeight()).collect(Collectors.toList());
        StyleUtil.setComboTextColor(cboResolutions, "#FFFFFF");
        cboResolutions.getItems().setAll(resolutionStrings);
        final String existingResolutionString = config().get(StandardConfigOption.RESOLUTION);
        if (existingResolutionString != null) {
            cboResolutions.getSelectionModel().select(existingResolutionString);
        }
        cboResolutions.setOnAction(__ -> selectResolution(cboResolutions.getSelectionModel().getSelectedItem()));
    }

    private void selectResolution(final String resolution) {
        setOptionValue(StandardConfigOption.RESOLUTION, resolution);
    }

    private void updateFullscreenOption(final boolean selected) {
        setOptionValue(StandardConfigOption.FULLSCREEN, Boolean.toString(selected));
    }

    private boolean isFullscreen() {
        return config().getBoolean(StandardConfigOption.FULLSCREEN, true);
    }


    private List<DisplayMode> findDisplayModes() {
        final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = genv.getDefaultScreenDevice();
        final DisplayMode desktopMode = device.getDisplayMode();
        final DisplayMode[] displayModes = device.getDisplayModes();
        return Stream.of(displayModes)
                .filter(dm -> dm.getBitDepth() == desktopMode.getBitDepth())
                .filter(dm -> dm.getWidth() <= OghmaConst.MAX_RESOLUTION_WIDTH && dm.getHeight() <= OghmaConst.MAX_RESOLUTION_HEIGHT)
                .filter(dm -> dm.getWidth() >= OghmaConst.MIN_RESOLUTION_WIDTH && dm.getHeight() >= OghmaConst.MIN_RESOLUTION_HEIGHT)
                .distinct()
                .collect(Collectors.toList());
    }

}

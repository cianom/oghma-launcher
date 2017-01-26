package com.opusreverie.oghma.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point of launcher.
 *
 * @author Cian.
 */
public class Main extends Application {

    private static final String TITLE = "oghma";

    @Override
    public void start(final Stage stage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        stage.setTitle(TITLE);
        stage.setResizable(false);
        final Scene scene = new Scene(root);
        scene.getStylesheets().add("/launcher.css");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

}

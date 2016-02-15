package com.opusreverie.oghma.launcher.ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Created by keen on 26/01/16.
 */
public class LaunchScreen {


    public void init(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 500, 500, Color.WHITE);

        Rectangle r = new Rectangle(25,25,250,250);
        r.setFill(Color.BLUE);
        root.getChildren().add(r);

        Button b = new Button("Play");
        root.getChildren().add(b);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setProgress(40);
        root.getChildren().add(progressBar);

        stage.setTitle("oghma");
        stage.setScene(scene);
        stage.show();

        scene.setRoot(new ProgressBar(4));
    }
}

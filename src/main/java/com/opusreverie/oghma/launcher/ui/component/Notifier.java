package com.opusreverie.oghma.launcher.ui.component;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Notification service to present user with helpful event notifications.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class Notifier {

    public enum NotificationType {
        INFO(Color.web("#55CCFF")), WARN(Color.ORANGE), ERROR(Color.RED);

        private Color color;
        NotificationType(Color color) {
            this.color = color;
        }

    }

    private final HBox notificationBox;

    public Notifier(HBox notificationBox) {
        this.notificationBox = notificationBox;
    }

    public void notify(final String message, final NotificationType type) {
        Platform.runLater(() -> {
            final Label notification = new Label();
            notification.setTextFill(type.color);
//            notification.setTextFill(Color.WHITE);
            notification.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//            notification.setBackground(new Background(new BackgroundFill(Color.web("#F3F3F3"), CornerRadii.EMPTY, Insets.EMPTY)));
//            notification.setBackground(new Background(new BackgroundFill(type.color, CornerRadii.EMPTY, Insets.EMPTY)));
            notification.setTextAlignment(TextAlignment.CENTER);
            notification.setAlignment(Pos.CENTER);
            notification.setBorder(new Border(new BorderStroke(Color.web("#F0F0F0"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            notification.setText(Character.toString(type.name().charAt(0)));
            Tooltip.install(notification, new Tooltip(message));
            double dim = notificationBox.getHeight();
            notification.setPrefSize(dim, dim);

            int maxNotifications = (int) Math.floor(notificationBox.getWidth() / dim);
            if (notificationBox.getChildren().size() >= maxNotifications) {
                notificationBox.getChildren().remove(0);
            }
            notificationBox.getChildren().add(notification);
            FadeTransition t = new FadeTransition(Duration.seconds(40), notification);
            t.setFromValue(1);
            t.setToValue(0.2);
            t.play();
        });
    }

}

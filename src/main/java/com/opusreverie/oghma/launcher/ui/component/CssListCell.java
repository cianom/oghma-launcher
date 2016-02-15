package com.opusreverie.oghma.launcher.ui.component;

import javafx.scene.control.ListCell;

/**
 * Created by keen on 07/02/16.
 */
public class CssListCell<T> extends ListCell<T> {

    /**
     * CSS-style color. Example:  #FFFFFF
     */
    private final String textColor;

    public CssListCell(String textColor) {
        this.textColor = textColor;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            // styled like -fx-prompt-text-fill:
            setStyle("-fx-text-fill: derive(-fx-control-inner-background,-30%)");
        } else {
            setStyle("-fx-text-fill: " + textColor);
            setText(item.toString());
        }
    }


}

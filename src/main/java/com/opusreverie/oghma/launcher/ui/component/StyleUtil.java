package com.opusreverie.oghma.launcher.ui.component;

import javafx.scene.control.ComboBox;

/**
 * Custom styling utility.
 *
 * @author Cian.
 */
public class StyleUtil {

    public static void setComboTextColor(final ComboBox<?> combo, final String textColor) {
        combo.setButtonCell(new CssListCell<>(textColor));
    }

}

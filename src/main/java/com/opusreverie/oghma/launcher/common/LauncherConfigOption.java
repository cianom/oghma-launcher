package com.opusreverie.oghma.launcher.common;

import io.lyra.oghma.common.config.ConfigOption;

/**
 * Launcher-specific config options.
 *
 * @author Cian.
 */
public enum LauncherConfigOption implements ConfigOption {

    SELECTED_RELEASE("last.selected.release", null);


    LauncherConfigOption(final String optionName, final String defaultValue) {
        this.optionName = optionName;
        this.defaultValue = defaultValue;
    }

    private final String optionName;
    private final String defaultValue;

    @Override
    public String getOptionName() {
        return optionName;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

}

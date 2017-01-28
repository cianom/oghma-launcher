package com.opusreverie.oghma.launcher.ui;

import io.lyra.oghma.common.config.ConfigOption;
import io.lyra.oghma.common.config.OghmaConfig;
import io.lyra.oghma.common.config.PreferenceOghmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Abstract base controller.
 *
 * @author Cian.
 */
abstract class BaseLauncherController {

    private static final Logger LOG = LoggerFactory.getLogger(BaseLauncherController.class);

    private OghmaConfig config;

    BaseLauncherController() {
        this.config = new PreferenceOghmaConfig();
    }

    OghmaConfig config() {
        return config;
    }

    void setOptionValue(final ConfigOption option, final String value) {
        try {
            config().put(option, value);
        }
        catch (final IOException e) {
            LOG.error("Could not set option " + option, e);
        }
    }

}

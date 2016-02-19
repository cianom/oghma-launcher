package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information on the latest launcher version.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class LauncherVersion {


    @JsonProperty("version")
    private final String version;

    @JsonCreator
    public LauncherVersion(@JsonProperty("version") final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

}

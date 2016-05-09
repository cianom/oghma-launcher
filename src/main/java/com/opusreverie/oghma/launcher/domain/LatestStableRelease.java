package com.opusreverie.oghma.launcher.domain;

import java.text.MessageFormat;

/**
 * A symbolic link release. This references another release that
 * is the latest stable release available, either downloaded or
 * downloadable.
 *
 * @author Cian.
 */
public class LatestStableRelease extends Release {

    public static final String LATEST_STABLE_VERSION = "9.9.9";

    private final String referencedVersion;

    LatestStableRelease(final Release r) {
        super(LATEST_STABLE_VERSION, r.getName(), r.getReleasedOn(), r.getOghon(),
                false, r.getBinary(), r.getContent());
        this.referencedVersion = r.getVersion();
    }

    @Override
    public String toString() {
        return MessageFormat.format("Latest Stable ({0})", referencedVersion);
    }

}

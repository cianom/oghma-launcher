package com.opusreverie.oghma.launcher.domain;

/**
 * Release object decorated with availability information.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class AvailabilityRelease {

    private final Release release;

    private boolean downloaded;

    private AvailabilityRelease(final Release release, final boolean downloaded) {
        this.release = release;
        this.downloaded = downloaded;
    }

    public static AvailabilityRelease downloaded(final Release release) {
        return new AvailabilityRelease(release, true);
    }

    public static AvailabilityRelease available(final Release release) {
        return new AvailabilityRelease(release, false);
    }

    public Release getRelease() {
        return release;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return release.toString();
    }

}

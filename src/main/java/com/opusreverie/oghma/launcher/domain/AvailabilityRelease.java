package com.opusreverie.oghma.launcher.domain;

import java.util.Optional;
import java.util.Set;

/**
 * Release object decorated with availability information.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class AvailabilityRelease implements Comparable<AvailabilityRelease> {

    private final Release release;

    private boolean downloaded;

    private AvailabilityRelease(final Release release, final boolean downloaded) {
        this.release = release;
        this.downloaded = downloaded;
    }

    /**
     * Creates a new downloaded release.
     */
    public static AvailabilityRelease downloaded(final Release release) {
        return new AvailabilityRelease(release, true);
    }

    /**
     * Creates a new remotely available release.
     */
    public static AvailabilityRelease available(final Release release) {
        return new AvailabilityRelease(release, false);
    }

    /**
     * Given a list of availability releases, finds the latest stable release (if any).
     *
     * @param selection the list of available releases.
     * @return the latest stable version, if any.
     */
    public static Optional<AvailabilityRelease> createLatestStable(final Set<AvailabilityRelease> selection) {
        return selection.stream()
                .filter(AvailabilityRelease::isStable)
                .max(AvailabilityRelease::compareTo)
                .map(ar -> {
                    final Release stable = new LatestStableRelease(ar.getRelease());
                    return (ar.isDownloaded())
                            ? AvailabilityRelease.downloaded(stable) : AvailabilityRelease.available(stable);
                });
    }

    public Release getRelease() {
        return release;
    }

    public boolean isStable() {
        return !isSnapshot();
    }

    private boolean isSnapshot() {
        return release.isSnapshot();
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

    /**
     * Compares two version strings.
     * <p/>
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     * <p/>
     * Note: It does not work if "1.10" is supposed to be equal to "1.10.0".
     *
     * @param o the other release to compare against.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     * The result is a positive integer if str1 is _numerically_ greater than str2.
     * The result is zero if the strings are _numerically_ equal.
     */
    @Override
    public int compareTo(final AvailabilityRelease o) {
        final String[] vals1 = release.getVersion().split("\\.");
        final String[] vals2 = o.getRelease().getVersion().split("\\.");
        int i = 0;
        // Set index to first non-equal ordinal or length of shortest version string.
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // Compare first non-equal ordinal number.
        if (i < vals1.length && i < vals2.length) {
            final int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // The strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

}

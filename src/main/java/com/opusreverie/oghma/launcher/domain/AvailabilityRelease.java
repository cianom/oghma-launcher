package com.opusreverie.oghma.launcher.domain;

import io.lyra.oghma.common.content.SemanticVersion;

import java.util.Optional;
import java.util.Set;

/**
 * Release object decorated with availability information.
 *
 * @author Cian.
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

    public void setDownloaded(final boolean downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return release.toString();
    }

    /**
     * Compares two releases using their semantic versions.
     *
     * @see SemanticVersion#compareTo(Object).
     */
    @Override
    public int compareTo(final AvailabilityRelease o) {
        if (o.isSnapshot() != isSnapshot()) {
            return (o.isSnapshot()) ? 1 : -1;
        }
        final SemanticVersion thisVersion = new SemanticVersion(getRelease().getVersion());
        final SemanticVersion thatVersion = new SemanticVersion(o.getRelease().getVersion());
        return thisVersion.compareTo(thatVersion);
    }

}

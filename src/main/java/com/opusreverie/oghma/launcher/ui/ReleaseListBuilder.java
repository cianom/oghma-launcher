package com.opusreverie.oghma.launcher.ui;

import com.opusreverie.oghma.launcher.domain.AvailabilityRelease;
import com.opusreverie.oghma.launcher.domain.Release;

import java.util.*;

/**
 * Presentation service which, given lists of downloaded and downloadable
 * releases, presents them into a view-friendly, sorted list.
 *
 * @author Cian.
 */
class ReleaseListBuilder {


    /**
     * Finds expired downloaded snapshots. A download is considered expired if it
     * is a snapshot and does not exist in the available to download list. If the
     * available list is empty, this method will simply short-circuit.
     *
     * @param downloaded list of all downloaded releases on disk.
     * @param available  list of all available releases for download.
     * @return a set containing the expired downloaded snapshots.
     */
    Set<Release> findExpiredSnapshots(final Collection<Release> downloaded, final Collection<Release> available) {
        final Set<Release> expired = new LinkedHashSet<>();

        // Only proceed if available list was actually found (non-empty).
        if (!available.isEmpty()) {
            // Any downloaded snapshot release that is not in available list is considered expired.
            downloaded.stream()
                    .filter(Release::isSnapshot)
                    .filter(d -> !available.contains(d))
                    .forEach(expired::add);
        }

        return expired;
    }

    /**
     * Merges downloaded and downloadable lists.
     * <p/>
     * Expired snapshots are filtered out, and reference releases like
     * "Latest Stable" are added to the final list. The final list
     * will be sorted in descending order (newest releases first).
     */
    SortedSet<AvailabilityRelease> merge(final Collection<Release> downloadedList, final Collection<Release> available) {
        final Set<Release> downloaded = new HashSet<>(downloadedList);
        final SortedSet<AvailabilityRelease> resultSet = new TreeSet<>(Collections.reverseOrder());

        // Remove expired downloaded.
        final Set<Release> expired = findExpiredSnapshots(downloaded, available);
        downloaded.removeAll(expired);

        // Add valid existing downloadedSet releases to list.
        downloaded.stream()
                .filter(d -> available.stream().noneMatch(d::isExpiredSnapshot))
                .map(AvailabilityRelease::downloaded)
                .forEach(resultSet::add);

        // Add downloadable releases to list.
        available.stream()
                .filter(a -> !downloaded.contains(a))
                .map(AvailabilityRelease::available)
                .forEach(resultSet::add);

        // Add "latest stable" release.
        AvailabilityRelease.createLatestStable(resultSet)
                .ifPresent(resultSet::add);

        return resultSet;
    }

}

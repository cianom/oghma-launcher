package com.opusreverie.oghma.launcher.ui;

import com.opusreverie.oghma.launcher.domain.AvailabilityRelease;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.helper.ReleaseTestUtil;
import org.junit.Test;

import java.util.*;

import static com.opusreverie.oghma.launcher.domain.LatestStableRelease.LATEST_STABLE_VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link ReleaseListBuilder}.
 */
public class ReleaseListBuilderTest {

    private ReleaseListBuilder sut = new ReleaseListBuilder();

    private Date createDate(final int hoursInPast) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -hoursInPast);
        return calendar.getTime();
    }

    private List<Release> createReleases(final int startIndex, final int stableCount, final int snapshotCount) {
        return createReleases(startIndex, stableCount, snapshotCount, Calendar.getInstance().getTime());
    }

    private List<Release> createReleases(final int startIndex, final int stableCount, final int snapshotCount,
                                         final Date date) {
        final List<Release> downloaded = new ArrayList<>();
        int i = startIndex;
        for (int n = 0; n < stableCount; n++, i++) {
            downloaded.add(ReleaseTestUtil.createTestRelease(1, false, i, date));
        }
        for (int n = 0; n < snapshotCount; n++, i++) {

            downloaded.add(ReleaseTestUtil.createTestRelease(1, true, i, date));
        }
        return downloaded;
    }

    @Test
    public void merge_NoReleases_NoLatestStableFound() throws Exception {
        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 0, 0), createReleases(0, 0, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    public void merge_MultipleStable_LatestChosenCorrectly() throws Exception {
        final List<Release> available = createReleases(1, 2, 0);
        final Release latestAvailable = available.get(available.size() - 1);
        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 2, 0), available);

        final AvailabilityRelease latestStable = result.stream()
                .filter(ar -> LATEST_STABLE_VERSION.equals(ar.getRelease().getVersion()))
                .findFirst().orElse(null);

        assertEquals(latestAvailable.getName(), latestStable.getRelease().getName());
    }

    @Test
    public void merge_AllSnapshotReleases_NoLatestStableFound() throws Exception {
        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 0, 2), createReleases(1, 0, 2));

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(AvailabilityRelease::isStable));
    }

    @Test
    public void merge_MultipleStable_LatestStableChoosesCorrectOne() throws Exception {
        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 2, 0), createReleases(1, 2, 0));

        assertEquals(4, result.size());
        assertTrue(result.stream().allMatch(AvailabilityRelease::isStable));
        assertEquals(1, result.stream().filter(ar -> LATEST_STABLE_VERSION.equals(ar.getRelease().getVersion())).count());
    }

    @Test
    public void merge_AvailableSnapshotNewerThanDownloaded_UseNewer() throws Exception {
        final Date newerDate = createDate(0);
        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 0, 2, createDate(1)),
                createReleases(0, 0, 2, newerDate));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(ar -> ar.getRelease().getReleasedOn().equals(newerDate)));
    }

    @Test
    public void merge_DownloadedSnapshotNotInAvailableList_ExcludeFromResult() throws Exception {
        final Date newerDate = createDate(0);

        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 0, 1, createDate(1)),
                createReleases(1, 0, 1, newerDate));

        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(ar -> ar.getRelease().getReleasedOn().equals(newerDate)));
    }

    @Test
    public void merge_AvailableListEmpty_NoSnapshotsMarkedAsExpired() throws Exception {
        final Set<AvailabilityRelease> result = sut.merge(createReleases(0, 0, 1), createReleases(0, 0, 0));

        assertEquals(1, result.size());
    }

    @Test
    public void merge_CombinationsOfSnapshotAndStableForBothDownloadedAndAvailable_MergeCorrectly() throws Exception {
        final Collection<Release> downloaded = createReleases(0, 3, 3, createDate(1));
        final Collection<Release> available = createReleases(1, 2, 2, createDate(0));

        final Set<AvailabilityRelease> result = sut.merge(downloaded, available);

        // 1 "Latest Stable", 3 stable, 2 snapshots.
        assertEquals(6, result.size());
    }


}
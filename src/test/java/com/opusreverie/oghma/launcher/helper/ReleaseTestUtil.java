package com.opusreverie.oghma.launcher.helper;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.domain.Release;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility for creating releases/content.
 */
public class ReleaseTestUtil {

    // SHA256 hash for bytes [1, 2].
    private static final String HASH = "a12871fee210fb8619291eaea194581cbd2531e4b23759d225f6806923f63222";

    public static Release createTestRelease(final int contentFileCount, final boolean snapshot) {
        return createTestRelease(contentFileCount, snapshot, 0, Calendar.getInstance().getTime());
    }
    public static Release createTestRelease(final int contentFileCount, final boolean snapshot, final int index,
                                            final Date date) {
        final List<Content> contents = IntStream.range(1, 1 + contentFileCount)
                .mapToObj(ReleaseTestUtil::createTestPack)
                .collect(Collectors.toList());
        return new Release("1.0." + index, "R" + index, date, "na",
                snapshot, createTestContent(0), contents);
    }

    public static Content createTestContent(int index) {
        return new Content("http://" + index, "path/" + index, 1366, HASH);
    }

    public static Content createTestPack(int index) {
        return new Content("http://" + index, "path/" + index + ".op1", 1366, HASH);
    }

}

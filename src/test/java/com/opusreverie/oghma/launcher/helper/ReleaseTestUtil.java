package com.opusreverie.oghma.launcher.helper;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.domain.Release;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by keen on 15/02/16.
 */
public class ReleaseTestUtil {

    // SHA256 hash for bytes [1, 2].
    private static final String HASH = "a12871fee210fb8619291eaea194581cbd2531e4b23759d225f6806923f63222";

    public static Release createTestRelease(int contentFileCount) {
        final List<Content> contents = IntStream.range(1, 1 + contentFileCount)
                .mapToObj(ReleaseTestUtil::createTestContent)
                .collect(Collectors.toList());
        return new Release("1.0.0", "One", Calendar.getInstance().getTime(), "na", createTestContent(0), contents);
    }

    public static Content createTestContent(int index) {
        return new Content("http://" + index, "path/" + index, 1366, HASH);
    }

}

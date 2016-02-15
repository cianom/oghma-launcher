package com.opusreverie.oghma.launcher.io.download;

import java.text.MessageFormat;

/**
 * Event specifying progress information for a particular download.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class ProgressEvent {

    private final long downloadedBytes;
    private final long totalBytes;

    public ProgressEvent(long downloadedBytes, long totalBytes) {
        this.downloadedBytes = downloadedBytes;
        this.totalBytes = totalBytes;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public double getPercentage() {
        return (double)downloadedBytes / (double)totalBytes;
    }

    public String getFormattedPercentage() {
        return MessageFormat.format("{0,number,#.##%}", getPercentage());
    }

}

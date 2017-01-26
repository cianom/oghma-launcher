package com.opusreverie.oghma.launcher.io;

import java.text.MessageFormat;

/**
 * Event specifying progress information for a particular install.
 *
 * @author Cian.
 */
public class InstallProgressEvent {

    private final double downloadPercent;
    private final double extractPercent;

    InstallProgressEvent(double downloadPercent, double extractPercent) {
        this.downloadPercent = downloadPercent;
        this.extractPercent = extractPercent;
    }

    public double getPercentage() {
        return (downloadPercent * 0.9) + (extractPercent * 0.1);
    }

    public String getFormattedPercentage() {
        return MessageFormat.format("{0,number,#.##%}", getPercentage());
    }

}

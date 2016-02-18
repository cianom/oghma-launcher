package com.opusreverie.oghma.launcher.io;

import java.text.MessageFormat;

/**
 * Event specifying progress information for a particular install.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class InstallProgressEvent {

    private final double downloadPercent;
    private final double extractPercent;

    public InstallProgressEvent(double downloadPercent, double extractPercent) {
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

package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.common.LauncherException;
import com.opusreverie.oghma.launcher.converter.Decoder;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.ui.component.Notifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Finds releases already installed on the local filesystem.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class ReleaseDirectoryScanner {

    private static final String META_FILENAME = "oghma-{0}.json";
    private static final String RELEASE_FILENAME = "oghma-{0}.jar";

    private final Decoder decoder;

    public ReleaseDirectoryScanner(Decoder decoder) {
        this.decoder = decoder;
    }

    public List<Release> findAvailableReleases(final Path releaseDirectory, final Notifier notifier) throws LauncherException {
        try {
            return Files.walk(releaseDirectory, 1)
                    .filter(this::isRelease)
                    .map(x -> loadRelease(x, notifier))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new LauncherException(e);
        }
    }

    private boolean isRelease(final Path path) {
        boolean release = false;
        final File file = path.toFile();

        if (file.isDirectory()) {
            boolean hasMeta = getReleaseFile(path, META_FILENAME).exists();
            boolean hasBinary = getReleaseFile(path, RELEASE_FILENAME).exists();
            release = hasMeta && hasBinary;
        }
        return release;
    }

    private Release loadRelease(final Path releaseDirectory, final Notifier notifier) {
        Release loaded = null;
        final File metaFile = getReleaseFile(releaseDirectory, META_FILENAME);
        try {
            loaded = decoder.read(metaFile, Release.class);
        }
        catch (IOException e) {
            final String msg = MessageFormat.format("Could not load [{0}], reason: [{1}].", metaFile.getAbsolutePath(), e.getMessage());
            notifier.notify(msg, Notifier.NotificationType.ERROR);
        }
        return loaded;
    }

    private File getReleaseFile(final Path releaseDirectory, final String filePattern) {
        final File dir = releaseDirectory.toFile();
        return new File(dir, MessageFormat.format(filePattern, dir.getName()));
    }

}

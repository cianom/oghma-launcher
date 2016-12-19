package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.common.LauncherException;
import com.opusreverie.oghma.launcher.converter.Decoder;
import com.opusreverie.oghma.launcher.domain.Release;
import com.opusreverie.oghma.launcher.io.file.FileHandler;
import com.opusreverie.oghma.launcher.ui.component.Notifier;
import io.lyra.oghma.common.io.DirectoryResolver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
public class LocalReleaseRepository {


    private final Decoder decoder;

    private final DirectoryResolver dirResolver;

    private final FileHandler fileHandler;

    public LocalReleaseRepository(Decoder decoder, DirectoryResolver dirResolver, FileHandler fileHandler) {
        this.decoder = decoder;
        this.dirResolver = dirResolver;
        this.fileHandler = fileHandler;
    }

    void writeReleaseMeta(final Release release) throws IOException {
        final String identifier = release.getDirectory();

        final Path releaseDir = dirResolver.getReleaseDir(identifier);
        fileHandler.createDirectories(releaseDir);

        final Path outPath = dirResolver.getReleaseMeta(identifier).toPath();
        try (final OutputStream out = fileHandler.getOutputStream(outPath)) {
            decoder.write(out, release);
        }
    }

    /**
     * Delete a downloaded release from disk.
     *
     * @param release the release to delete.
     * @throws IOException if the release could not be deleted from disk.
     */
    public void deleteRelease(final Release release) throws IOException {
        final Path dir = dirResolver.getReleaseDir(release.getDirectory());
        fileHandler.deleteRecursive(dir);
    }

    public List<Release> findAvailableReleases(final Notifier notifier) throws LauncherException {
        try {
            return Files.walk(dirResolver.getReleasesRoot(), 1)
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
            boolean hasMeta = dirResolver.getReleaseMeta(file.getName()).exists();
            boolean hasBinary = dirResolver.getReleaseBinary(file.getName()).exists();
            release = hasMeta && hasBinary;
        }
        return release;
    }

    private Release loadRelease(final Path releaseDirectory, final Notifier notifier) {
        Release loaded = null;
        final File metaFile = dirResolver.getReleaseMeta(releaseDirectory.toFile().getName());
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

package com.opusreverie.oghma.launcher.io.file;

import com.opusreverie.oghma.launcher.common.LauncherException;
import com.opusreverie.oghma.launcher.io.file.DirectoryResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

/**
 * Ensures existence of directory structure required to download and install content.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class FileSystemInitializer {

    private DirectoryResolver dirResolver;

    public FileSystemInitializer(DirectoryResolver dirResolver)
    {
        this.dirResolver = dirResolver;
    }

    public void setUpFileSystemStructure() throws LauncherException {
        dirResolver.getAllRequiredDirectories().forEach(this::safeCreateDir);
    }

    private void safeCreateDir(final Path directory) throws LauncherException {
        try {
            if (!Files.exists(directory)) Files.createDirectories(directory);
        }
        catch (IOException e) {
            throw new LauncherException(MessageFormat.format("Could not create directory [{0}]", directory), e);
        }
    }

}

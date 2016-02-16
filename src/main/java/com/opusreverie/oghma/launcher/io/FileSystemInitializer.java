package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.common.LauncherException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Ensures existence of directory structure required to download and install content.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class FileSystemInitializer {

    public void setUpFileSystemStructure(final Path oghmaAppData) throws LauncherException {
        safeCreateDir(oghmaAppData);
        //TODO compute oghma root based on OS
        //TODO extract these dir names
        Arrays.asList("release", "pack", "schema", "audio").stream()
                .map(oghmaAppData::resolve)
                .forEach(this::safeCreateDir);
    }

    private void safeCreateDir(final Path directory) throws LauncherException {
        try {
            if (!Files.exists(directory)) Files.createDirectory(directory);
        }
        catch (IOException e) {
            throw new LauncherException(MessageFormat.format("Could not create directory [{0}]", directory), e);
        }
    }

}

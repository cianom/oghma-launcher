package com.opusreverie.oghma.launcher.io;

import com.opusreverie.oghma.launcher.common.LauncherException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Created by keen on 27/01/16.
 */
public class FileSystemInitializer {

    public void setUpFileSystemStructure(final Path oghmaAppData) throws LauncherException {
        safeCreateDir(oghmaAppData);

        Arrays.asList("release", "pack", "schema", "audio").stream()
                .map(oghmaAppData::resolve)
                .forEach(this::safeCreateDir);
    }

    private void safeCreateDir(final Path directory) throws LauncherException {
        try {
            if (!Files.exists(directory)) Files.createDirectory(directory);
        }
        catch (IOException e) {
            throw new LauncherException(e);
        }
    }

}

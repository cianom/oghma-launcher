package com.opusreverie.oghma.launcher.io.file;

import com.opusreverie.oghma.launcher.common.ContentType;
import com.opusreverie.oghma.launcher.domain.Content;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * Resolves directories and path for different content operations.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class DirectoryResolver {


    private static final String META_FILENAME = "oghma-{0}.json";
    private static final String RELEASE_FILENAME = "oghma-{0}.jar";
    private static final String INSTALLED_PACK_DIR = "pack/installed/";

    private final Path oghmaRoot;

    public DirectoryResolver(final Path oghmaRoot) {
        this.oghmaRoot = oghmaRoot;
    }

    public DirectoryResolver() {
        Path userHome = Paths.get(System.getProperty("user.home"));
        this.oghmaRoot = userHome.resolve(".oghma");
        //TODO test validity under MAC/Windows
    }

    public boolean isInstalled(final Content file) {
        return getInstalledPath(file).toFile().exists();
    }

    public Path getDownloadPath(final Content file) {
        return oghmaRoot.resolve(file.getPath());
    }

    public Path getInstalledDir() {
        return oghmaRoot.resolve(INSTALLED_PACK_DIR);
    }

    public Path getInstalledPath(final Content file) {
        final Path contentPath = Paths.get(file.getPath());
        final String hash = file.getSha256Hash();
        final String shortHash = hash.substring(0, Math.min(10, hash.length()));
        final String installedFileName = shortHash + "_" + contentPath.getFileName();
        return getInstalledDir().resolve(installedFileName);
    }

    public Path computeExtractPath(final String fileName) {
        return ContentType.fromFilePath(fileName)
                .map(type -> oghmaRoot.resolve(type.getContentPath()))
                .orElse(null);
    }

    public File getReleaseMeta(final String version) {
        final String metaName = MessageFormat.format(META_FILENAME, version);
        return getReleasesRoot().resolve(version).resolve(metaName).toFile();
    }

    public File getReleaseBinary(final String version) {
        final String metaName = MessageFormat.format(RELEASE_FILENAME, version);
        return getReleasesRoot().resolve(version).resolve(metaName).toFile();
    }

    public Path getReleasesRoot() {
        return oghmaRoot.resolve("release/");
    }


}

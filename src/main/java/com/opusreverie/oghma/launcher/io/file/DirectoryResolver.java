package com.opusreverie.oghma.launcher.io.file;

import com.opusreverie.oghma.launcher.common.ContentType;
import com.opusreverie.oghma.launcher.domain.Content;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

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

    private DirectoryResolver(final Path oghmaRoot) {
        this.oghmaRoot = oghmaRoot;
    }

    public static DirectoryResolver ofRoot(final Path root) {
        return new DirectoryResolver(root);
    }

    public static DirectoryResolver create() {
        final String oghmaDirName = ".oghma";
        final Path userHome = Paths.get(System.getProperty("user.home"));
        final String os = System.getProperty("os.name");

        Path oghmaRoot;
        if (os.startsWith("Windows")) {
            oghmaRoot = userHome.resolve("AppData/Roaming/").resolve(oghmaDirName);
            if (!oghmaRoot.toFile().exists()) {
                oghmaRoot = userHome.resolve(oghmaDirName);
            }
        }
        else {
            oghmaRoot = userHome.resolve(oghmaDirName);
        }

        return new DirectoryResolver(oghmaRoot);
    }

    public Path getRoot() {
        return oghmaRoot;
    }

    public List<Path> getAllRequiredDirectories() {
        final List<Path> allRequired = new ArrayList<>(Collections.singletonList(getRoot()));

        EnumSet.allOf(ContentType.class).stream()
                .map(ContentType::getContentPath)
                .map(getRoot()::resolve)
                .forEach(allRequired::add);

        return allRequired;
    }

    public boolean isInstalled(final Content file) {
        return getInstalledPath(file).toFile().exists();
    }

    public Path getDownloadPath(final Content file) {
        return getRoot().resolve(file.getPath());
    }

    public Path getInstalledDir() {
        return getRoot().resolve(INSTALLED_PACK_DIR);
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
                .map(type -> getRoot().resolve(type.getContentPath()))
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
        return getRoot().resolve("release/");
    }


}

package com.opusreverie.oghma.launcher.io.file;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.stream.Collectors;

/**
 * Handler for basic filesystem operations.
 *
 * @author Cian.
 */
public class FileHandler {

    public boolean exists(final Path file) throws SecurityException {
        return Files.exists(file);
    }

    /**
     * Delete recursively. If the file argument is a directory, it will be
     * completely deleted.
     */
    public boolean deleteRecursive(final Path file) throws IOException, SecurityException {
        if (Files.isDirectory(file)) {
            for (Path dirFile : Files.list(file).collect(Collectors.toList())) {
                deleteRecursive(dirFile);
            }
        }
        return delete(file);
    }

    public boolean delete(final Path file) throws SecurityException {
        return file.toFile().delete();
    }

    public Path write(final Path file, final byte[] data) throws IOException {
        return Files.write(file, data);
    }

    public Path move(final Path source, final Path target, final CopyOption... options) throws IOException {
        return Files.move(source, target, options);
    }

    public Path createFile(final Path path, FileAttribute<?>... attrs) throws IOException {
        return Files.createFile(path, attrs);
    }

    public Path createDirectories(final Path dir, final FileAttribute<?>... attrs) throws IOException {
        return Files.createDirectories(dir, attrs);
    }

    public InputStream getInputStream(final Path file) throws IOException {
        return new FileInputStream(file.toFile());
    }

    public boolean deleteIfExists(final Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    public OutputStream getOutputStream(final Path file) throws IOException {
        return new FileOutputStream(file.toFile());
    }

    public byte[] readAllBytes(final Path path) throws IOException {
        return Files.readAllBytes(path);
    }

}

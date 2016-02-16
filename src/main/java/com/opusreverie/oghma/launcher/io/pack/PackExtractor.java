package com.opusreverie.oghma.launcher.io.pack;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.io.FileHandler;
import com.opusreverie.oghma.launcher.io.download.ProgressEvent;
import com.opusreverie.oghma.launcher.io.file.DirectoryResolver;
import rx.Observable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extracts pack contents into their relevant filesystem directories.
 * <p/>
 * The extractor first extracts all content into memory, and then sequentially writes each file to disk.
 * If a failure is encountered at any point, all of the installed files should be rolled back.
 * <p/>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class PackExtractor {


    private final FileHandler fileHandler;

    private final DirectoryResolver dirResolver;

    public PackExtractor(final FileHandler fileHandler, final DirectoryResolver dirResolver) {
        this.fileHandler = fileHandler;
        this.dirResolver = dirResolver;
    }

    public PackExtractor(final Path oghmaRoot) {
        this(new FileHandler(), new DirectoryResolver(oghmaRoot));
    }

    public Observable<ProgressEvent> extract(final Content packFile) {
        return Observable.create(subscriber -> {
            try {
                final Path outPath = dirResolver.getDownloadPath(packFile);

                final List<ExtractContent> extracted = extractInMemory(outPath);

                writeFilesToDisk(extracted);

                markInstalled(packFile);

                subscriber.onCompleted();
            }
            catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private List<ExtractContent> extractInMemory(final Path packFile) throws IOException {
        final List<ExtractContent> content = new ArrayList<>();

        try (final ZipInputStream zis = new ZipInputStream(fileHandler.getInputStream(packFile))) {
            ZipEntry packEntry;
            while ((packEntry = zis.getNextEntry()) != null) {
                final String fileName = packEntry.getName();
                final Path extractPath = dirResolver.computeExtractPath(fileName);

                if (extractPath != null) {
                    ensureDirectoryStructure(extractPath);

                    content.add(extractedContentFromInStream(zis, extractPath));
                }
            }
        }
        return content;
    }

    private List<ExtractContent> writeFilesToDisk(final List<ExtractContent> content) throws IOException {
        final List<ExtractContent> created = new ArrayList<>();
        for (ExtractContent c : content) {
            try {
                if (writeFileToDisk(c)) created.add(c);
            } catch (IOException e) {
                created.stream().map(x -> x.destExtractPath).forEach(fileHandler::delete);
                throw e;
            }
        }
        return created;
    }

    protected boolean writeFileToDisk(final ExtractContent content) throws IOException {
        if (!fileHandler.exists(content.destExtractPath)) {
            fileHandler.write(content.destExtractPath, content.fileData);
            return true;
        }
        return false;
    }

    private ExtractContent extractedContentFromInStream(final InputStream in, final Path extractPath) throws IOException {
        ExtractContent retVal;
        final byte[] buffer = new byte[8192];
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            retVal = new ExtractContent(extractPath, out.toByteArray());
        }
        return retVal;
    }

    private void markInstalled(final Content packFile) throws IOException {
        final Path source = dirResolver.getDownloadPath(packFile);
        final Path target = dirResolver.getInstalledPath(packFile);
        fileHandler.move(source, target);
    }

    private void ensureDirectoryStructure(final Path extractPath) throws IOException {
        fileHandler.createDirectories(extractPath);
    }

    static class ExtractContent {
        private final Path destExtractPath;
        private final byte[] fileData;

        public ExtractContent(Path destExtractPath, byte[] fileData) {
            this.destExtractPath = destExtractPath;
            this.fileData = fileData;
        }
    }

}

package com.opusreverie.oghma.launcher.io.pack;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.io.FileHandler;
import com.opusreverie.oghma.launcher.io.download.ProgressEvent;
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
 * Created by keen on 11/02/16.
 */
public class PackExtractor {

    private final Path oghmaRoot;

    private final FileHandler fileHandler;

    public PackExtractor(final Path oghmaRoot, final FileHandler fileHandler) {
        this.oghmaRoot = oghmaRoot;
        this.fileHandler = fileHandler;
    }

    public PackExtractor(final Path oghmaRoot) {
        this(oghmaRoot, new FileHandler());
    }

    public Observable<ProgressEvent> extract(final Content packFile) {
        return Observable.create(subscriber -> {
            try {
                final Path outPath = oghmaRoot.resolve(packFile.getPath());

                final List<ExtractContent> extracted = extractInMemory(outPath);

                writeFilesToDisk(extracted);

                markInstalled(outPath);

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
                final Path extractPath = computeExtractPath(oghmaRoot, fileName);

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

    private void markInstalled(final Path packFile) throws IOException {
        final Path installedDest = oghmaRoot.resolve("pack/installed/").resolve(packFile.getFileName());
        fileHandler.move(packFile, installedDest);
    }

    private void ensureDirectoryStructure(final Path extractPath) throws IOException {
        fileHandler.createDirectories(extractPath);
    }

    private Path computeExtractPath(final Path oghmaRoot, final String fileName) {
        final String contentType = getFirstTwoFileExtChars(fileName);
        if (contentType != null) {
            switch (contentType) {
                case "sf":
                    return oghmaRoot.resolve("schema/form");
                case "sm":
                    return oghmaRoot.resolve("schema/material");
                case "sc":
                    return oghmaRoot.resolve("schema/climate");
                case "si":
                    return oghmaRoot.resolve("schema/item");
                case "sr":
                    return oghmaRoot.resolve("schema/flora");
                case "sg":
                    return oghmaRoot.resolve("schema/grass");
                case "am":
                    return oghmaRoot.resolve("audio/music");
                case "an":
                    return oghmaRoot.resolve("audio/environment");
                case "oe":
                    return oghmaRoot.resolve("audio/effect");
                case "gu":
                    return oghmaRoot.resolve("graphic/ui");
                case "gg":
                    return oghmaRoot.resolve("graphic/game");
                case "gs":
                    return oghmaRoot.resolve("graphic/shaders");
                default:
                    return null;
            }
        }
        return null;
    }

    private String getFirstTwoFileExtChars(final String fileName) {
        String extChars = null;
        final int i = fileName.lastIndexOf('.');
        if (i > 0 && fileName.length() - i >= 3) {
            extChars = fileName.substring(i + 1, i + 3);
        }
        return extChars;
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

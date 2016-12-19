package com.opusreverie.oghma.launcher.io.download;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.io.file.FileHandler;
import io.lyra.oghma.common.io.DirectoryResolver;
import org.apache.commons.codec.digest.DigestUtils;
import rx.Observable;
import rx.Subscriber;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * Downloads a specified file and performs hash validation once complete.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class FileDownloader {

    private static final int BUFFER_SIZE = 1024 * 16;

    private final DirectoryResolver dirResolver;

    private final FileHandler fileHandler;

    public FileDownloader(final DirectoryResolver dirResolver) {
        this(dirResolver, new FileHandler());
    }

    public FileDownloader(final DirectoryResolver dirResolver, final FileHandler fileHandler) {
        this.dirResolver = dirResolver;
        this.fileHandler = fileHandler;
    }

    /**
     * Download a particular file and validate hash once complete.
     *
     * @param file the file to download.
     * @return a stream of progress events.
     */
    public Observable<DownloadProgressEvent> downloadFile(final Content file) {
        return Observable.<DownloadProgressEvent>create(subscriber -> {
            try {
                long totalBytes = file.getSizeBytes();
                final String url = file.getUrl();

                final Path outPath = dirResolver.resolveRelativeRoot(file.getPath());
                if (lengthDiffers(totalBytes, outPath) || hashDiffers(file)) {

                    fileHandler.deleteIfExists(outPath);
                    fileHandler.createDirectories(outPath.getParent());

                    final Client httpClient = ClientBuilder.newClient();

                    try (final InputStream httpIn = retrieveHttpResponseStream(httpClient, url);
                         final OutputStream fileOut = fileHandler.getOutputStream(outPath)) {

                        transferStreams(httpIn, fileOut, subscriber, totalBytes);
                    }
                    finally {
                        httpClient.close();
                    }

                    // Hash check
                    validateHash(file);
                }

            }
            catch (IOException | ProcessingException | NoSuchAlgorithmException | IllegalStateException ex) {
                subscriber.onError(ex);
            }

            //TODO consider cleaning up file is unsubscribed.

            subscriber.onCompleted();

        }).share();
    }

    private void transferStreams(final InputStream in, final OutputStream out, final Subscriber<? super DownloadProgressEvent> subscriber,
                                 final long totalExpectedBytes) throws IOException {
        int read;
        final byte[] buffer = new byte[BUFFER_SIZE];
        long downloadedBytes = 0;
        while ((read = in.read(buffer)) != -1 && !subscriber.isUnsubscribed()) {
            downloadedBytes += read;
            if (downloadedBytes > totalExpectedBytes) {
                throw new IllegalStateException("Downloaded more bytes than expected");
            }
            out.write(buffer, 0, read);
            subscriber.onNext(new DownloadProgressEvent(downloadedBytes));
        }
    }

    private void validateHash(final Content file) throws IOException, NoSuchAlgorithmException, IllegalStateException {
        final byte[] data = fileHandler.readAllBytes(dirResolver.resolveRelativeRoot(file.getPath()));
        final String digest = DigestUtils.sha256Hex(data);

        if (!Objects.equals(digest, file.getSha256Hash())) {
            final String template = "Hash check of file {0} failed. Expected [{1}] Actual [{2}].";
            throw new IllegalStateException(MessageFormat.format(template, file.getPath(), file.getSha256Hash(), digest));
        }
    }

    private boolean hashDiffers(final Content file) {
        boolean differs = false;
        try {
            validateHash(file);
        }
        catch (Exception e) {
            differs = true;
        }
        return differs;
    }

    private boolean lengthDiffers(final long expected, final Path file) {
        long actual = file.toFile().length();
        return expected != actual;
    }

    protected InputStream retrieveHttpResponseStream(final Client httpClient, final String url) throws IOException {
        final WebTarget target = httpClient.target(url);

        return target.request().get(InputStream.class);
    }

    /**
     * Event specifying progress information for a particular download.
     * <p>
     * Copyright © 2016 Cian O'Mahony. All rights reserved.
     *
     * @author Cian O'Mahony
     */
    public static class DownloadProgressEvent {

        private final long downloadedBytes;

        public DownloadProgressEvent(long downloadedBytes) {
            this.downloadedBytes = downloadedBytes;
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

    }


}

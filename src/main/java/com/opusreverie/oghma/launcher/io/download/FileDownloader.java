package com.opusreverie.oghma.launcher.io.download;

import com.opusreverie.oghma.launcher.domain.Content;
import com.opusreverie.oghma.launcher.io.FileHandler;
import com.opusreverie.oghma.launcher.io.file.DirectoryResolver;
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

    private final DirectoryResolver dirResolver;

    private final FileHandler fileHandler;

    public FileDownloader(final Path oghmaRoot) {
        this(new DirectoryResolver(oghmaRoot), new FileHandler());
    }

    public FileDownloader(final DirectoryResolver dirResolver, final FileHandler fileHandler) {
        this.dirResolver = dirResolver;
        this.fileHandler = fileHandler;
    }

    public Observable<ProgressEvent> downloadFile(final Content file) {
        return Observable.<ProgressEvent>create(subscriber -> {
            try {
                long totalBytes = file.getSizeBytes();
                final String url = file.getUrl();

                final Path outPath = dirResolver.getDownloadPath(file);
                boolean installed = dirResolver.getInstalledPath(file).toFile().exists();
                if (!installed && (lengthDiffers(totalBytes, outPath) || hashDiffers(file))) {

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

            } catch (IOException | ProcessingException | NoSuchAlgorithmException | IllegalStateException ex) {
                subscriber.onError(ex);
            }
            subscriber.onCompleted();

        }).share();
    }

    private void transferStreams(InputStream in, OutputStream out, Subscriber<? super ProgressEvent> subscriber, long totalExpectedBytes) throws IOException {
        int read;
        final byte[] buffer = new byte[4096];
        long downloadedBytes = 0;

        while ((read = in.read(buffer)) != -1) {
            downloadedBytes += read;
            if (downloadedBytes > totalExpectedBytes) {
                throw new IllegalStateException("Downloaded more bytes than expected");
            }
            out.write(buffer, 0, read);
            subscriber.onNext(new ProgressEvent(downloadedBytes, totalExpectedBytes));
        }
    }

    private void validateHash(final Content file) throws IOException, NoSuchAlgorithmException, IllegalStateException {
        final byte[] data = fileHandler.readAllBytes(dirResolver.getDownloadPath(file));
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
        } catch (Exception e) {
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

}

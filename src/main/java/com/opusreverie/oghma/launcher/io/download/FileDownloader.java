package com.opusreverie.oghma.launcher.io.download;

import com.opusreverie.oghma.launcher.domain.Content;
import org.apache.commons.codec.digest.DigestUtils;
import rx.Observable;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

    private final Path oghmaRoot;

    public FileDownloader(final Path oghmaRoot) {
        this.oghmaRoot = oghmaRoot;
    }

    public Observable<ProgressEvent> downloadFile(final Content file) {
        return Observable.<ProgressEvent>create(subscriber -> {
            try {
                long totalBytes = file.getSizeBytes();
                final String url = file.getUrl();

                final Path outPath = oghmaRoot.resolve(file.getPath());
                if (!lengthEquals(totalBytes, outPath) || !hashEquals(file)) {

                    Files.deleteIfExists(outPath);
                    Files.createDirectories(outPath.getParent());

                    final WebTarget target = ClientBuilder.newClient().target(url);

                    try (final InputStream in = target.request().get(InputStream.class);
                         final FileOutputStream fos = new FileOutputStream(outPath.toFile())) {

                        int read;
                        final byte[] buffer = new byte[4096];
                        long downloadedBytes = 0;

                        while ((read = in.read(buffer)) != -1) {
                            downloadedBytes += read;
                            if (downloadedBytes > totalBytes) {
                                throw new IllegalStateException("Downloaded more bytes than expected");
                            }
                            fos.write(buffer, 0, read);
                            subscriber.onNext(new ProgressEvent(downloadedBytes, totalBytes));
                        }
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

    private void validateHash(final Content file) throws IOException, NoSuchAlgorithmException, IllegalStateException {
        byte[] data = Files.readAllBytes(oghmaRoot.resolve(file.getPath()));
        final String digest = DigestUtils.sha256Hex(data);

        if (!Objects.equals(digest, file.getSha256Hash())) {
            final String template = "Hash check of file {0} failed. Expected [{1}] Actual [{2}].";
            throw new IllegalStateException(MessageFormat.format(template, file.getPath(), file.getSha256Hash(), digest));
        }
    }

    private boolean hashEquals(final Content file) {
        boolean equals = true;
        try {
            validateHash(file);
        } catch (Exception e) {
            equals = false;
        }
        return equals;
    }

    private boolean lengthEquals(final long expected, final Path file) {
        long actual = file.toFile().length();
        return expected == actual;
    }

}

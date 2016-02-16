package com.opusreverie.oghma.launcher.helper;

import com.opusreverie.oghma.launcher.common.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by keen on 15/02/16.
 */
public class PackTestUtil {


    public static InputStream createPackAsZipStream() {
        return new ZipInputStream(createPackAsStream());
    }

    public static InputStream createPackAsStream() {
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

        try (ZipOutputStream out = new ZipOutputStream(bytesOut)) {
            for (ContentType contentType : ContentType.values()) {
                final ZipEntry entry = new ZipEntry("test." + contentType.getExtension() + "1");
                out.putNextEntry(entry);
                out.write(new byte[] { 1, 2});
            }
            out.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(bytesOut.toByteArray());
    }


}

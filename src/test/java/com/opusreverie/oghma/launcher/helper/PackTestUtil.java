package com.opusreverie.oghma.launcher.helper;

import com.opusreverie.oghma.launcher.common.ContentType;

import java.io.*;
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

        createPackToOutStream(bytesOut);

        return new ByteArrayInputStream(bytesOut.toByteArray());
    }

    public static void createPackToOutStream(final OutputStream out) {
        try (final ZipOutputStream zout = new ZipOutputStream(out)) {
            for (ContentType contentType : ContentType.packTypes()) {
                final ZipEntry entry = new ZipEntry("test." + contentType.getExtension() + "1");
                zout.putNextEntry(entry);
                zout.write(new byte[]{1, 2});
            }
            zout.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        createPackToOutStream(new FileOutputStream(new File("/tmp/sample_pack.op1")));
    }

}

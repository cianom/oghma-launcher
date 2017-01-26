package com.opusreverie.oghma.launcher.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * JSON decoder that de-serializes incoming JSON streams into objects.
 *
 * @author Cian.
 */
public class Decoder {

    private final ObjectMapper mapper;

    public Decoder() {
        mapper = new ObjectMapper();
    }

    public <T> T read(final File in, final Class<T> clazz) throws IOException {
        return read(new FileInputStream(in), clazz);
    }

    public <T> T read(final InputStream in, final Class<T> clazz) throws IOException {
        return mapper.readValue(in, clazz);
    }

    public <T> void write(final OutputStream out, final T obj) throws IOException {
        mapper.writeValue(out, obj);
    }

    public <T> String writeToString(final T obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }


}

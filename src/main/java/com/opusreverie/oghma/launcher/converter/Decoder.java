package com.opusreverie.oghma.launcher.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * JSON decoder that de-serializes incoming JSON streams into objects.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class Decoder {

    final private ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally


    public <T> T read(final File in, Class<T> clazz) throws IOException {
        return read(new FileInputStream(in), clazz);
    }

    public <T> T read(final InputStream in, Class<T> clazz) throws IOException {
        return mapper.readValue(in, clazz);
    }

    public <T> void write(final OutputStream out, T obj) throws IOException {
        mapper.writeValue(out, obj);
    }

    public <T> String writeToString(T obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }


}

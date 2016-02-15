package com.opusreverie.oghma.launcher.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by keen on 27/01/16.
 */
public class Decoder {

    final private ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally


    public <T> T read(final File in, Class<T> clazz) throws IOException {
        return read(new FileInputStream(in), clazz);
    }

    public <T> T read(final InputStream in, Class<T> clazz) throws IOException {
        return mapper.readValue(in, clazz);
    }

}

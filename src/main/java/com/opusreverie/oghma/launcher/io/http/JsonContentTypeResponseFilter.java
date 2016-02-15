package com.opusreverie.oghma.launcher.io.http;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by keen on 02/02/16.
 */
public class JsonContentTypeResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        List<String> contentType = new ArrayList<>(1);
        contentType.add(MediaType.APPLICATION_JSON);
        responseContext.getHeaders().put("Content-Type", contentType);
    }

}

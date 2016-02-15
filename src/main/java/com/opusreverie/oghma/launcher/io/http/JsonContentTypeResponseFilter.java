package com.opusreverie.oghma.launcher.io.http;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter that sets the {@value CONTENT_TYPE} HTML response header to application/json.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class JsonContentTypeResponseFilter implements ClientResponseFilter
{

    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
    {
        final List<String> contentType = new ArrayList<>(1);
        contentType.add(MediaType.APPLICATION_JSON);
        responseContext.getHeaders().put(CONTENT_TYPE, contentType);
    }

}

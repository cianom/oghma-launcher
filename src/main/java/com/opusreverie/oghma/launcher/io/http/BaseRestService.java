package com.opusreverie.oghma.launcher.io.http;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

/**
 * Retrieves latest release information from remote hosted services.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class BaseRestService {

    private static final int INITIAL_BACKOFF_MILLIS = 3_000;
    private static final int MAX_BACKOFF_MILLIS = 20_000;

    private final String host;

    protected BaseRestService(final String host) {
        this.host = host;
    }

    protected <T> T get(final Class<T> clazz, final String uri) {
        return request(uri).get(clazz);
    }

    protected <T> T get(final GenericType<T> type, final String uri) {
        return request(uri).get(type);
    }

    private Invocation.Builder request(final String uri) {
        final String url = MessageFormat.format("http://{0}", host);

        final Client client = ClientBuilder.newBuilder()
                .property(ClientProperties.CONNECT_TIMEOUT, 5000)
                .property(ClientProperties.READ_TIMEOUT, 5000)
                .register(JacksonJsonProvider.class)
                .register(JsonContentTypeResponseFilter.class).build();
        final WebTarget target = client.target(url).path(uri);
        return target.request(MediaType.APPLICATION_JSON);
    }

    protected <T> CompletableFuture<T> getWithRetry(final GenericType<T> type, final String uri, final long delayMillis) {
        return CompletableFuture.supplyAsync(() -> getWithDelay(type, uri, delayMillis))
                .handle((list, ex) -> {
                    if (ex == null) {
                        return CompletableFuture.completedFuture(list);
                    } else {
                        System.out.println(ex.getMessage());
                        long backOff = Math.min(MAX_BACKOFF_MILLIS, Math.max(INITIAL_BACKOFF_MILLIS, delayMillis + 1000));
                        return getWithRetry(type, uri, backOff);
                    }
                })
                .thenCompose(x -> x);
    }

    protected <T> T getWithDelay(final GenericType<T> type, final String uri, final long delayMillis) {
        try {
            if (delayMillis > 0) {
                Thread.sleep(delayMillis);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return get(type, uri);
    }

}

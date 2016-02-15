package com.opusreverie.oghma.launcher.io.http;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.opusreverie.oghma.launcher.domain.Release;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Retrieves latest release information from remote hosted services.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class ReleaseService {

    private static final int INITIAL_BACKOFF_MILLIS = 3_000;
    private static final int MAX_BACKOFF_MILLIS = 20_000;

    private final String host;

    public ReleaseService(final String host) {
        this.host = host;
    }

    private List<Release> getReleases() {
        final String url = MessageFormat.format("http://{0}", host);

        final Client client = ClientBuilder.newBuilder()
                .property(ClientProperties.CONNECT_TIMEOUT, 5000)
                .property(ClientProperties.READ_TIMEOUT, 5000)
                .register(JacksonJsonProvider.class)
                .register(JsonContentTypeResponseFilter.class).build();
        final WebTarget target = client.target(url).path("release");
        final GenericType<List<Release>> type = new GenericType<List<Release>>(){};
        return target.request(MediaType.APPLICATION_JSON).get(type);
    }


    public CompletableFuture<List<Release>> getReleasesWithRetry() {
        return getReleasesWithRetry(0);
    }

    private CompletableFuture<List<Release>> getReleasesWithRetry(final long delayMillis) {
        return CompletableFuture.supplyAsync(() -> getReleasesWithDelay(delayMillis))
                .handle((list, ex) -> {
                    if (ex == null) {
                        return CompletableFuture.completedFuture(list);
                    } else {
                        System.out.println(ex.getMessage()); //TODO LOg OR NOTIFY
                        long backOff = Math.min(MAX_BACKOFF_MILLIS, Math.max(INITIAL_BACKOFF_MILLIS, delayMillis + 1000));
                        return getReleasesWithRetry(backOff);
                    }
                })
                .thenCompose(x -> x);
    }

    private List<Release> getReleasesWithDelay(final long delayMillis) {
        try {
            if (delayMillis > 0) {
                Thread.sleep(delayMillis);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getReleases();
    }

}

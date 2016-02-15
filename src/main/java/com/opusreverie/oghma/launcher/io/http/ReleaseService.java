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
 * Created by keen on 02/02/16.
 */
public class ReleaseService {

    private static final int INITIAL_BACKOFF_MILLIS = 3_000;
    private static final int MAX_BACKOFF_MILLIS = 20_000;

    private final String host;

    public ReleaseService(final String host) {
        this.host = host;
    }

    public List<Release> getReleases() {
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

    private List<Release> getReleasesWithDelay(final long delayMillis) {

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getReleases();
    }

    public CompletableFuture<List<Release>> getReleasesWithRetry(final long delayMillis) {
        return CompletableFuture.supplyAsync(() -> getReleasesWithDelay(delayMillis))
                .handle((list, ex) -> {
                    if (ex == null) {
                        return CompletableFuture.completedFuture(list);
                    } else {
                        System.out.println(ex.getMessage());
                        long backoff = Math.min(MAX_BACKOFF_MILLIS, Math.max(INITIAL_BACKOFF_MILLIS, delayMillis + 1000));
                        return getReleasesWithRetry(backoff);
                    }
                })
                .thenCompose(x -> x);
    }

}

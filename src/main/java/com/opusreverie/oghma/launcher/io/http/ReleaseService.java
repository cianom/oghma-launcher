package com.opusreverie.oghma.launcher.io.http;

import com.opusreverie.oghma.launcher.domain.Release;

import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Retrieves latest release information from remote hosted services.
 *
 * @author Cian.
 */
public class ReleaseService extends BaseRestService {


    public ReleaseService(final String host) {
        super(host);
    }

    public CompletableFuture<List<Release>> getReleasesWithRetry() {
        return getWithRetry(new GenericType<List<Release>>() {}, "release", 0);
    }


}

package com.opusreverie.oghma.launcher.io.http;

import com.opusreverie.oghma.launcher.domain.Release;

import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Retrieves latest release information from remote hosted services.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class ReleaseService extends BaseRestService {


    public ReleaseService(final String host) {
        super(host);
    }

    public CompletableFuture<List<Release>> getReleasesWithRetry() {
        return getWithRetry(new GenericType<List<Release>>() {}, "release", 0);
    }


}

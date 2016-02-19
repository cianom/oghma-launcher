package com.opusreverie.oghma.launcher.io.http;

import com.opusreverie.oghma.launcher.domain.LauncherVersion;

import javax.ws.rs.core.GenericType;
import java.util.concurrent.CompletableFuture;

/**
 * Retrieves latest launcher version information from remote hosted services.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class LauncherVersionService extends BaseRestService {

    public LauncherVersionService(String host) {
        super(host);
    }

    public CompletableFuture<LauncherVersion> getLatestVersionWithRetry() {
        return getWithRetry(new GenericType<LauncherVersion>() {}, "launcher/latest/version", 0);
    }

}

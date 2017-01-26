package com.opusreverie.oghma.launcher.io.http;

import com.opusreverie.oghma.launcher.domain.LauncherVersion;

import javax.ws.rs.core.GenericType;
import java.util.concurrent.CompletableFuture;

/**
 * Retrieves latest launcher version information from remote hosted services.
 *
 * @author Cian.
 */
public class LauncherVersionService extends BaseRestService {

    public LauncherVersionService(String host) {
        super(host);
    }

    public CompletableFuture<LauncherVersion> getLatestVersionWithRetry() {
        return getWithRetry(new GenericType<LauncherVersion>() {}, "launcher/version", 0);
    }

}

package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Information about a remote file that's available for download.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class Content {

    @JsonProperty("url")
    private final String url;

    @JsonProperty("path")
    private final String path;

    @JsonProperty("size")
    private final long sizeBytes;

    @JsonProperty("hash")
    private final String sha256Hash;

    @JsonCreator
    public Content(@JsonProperty("url") String url, @JsonProperty("path") String path,
                   @JsonProperty("size") long sizeBytes, @JsonProperty("hash") String sha256Hash) {
        this.url = url;
        this.path = path;
        this.sizeBytes = sizeBytes;
        this.sha256Hash = sha256Hash;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256Hash() {
        return sha256Hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return sizeBytes == content.sizeBytes &&
                Objects.equals(path, content.path) &&
                Objects.equals(sha256Hash, content.sha256Hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, sizeBytes, sha256Hash);
    }
}

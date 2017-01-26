package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Information about a remote file that's available for download.
 *
 * @author Cian.
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
    public Content(@JsonProperty("url") final String url, @JsonProperty("path") final String path,
                   @JsonProperty("size") final long sizeBytes, @JsonProperty("hash") final String sha256Hash) {
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
    public boolean equals(final Object o) {
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

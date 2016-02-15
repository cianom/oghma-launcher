package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by keen on 14/01/16.
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

}

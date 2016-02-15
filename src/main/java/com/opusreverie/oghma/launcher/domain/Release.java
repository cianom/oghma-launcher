package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Information on an available binary release and its related content.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class Release {

    @JsonProperty("version")
    private final String version;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("releasedOn")
    private final Date releasedOn;

    @JsonProperty("oghon")
    private final String oghon;

    @JsonProperty("binary")
    private final Content binary;

    @JsonProperty("content")
    private final List<Content> content;

    @JsonCreator
    public Release(@JsonProperty("version") String version, @JsonProperty("name") String name,
                   @JsonProperty("releasedOn") Date releasedOn, @JsonProperty("oghon") String oghon,
                   @JsonProperty("binary") Content binary, @JsonProperty("content") List<Content> content) {
        Objects.requireNonNull(version);
        Objects.requireNonNull(name);
        Objects.requireNonNull(releasedOn);
        Objects.requireNonNull(oghon);
        Objects.requireNonNull(binary);
        Objects.requireNonNull(content);
        this.version = version;
        this.name = name;
        this.releasedOn = releasedOn;
        this.oghon = oghon;
        this.binary = binary;
        this.content = content;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Date getReleasedOn() {
        return releasedOn;
    }

    public String getOghon() {
        return oghon;
    }

    public List<Content> getBinaryAndContent() {
        final List<Content> all = new ArrayList<>(getContent());
        all.add(getBinary());
        return all;
    }

    public Content getBinary() {
        return binary;
    }

    public List<Content> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return version + " " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Release release = (Release) o;
        return Objects.equals(version, release.version) &&
                Objects.equals(name, release.name) &&
                Objects.equals(releasedOn, release.releasedOn) &&
                Objects.equals(oghon, release.oghon) &&
                Objects.equals(binary, release.binary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, name, releasedOn, oghon, binary);
    }
}
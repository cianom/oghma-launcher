package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Information on an available binary release and its related content.
 *
 * @author Cian.
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

    @JsonProperty("snapshot")
    private final boolean snapshot;

    @JsonProperty("content")
    private final List<Content> content;

    @JsonCreator
    public Release(@JsonProperty("version") String version, @JsonProperty("name") String name,
                   @JsonProperty("releasedOn") Date releasedOn, @JsonProperty("oghon") String oghon,
                   @JsonProperty("snapshot") boolean snapshot, @JsonProperty("binary") Content binary,
                   @JsonProperty("content") List<Content> content) {
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
        this.snapshot = snapshot;
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

    public boolean isSnapshot() {
        return snapshot;
    }

    @JsonIgnore
    public List<Content> getBinaryAndContent() {
        final List<Content> all = new ArrayList<>(getContent());
        all.add(getBinary());
        return all;
    }

    @JsonIgnore
    public String getDirectory() {
        return snapshot ? name.toLowerCase() : getConcreteVersion();
    }

    public boolean isExpiredSnapshot(final Release newer) {
        return isSameVersion(newer) && !equals(newer);
    }

    private boolean isSameVersion(final Release other) {
        return getVersion().equalsIgnoreCase(other.getVersion());
    }

    Content getBinary() {
        return binary;
    }

    public List<Content> getContent() {
        return content;
    }

    @JsonIgnore
    public String getConcreteVersion() {
        return getVersion();
    }

    @Override
    public String toString() {
        final String versionString = (snapshot) ? "" : (version + " ");
        return versionString + name + (snapshot ? " (rolling)" : "");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Release release = (Release) o;
        return Objects.equals(version, release.version) &&
                Objects.equals(name, release.name) &&
                Objects.equals(releasedOn, release.releasedOn) &&
                Objects.equals(oghon, release.oghon) &&
                Objects.equals(binary, release.binary) &&
                Objects.equals(content, release.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, name, releasedOn, oghon, binary, content);
    }

    /**
     * Ensures the returned object is a concrete release,
     * and not a symbolic pointer release (eg. latest-stable).
     *
     * @return the normalized release.
     */
    public Release toConcrete() {
        return this;
    }

}

package com.opusreverie.oghma.launcher.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.lyra.oghma.common.content.SemanticVersion;

/**
 * Information on the latest launcher version.
 *
 * @author Cian.
 */
public class LauncherVersion {


    @JsonProperty("minimumVersion")
    private final String minimumVersion;

    @JsonProperty("recommendedVersion")
    private final String recommendedVersion;

    @JsonCreator
    public LauncherVersion(@JsonProperty("minimumVersion") final String minimumVersion,
                           @JsonProperty("recommendedVersion") final String recommendedVersion) {
        this.minimumVersion = minimumVersion;
        this.recommendedVersion = recommendedVersion;
    }

    public String getMinimumVersion() {
        return minimumVersion;
    }

    public String getRecommendedVersion() {
        return recommendedVersion;
    }

    public SemanticVersion getSemanticMinimum() {
        return new SemanticVersion(minimumVersion);
    }

    public SemanticVersion getSemanticRecommended() {
        return new SemanticVersion(recommendedVersion);
    }

}

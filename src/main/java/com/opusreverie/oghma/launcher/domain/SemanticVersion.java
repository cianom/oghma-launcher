package com.opusreverie.oghma.launcher.domain;

/**
 * Slim wrapper around a semantic version string. Provides support for comparing.
 *
 * @author Cian.
 */
public class SemanticVersion implements Comparable<SemanticVersion> {

    private final String version;

    public SemanticVersion(String version) {
        this.version = version;
    }

    /**
     * Compares two version strings.
     * <p/>
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     * <p/>
     * Note: It does not work if "1.10" is supposed to be equal to "1.10.0".
     *
     * @param v1 the first version to compare against.
     * @param v2 the second version to compare against.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     * The result is a positive integer if str1 is _numerically_ greater than str2.
     * The result is zero if the strings are _numerically_ equal.
     */
    private static int compareTo(final SemanticVersion v1, final SemanticVersion v2) {

        final String[] vals1 = v1.getVersion().split("\\.");
        final String[] vals2 = v2.getVersion().split("\\.");
        int i = 0;
        // Set index to first non-equal ordinal or length of shortest version string.
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // Compare first non-equal ordinal number.
        if (i < vals1.length && i < vals2.length) {
            final int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // The strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int compareTo(SemanticVersion o) {
        return SemanticVersion.compareTo(this, o);
    }
}

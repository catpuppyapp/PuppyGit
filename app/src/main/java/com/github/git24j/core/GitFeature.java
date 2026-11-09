package com.github.git24j.core;

import java.util.EnumSet;

/** Combinations of these values describe the features with which libgit2 was compiled */
public enum GitFeature {
    /** If set, libgit2 was built thread-aware and can be safely used from multiple threads. */
    THTREADS(1 << 0),
    /**
     * If set, libgit2 was built with and linked against a TLS implementation. Custom TLS streams
     * may still be added by the user to support HTTPS regardless of this.
     */
    HTTPS(1 << 1),
    /**
     * If set, libgit2 was built with and linked against libssh2. A custom transport may still be
     * added by the user to support libssh2 regardless of this.
     */
    SSH(1 << 2),
    /**
     * If set, libgit2 was built with support for sub-second resolution in file modification times.
     */
    NSEC(1 << 3);

    public final int code;

    GitFeature(int code) {
        this.code = code;
    }

    private static void addIfMask(EnumSet<GitFeature> set, int maskedCode, GitFeature feature) {
        if ((maskedCode & feature.code) != 0) {
            set.add(feature);
        }
    }

    static EnumSet<GitFeature> valuesOf(int maskedCode) {
        EnumSet<GitFeature> set = EnumSet.noneOf(GitFeature.class);
        addIfMask(set, maskedCode, THTREADS);
        addIfMask(set, maskedCode, HTTPS);
        addIfMask(set, maskedCode, SSH);
        addIfMask(set, maskedCode, NSEC);
        return set;
    }
}

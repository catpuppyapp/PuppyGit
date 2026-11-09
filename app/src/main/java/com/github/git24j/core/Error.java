package com.github.git24j.core;

/** Delegate git_error_* methods. */
public class Error {
    /**
     * deprecated by libgit2 1.8.x
     */
    @Deprecated
    static native void jniClear();

    static native GitException jniLast();

    /**
     * deprecated by libgit2 1.8.x
     * @param klass
     * @param message
     */
    @Deprecated
    static native void jniSetStr(int klass, String message);

    /**
     * Helper to throw last GitException if error code is not zero
     *
     * @param error 0 or an error code
     * @throws GitException if there was an error recorded
     */
    public static void throwIfNeeded(int error) {
        if (error < 0) {
            GitException e = jniLast();
            if (e != null) {
                e.setCode(error);
                throw e;
            }
        }
    }
}

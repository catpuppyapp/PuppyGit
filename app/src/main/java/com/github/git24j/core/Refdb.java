package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public class Refdb extends CAutoReleasable {
    /** int git_refdb_compress(git_refdb *refdb); */
    static native int jniCompress(long refdb);

    /** void git_refdb_free(git_refdb *refdb); */
    static native void jniFree(long refdb);

    /** int git_refdb_open(git_refdb **out, git_repository *repo); */
    static native int jniOpen(AtomicLong out, long repoPtr);

    /** -------- Jni Signature ---------- */

    protected Refdb(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Create a new reference database and automatically add the default backends:
     *
     * <pre>
     *  - git_refdb_dir: read and write loose and packed refs
     *      from disk, assuming the repository dir as the folder
     * </pre>
     *
     * @return reference database if open succeeded or null if the open failed.
     * @param repository the repository
     * @throws GitException git errors
     */
    @Nullable
    public static Refdb open(@Nonnull Repository repository) {
        Refdb out = new Refdb(false, 0);
        Error.throwIfNeeded(jniOpen(out._rawPtr, repository.getRawPointer()));
        return out.getRawPointer() == 0 ? null : out;
    }

    /**
     * Suggests that the given refdb compress or optimize its references. This mechanism is
     * implementation specific. For on-disk reference databases, for example, this may pack all
     * loose references.
     */
    public void compress() {
        Error.throwIfNeeded(jniCompress(getRawPointer()));
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
}

package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public class Indexer extends CAutoReleasable {
    /**
     * int git_indexer_append(git_indexer *idx, const void *data, size_t size, git_indexer_progress
     * *stats);
     */
    static native int jniAppend(long idx, byte[] data, int size, long stats);

    /** int git_indexer_commit(git_indexer *idx, git_indexer_progress *stats); */
    static native int jniCommit(long idx, long stats);

    /** void git_indexer_free(git_indexer *idx); */
    static native void jniFree(long idx);

    /** const git_oid * git_indexer_hash(const git_indexer *idx); */
    static native byte[] jniHash(long idx);

    /**
     * int git_indexer_new(git_indexer **out, const char *path, unsigned int mode, git_odb *odb,
     * git_indexer_options *opts);
     */
    static native int jniNew(AtomicLong out, String path, int mode, long odb, long opts);

    static native void jniOptionsFree(long optsPtr);

    static native int jniOptionsNew(AtomicLong outOpts, int version, Internals.JCallback jCb);

    /** unsigned int indexed_deltas */
    static native int jniProgressGetIndexedDeltas(long progressPtr);

    /** unsigned int indexed_objects */
    static native int jniProgressGetIndexedObjects(long progressPtr);

    /** unsigned int local_objects */
    static native int jniProgressGetLocalObjects(long progressPtr);

    /** size_t received_bytes */
    static native int jniProgressGetReceivedBytes(long progressPtr);

    /** unsigned int received_objects */
    static native int jniProgressGetReceivedObjects(long progressPtr);

    /** unsigned int total_deltas */
    static native int jniProgressGetTotalDeltas(long progressPtr);

    /** unsigned int total_objects */
    static native int jniProgressGetTotalObjects(long progressPtr);

    static native long jniProgressNew();

    protected Indexer(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Create a new indexer instance
     *
     * @return the indexer instance
     * @param path to the directory where the packfile should be stored
     * @param mode permissions to use creating packfile or 0 for defaults
     * @param odb object database from which to read base objects when fixing thin packs. Pass NULL
     *     if no thin pack is expected (an error will be returned if there are bases missing)
     * @param opts Optional structure containing additional options. See `git_indexer_options`
     *     above.
     * @throws GitException git2 exception
     */
    @Nonnull
    public static Indexer create(
            @Nonnull String path, int mode, @Nonnull Odb odb, @Nullable Options opts) {
        Indexer out = new Indexer(false, 0);
        int e =
                jniNew(
                        out._rawPtr,
                        path,
                        mode,
                        odb.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Add data to the indexer
     *
     * @param data the data to add
     * @return stat progress
     */
    public Progress append(byte[] data) {
        Progress progress = Progress.create();
        int e = jniAppend(getRawPointer(), data, data.length, progress.getRawPointer());
        Error.throwIfNeeded(e);
        return progress;
    }

    /**
     * Finalize the pack and index
     *
     * <p>Resolve any pending deltas and write out the index file
     */
    public Progress commit() {
        Progress stats = Progress.create();
        int e = jniCommit(getRawPointer(), stats.getRawPointer());
        Error.throwIfNeeded(e);
        return stats;
    }

    @Nonnull
    public Oid hash() {
        return Oid.of(jniHash(getRawPointer()));
    }

    @FunctionalInterface
    public interface ProgressCb {
        int accept(Progress stats);
    }

    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;
        private ProgressCb _progressCb;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        public Options create(int version) {
            Options out = new Options(false, 0);
            jniOptionsNew(
                    out._rawPtr,
                    version,
                    progressPtr -> {
                        if (out._progressCb != null) {
                            return out._progressCb.accept(new Progress(true, progressPtr));
                        }
                        return 0;
                    });
            return out;
        }

        public void setProgressCb(ProgressCb progressCb) {
            _progressCb = progressCb;
        }
    }

    public static class Progress extends CAutoReleasable {
        protected Progress(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Progress create() {
            long ptr = jniProgressNew();
            return new Progress(false, ptr);
        }

        @Override
        protected void freeOnce(long cPtr) {}

        public int getTotalObjects() {
            return jniProgressGetTotalObjects(getRawPointer());
        }

        public int getIndexedObjects() {
            return jniProgressGetIndexedObjects(getRawPointer());
        }

        public int getReceivedObjects() {
            return jniProgressGetReceivedObjects(getRawPointer());
        }

        public int getLocalObjects() {
            return jniProgressGetLocalObjects(getRawPointer());
        }

        public int getTotalDeltas() {
            return jniProgressGetTotalDeltas(getRawPointer());
        }

        public int getIndexedDeltas() {
            return jniProgressGetIndexedDeltas(getRawPointer());
        }

        public int getReceivedBytes() {
            return jniProgressGetReceivedBytes(getRawPointer());
        }
    }
}

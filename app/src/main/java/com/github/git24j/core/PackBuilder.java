package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PackBuilder extends CAutoReleasable {

    /** int git_packbuilder_foreach(git_packbuilder *pb, git_packbuilder_foreach_cb * cb); */
    static native int jniForeach(long pb, Internals.BArrCallback cb);

    /** void git_packbuilder_free(git_packbuilder *pb); */
    static native void jniFree(long pb);

    /** const git_oid * git_packbuilder_hash(git_packbuilder *pb); */
    static native byte[] jniHash(long pb);

    /** int git_packbuilder_insert(git_packbuilder *pb, const git_oid *id, const char *name); */
    static native int jniInsert(long pb, Oid id, String name);

    /** int git_packbuilder_insert_commit(git_packbuilder *pb, const git_oid *id); */
    static native int jniInsertCommit(long pb, Oid id);

    /**
     * int git_packbuilder_insert_recur(git_packbuilder *pb, const git_oid *id, const char *name);
     */
    static native int jniInsertRecur(long pb, Oid id, String name);

    /** int git_packbuilder_insert_tree(git_packbuilder *pb, const git_oid *id); */
    static native int jniInsertTree(long pb, Oid id);

    /** int git_packbuilder_insert_walk(git_packbuilder *pb, git_revwalk *walk); */
    static native int jniInsertWalk(long pb, long walk);

    /** int git_packbuilder_new(git_packbuilder **out, git_repository *repo); */
    static native int jniNew(AtomicLong out, long repoPtr);

    /** size_t git_packbuilder_object_count(git_packbuilder *pb); */
    static native int jniObjectCount(long pb);

    /**
     * int git_packbuilder_set_callbacks(git_packbuilder *pb, git_packbuilder_progress *
     * progress_cb);
     */
    static native int jniSetCallbacks(long pb, Internals.IIICallback progressCb);

    /** unsigned int git_packbuilder_set_threads(git_packbuilder *pb, unsigned int n); */
    static native int jniSetThreads(long pb, int n);

    /**
     * int git_packbuilder_write(git_packbuilder *pb, const char *path, unsigned int mode,
     * git_indexer_progress_cb * progress_cb);
     */
    static native int jniWrite(long pb, String path, int mode, Internals.JCallback progressCb);

    /** int git_packbuilder_write_buf(git_buf *buf, git_packbuilder *pb); */
    static native int jniWriteBuf(Buf buf, long pb);

    /** size_t git_packbuilder_written(git_packbuilder *pb); */
    static native int jniWritten(long pb);

    protected PackBuilder(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Initialize a new packbuilder
     *
     * @return The new packbuilder object
     * @param repo The repository
     * @throws GitException git2 exceptions
     */
    @Nonnull
    public static PackBuilder create(@Nonnull Repository repo) {
        PackBuilder builder = new PackBuilder(false, 0);
        Error.throwIfNeeded(jniNew(builder._rawPtr, repo.getRawPointer()));
        return builder;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Set number of threads to spawn
     *
     * <p>By default, libgit2 won't spawn any threads at all; when set to 0, libgit2 will autodetect
     * the number of CPUs.
     *
     * @param n Number of threads to spawn
     * @return number of actual threads to be used
     */
    public int setThreads(int n) {
        return jniSetThreads(getRawPointer(), n);
    }

    /**
     * Insert a single object
     *
     * <p>For an optimal pack it's mandatory to insert objects in recency order, commits followed by
     * trees and blobs.
     *
     * @param id The oid of the commit
     * @param name The name; might be NULL
     * @throws GitException git2 exceptions
     */
    public PackBuilder insert(@Nonnull Oid id, @Nullable String name) {
        Error.throwIfNeeded(jniInsert(getRawPointer(), id, name));
        return this;
    }

    /**
     * Insert a root tree object
     *
     * <p>This will add the tree as well as all referenced trees and blobs.
     *
     * @param id The oid of the root tree
     * @throws GitException git2 exceptions
     */
    public PackBuilder insertTree(@Nonnull Oid id) {
        Error.throwIfNeeded(jniInsertTree(getRawPointer(), id));
        return this;
    }

    /**
     * Insert a commit object
     *
     * <p>This will add a commit as well as the completed referenced tree.
     *
     * @param id The oid of the commit
     * @throws GitException git2 exceptions
     */
    @Nonnull
    public PackBuilder insertCommit(@Nonnull Oid id) {
        Error.throwIfNeeded(jniInsertCommit(getRawPointer(), id));
        return this;
    }

    /**
     * Insert objects as given by the walk
     *
     * <p>Those commits and all objects they reference will be inserted into the packbuilder.
     *
     * @param walk the revwalk to use to fill the packbuilder
     * @throws GitException git2 exceptions
     */
    @Nonnull
    public PackBuilder insertWalk(@Nonnull Revwalk walk) {
        Error.throwIfNeeded(jniInsertWalk(getRawPointer(), walk.getRawPointer()));
        return this;
    }

    /**
     * Recursively insert an object and its referenced objects
     *
     * <p>Insert the object as well as any object it references.
     *
     * @param id the id of the root object to insert
     * @param name optional name for the object
     * @throws GitException git2 exceptions
     */
    @Nonnull
    public PackBuilder insertRecur(@Nonnull Oid id, @Nullable String name) {
        Error.throwIfNeeded(jniInsertRecur(getRawPointer(), id, name));
        return this;
    }

    /**
     * Write the contents of the packfile to an in-memory buffer
     *
     * <p>The contents of the buffer will become a valid packfile, even though there will be no
     * attached index
     *
     * @return the written content of the packfile, emtpy string implies nothing was written.
     */
    @Nonnull
    public String writeBuf() {
        Buf out = new Buf();
        jniWriteBuf(out, getRawPointer());
        return out.getString().orElse("");
    }

    /**
     * Write the new pack and corresponding index file to path.
     *
     * @param path Path to the directory where the packfile and index should be stored, or NULL for
     *     default location
     * @param mode permissions to use creating a packfile or 0 for defaults
     * @param progressCb function to call with progress information from the indexer (optional)
     * @throws GitException git2 exceptions
     */
    public void write(String path, int mode, @Nullable Indexer.ProgressCb progressCb) {
        jniWrite(
                getRawPointer(),
                path,
                mode,
                progressCb == null
                        ? null
                        : ptr -> progressCb.accept(new Indexer.Progress(true, ptr)));
    }

    @Nonnull
    public Oid hash() {
        return Oid.of(jniHash(getRawPointer()));
    }

    /**
     * Create the new pack and pass each object to the callback
     *
     * @param foreachCb the callback to call with each packed object's buffer
     * @throws GitException git2 exceptions
     */
    public void foreach(@Nonnull ForeachCb foreachCb) {
        int e = jniForeach(getRawPointer(), foreachCb::accept);
        Error.throwIfNeeded(e);
    }

    /**
     * Get the total number of objects the packbuilder will write out
     *
     * @return the number of objects in the packfile
     */
    public int objectCount() {
        return jniObjectCount(getRawPointer());
    }

    /**
     * Get the number of objects the packbuilder has already written out
     *
     * @return the number of objects which have already been written
     */
    public int written() {
        return jniWritten(getRawPointer());
    }

    /**
     * Set the callbacks for a packbuilder
     *
     * @param progressCb Function to call with progress information during pack building. Be aware
     *     that this is called inline with pack building operations, so performance may be affected.
     * @throws GitException git2 exceptions.
     */
    public void setCallbacks(ProgressCb progressCb) {
        int e = jniSetCallbacks(getRawPointer(), progressCb::accept);
        Error.throwIfNeeded(e);
    }

    /** Callback used to iterate over packed objects */
    @FunctionalInterface
    public interface ForeachCb {
        /** non-zero to terminate the iteration. */
        int accept(byte[] data);
    }

    /** Packbuilder progress notification function */
    public interface ProgressCb {
        int accept(int stage, int current, int total);
    }
}

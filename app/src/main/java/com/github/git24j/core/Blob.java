package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Blob extends GitObject {
    static native int jniCreateFromBuffer(Oid oid, long repoPtr, final byte[] buf);

    static native int jniCreateFromDisk(Oid oid, long repoPtr, String path);

    static native int jniCreateFromStream(AtomicLong outStream, long repoPtr, String hintPath);

    static native int jniCreateFromStreamCommit(Oid oid, long streamPtr);

    static native int jniCreateFromWorkdir(Oid oid, long repoPtr, String relativePath);

    static native int jniDup(AtomicLong outDest, long srcPtr);

    /**
     * int git_blob_filtered_content(git_buf *out, git_blob *blob, const char *as_path, int
     * check_for_binary_data);
     */
    static native int jniFilteredContent(Buf out, long blob, String asPath, int checkForBinaryData);

    static native byte[] jniId(long blobPtr);

    static native int jniIsBinary(long blobPtr);

    static native int jniLookup(AtomicLong outBlob, long repoPtr, Oid oid);

    static native int jniLookupPrefix(AtomicLong outBlob, long repoPtr, String shortId);

    static native long jniOwner(long blobPtr);

    static native long jniRawSize(long blobPtr);

    Blob(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }

    /**
     * Read a file from the working folder of a repository and write it to the Object Database as a
     * loose blob
     *
     * @param repo git repository
     * @param relativePath file from which the blob will be created, relative to the repository's
     *     working dir
     * @return id of the written blob.
     */
    public static Oid createFromWorkdir(Repository repo, String relativePath) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromWorkdir(oid, repo.getRawPointer(), relativePath));
        return oid;
    }

    /**
     * Read a file from the filesystem and write its content to the Object Database as a loose blob
     *
     * @param repo repository where the blob will be written. this repository can be bare or not
     * @param path file from which the blob will be created
     * @return the id of the written blob
     * @throws GitException git error
     */
    public static Oid createdFromDisk(Repository repo, String path) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromDisk(oid, repo.getRawPointer(), path));
        return oid;
    }

    /**
     * Create a stream to write a new blob into the object db
     *
     * <p>This function may need to buffer the data on disk and will in general not be the right
     * choice if you know the size of the data to write. If you have data in memory, use
     * `git_blob_create_frombuffer()`. If you do not, but know the size of the contents (and don't
     * want/need to perform filtering), use `git_odb_open_wstream()`.
     *
     * <p>Don't close this stream yourself but pass it to `git_blob_create_fromstream_commit()` to
     * commit the write to the object db and get the object id.
     *
     * <p>If the `hintpath` parameter is filled, it will be used to determine what git filters
     * should be applied to the object before it is written to the object database.
     *
     * @param repo Repository where the blob will be written. This repository can be bare or not.
     * @param hintpath If not NULL, will be used to select data filters to apply onto the content of
     *     the blob to be created.
     * @return {@link WriteStream} an object to write data to.
     * @throws GitException git error
     */
    public static @Nonnull WriteStream createFromStream(
            @Nonnull Repository repo, @Nullable String hintpath) {
        AtomicLong outWs = new AtomicLong();
        Error.throwIfNeeded(jniCreateFromStream(outWs, repo.getRawPointer(), hintpath));
        return new WriteStream(outWs.get());
    }

    /**
     * Close the stream and write the blob to the object db
     *
     * <p>The stream will be closed and freed.
     *
     * @param ws an object to write data to.
     * @return id of the new blob
     * @throws GitException git error
     */
    public static Oid createFromStreamCommit(WriteStream ws) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromStreamCommit(oid, ws.getRawPointer()));
        return oid;
    }

    /**
     * Write an in-memory buffer to the ODB as a blob
     *
     * @param repo repository where to blob will be written
     * @param buf data to be written into the blob
     * @return id of the written blob
     */
    public static Oid createFromBuffer(Repository repo, final byte[] buf) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromBuffer(oid, repo.getRawPointer(), buf));
        return oid;
    }

    /**
     * Lookup a blob from sha in a repository.
     *
     * @param repo git repo
     * @param oid sha of the blob to search
     * @return found blob or null
     * @throws GitException git error
     */
    @CheckForNull
    public static Blob lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniLookup(out, repo.getRawPointer(), oid));
        return out.get() == 0 ? null : new Blob(false, out.get());
    }

    /**
     * Lookup a blob from short sha in a repository. You probably want to use {@code Blob::lookup}
     * instead.
     *
     * @param repo git repo
     * @param shortId oid sha of the blob to search.
     * @return found blob or null
     * @throws GitException git error
     */
    @CheckForNull
    public static Blob lookupPrefix(@Nonnull Repository repo, @Nonnull String shortId) {
        AtomicLong outBlob = new AtomicLong();
        Error.throwIfNeeded(jniLookupPrefix(outBlob, repo.getRawPointer(), shortId));
        return outBlob.get() == 0 ? null : new Blob(false, outBlob.get());
    }

    @Override
    @Nonnull
    public Blob dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return new Blob(false, out.get());
    }

    /** Get the id of a blob. */
    @CheckForNull
    @Override
    public Oid id() {
        return Oid.ofNullable(jniId(getRawPointer()));
    }

    /** Get the repository that contains the blob. */
    @Override
    public Repository owner() {
        return Repository.ofRaw(jniOwner(getRawPointer()));
    }

    /** @return the size in bytes of the contents of a blob. */
    public long rawSize() {
        return jniRawSize(getRawPointer());
    }

    public boolean isBinary() {
        return jniIsBinary(getRawPointer()) == 1;
    }

    /**
     * Get a buffer with the filtered content of a blob.
     *
     * <p>This applies filters as if the blob was being checked out to the working directory under
     * the specified filename. This may apply CRLF filtering or other types of changes depending on
     * the file attributes set for the blob and the content detected in it.
     *
     * <p>If no filters need to be applied, then the `out` buffer will just be populated with a
     * pointer to the raw content of the blob. In that case, be careful to *not* free the blob until
     * done with the buffer or copy it into memory you own.
     *
     * @param asPath Path used for file attribute lookups, etc.
     * @param checkForBinaryData Should this test if blob content contains NUL bytes / looks like
     *     binary data before applying filters?
     * @throws GitException git errors
     */
    @Nullable
    public String filteredContent(@Nonnull String asPath, boolean checkForBinaryData) {
        Buf out = new Buf();
        Error.throwIfNeeded(
                jniFilteredContent(out, getRawPointer(), asPath, checkForBinaryData ? 1 : 0));
        return out.getString().orElse(null);
    }
}

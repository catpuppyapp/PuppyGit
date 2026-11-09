package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Blame extends CAutoReleasable {
    /**
     * int git_blame_buffer(git_blame **out, git_blame *reference, const char *buffer, size_t
     * buffer_len);
     */
    static native int jniBuffer(AtomicLong out, long reference, String buffer, int bufferLen);

    /**
     * int git_blame_file(git_blame **out, git_repository *repo, const char *path, git_blame_options
     * *options);
     */
    static native int jniFile(AtomicLong out, long repoPtr, String path, long options);

    /** void git_blame_free(git_blame *blame); */
    static native void jniFree(long blame);

    /** const git_blame_hunk * git_blame_get_hunk_byindex(git_blame *blame, size_t index); */
    static native long jniGetHunkByindex(long blame, int index);

    /** const git_blame_hunk * git_blame_get_hunk_byline(git_blame *blame, size_t lineno); */
    static native long jniGetHunkByline(long blame, int lineno);

    /** size_t git_blame_get_hunk_count(git_blame *blame); */
    static native int jniGetHunkCount(long blame);

    /** char boundary */
    static native char jniHunkGetBoundary(long hunkPtr);

    /** git_oid final_commit_id */
    static native byte[] jniHunkGetFinalCommitId(long hunkPtr);

    /** git_signature *final_signature */
    static native long jniHunkGetFinalSignature(long hunkPtr);

    /** size_t final_start_line_number */
    static native int jniHunkGetFinalStartLineNumber(long hunkPtr);

    /** size_t lines_in_hunk */
    static native int jniHunkGetLinesInHunk(long hunkPtr);

    /** git_oid orig_commit_id */
    static native byte[] jniHunkGetOrigCommitId(long hunkPtr);

    /** const char *orig_path */
    static native String jniHunkGetOrigPath(long hunkPtr);

    /** git_signature *orig_signature */
    static native long jniHunkGetOrigSignature(long hunkPtr);

    /** size_t orig_start_line_number */
    static native int jniHunkGetOrigStartLineNumber(long hunkPtr);

    // return is the errcode, not 0 means err
    static native int jniOptionsNew(AtomicLong outPtr, int version);

    protected Blame(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Get the blame for a single file.
     *
     * @param repo repository whose history is to be walked
     * @param path path to file to consider
     * @param options options for the blame operation. If NULL, default Options will be used.
     * @return Blame object
     * @throws GitException git errors
     */
    @Nonnull
    public static Blame file(
            @Nonnull Repository repo, @Nonnull String path, @Nullable Options options) {
        Blame blame = new Blame(false, 0);
        Error.throwIfNeeded(
                jniFile(
                        blame._rawPtr,
                        repo.getRawPointer(),
                        path,
                        options == null ? 0 : options.getRawPointer()));
        return blame;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /** Gets the number of hunks that exist in the blame structure. */
    public int getHunkCount() {
        return jniGetHunkCount(getRawPointer());
    }

    /**
     * Gets the blame hunk at the given index.
     *
     * @param index index of the hunk to retrieve
     * @return the hunk at the given index, or empty if non is available
     * @throws GitException git errors
     */
    @Nullable
    public Hunk getHunkByIndex(int index) {
        long ptr = jniGetHunkByindex(getRawPointer(), index);
        return ptr == 0 ? null : new Hunk(true, ptr);
    }

    /**
     * Gets the hunk that relates to the given line number in the newest commit.
     *
     * @param lineno the (1-based) line number to find a hunk for
     * @return the hunk that contains the given line, or NULL on error
     */
    @Nullable
    public Hunk getHunkByLine(int lineno) {
        long ptr = jniGetHunkByline(getRawPointer(), lineno);
        return ptr == 0 ? null : new Hunk(true, ptr);
    }

    /**
     * Get blame data for a file that has been modified in memory. The `reference` parameter is a
     * pre-calculated blame for the in-odb history of the file. This means that once a file blame is
     * completed (which can be expensive), updating the buffer blame is very fast.
     *
     * <p>Lines that differ between the buffer and the committed version are marked as having a zero
     * OID for their final_commit_id.
     *
     * @param buffer the (possibly) modified contents of the file
     * @return blame data.
     * @throws GitException git errors
     */
    @Nonnull
    public Blame buffer(@Nonnull String buffer) {
        Blame out = new Blame(false, 0);
        Error.throwIfNeeded(jniBuffer(out._rawPtr, getRawPointer(), buffer, buffer.length()));
        return out;
    }

    public enum FlagT implements IBitEnum {
        /** Normal blame, the default */
        NORMAL(0),
        /** Track lines that have moved within a file (like `git blame -M`). NOT IMPLEMENTED. */
        TRACK_COPIES_SAME_FILE(1 << 0),
        /**
         * Track lines that have moved across files in the same commit (like `git blame -C`). NOT
         * IMPLEMENTED.
         */
        TRACK_COPIES_SAME_COMMIT_MOVES(1 << 1),
        /**
         * Track lines that have been copied from another file that exists in the same commit (like
         * `git blame -CC`). Implies SAME_FILE. NOT IMPLEMENTED.
         */
        TRACK_COPIES_SAME_COMMIT_COPIES(1 << 2),
        /**
         * Track lines that have been copied from another file that exists in *any* commit (like
         * `git blame -CCC`). Implies SAME_COMMIT_COPIES. NOT IMPLEMENTED.
         */
        TRACK_COPIES_ANY_COMMIT_COPIES(1 << 3),
        /** Restrict the search of commits to those reachable following only the first parents. */
        FIRST_PARENT(1 << 4),
        /**
         * Use mailmap file to map author and committer names and email addresses to canonical real
         * names and email addresses. The mailmap will be read from the working directory, or HEAD
         * in a bare repository.
         */
        USE_MAILMAP(1 << 5),

        /** Ignore whitespace differences */
        IGNORE_WHITESPACE(1 << 6);

        private final int _bit;

        FlagT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
    }

    /**
     * Structure that represents a blame hunk.
     *
     * <pre>
     * - `lines_in_hunk` is the number of lines in this hunk
     * - `final_commit_id` is the OID of the commit where this line was last
     *   changed.
     * - `final_start_line_number` is the 1-based line number where this hunk
     *   begins, in the final version of the file
     * - `final_signature` is the author of `final_commit_id`. If
     *   `GIT_BLAME_USE_MAILMAP` has been specified, it will contain the canonical
     *    real name and email address.
     * - `orig_commit_id` is the OID of the commit where this hunk was found.  This
     *   will usually be the same as `final_commit_id`, except when
     *   `GIT_BLAME_TRACK_COPIES_ANY_COMMIT_COPIES` has been specified.
     * - `orig_path` is the path to the file where this hunk originated, as of the
     *   commit specified by `orig_commit_id`.
     * - `orig_start_line_number` is the 1-based line number where this hunk begins
     *   in the file named by `orig_path` in the commit specified by
     *   `orig_commit_id`.
     * - `orig_signature` is the author of `orig_commit_id`. If
     *   `GIT_BLAME_USE_MAILMAP` has been specified, it will contain the canonical
     *    real name and email address.
     * - `boundary` is 1 iff the hunk has been tracked to a boundary commit (the
     *   root, or the commit specified in git_blame_options.oldest_commit)
     * </pre>
     */
    public static class Hunk extends CAutoReleasable {
        protected Hunk(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        /** size_t lines_in_hunk */
        public int getLinesInHunk() {
            return jniHunkGetLinesInHunk(getRawPointer());
        }

        /** git_oid final_commit_id */
        @CheckForNull
        public Oid getFinalCommitId() {
            byte[] rawId = jniHunkGetFinalCommitId(getRawPointer());
            return rawId == null ? null : Oid.of(rawId);
        }

        /** size_t final_start_line_number */
        public int getFinalStartLineNumber() {
            return jniHunkGetFinalStartLineNumber(getRawPointer());
        }

        /** git_signature *final_signature */
        public long getFinalSignature() {
            return jniHunkGetFinalSignature(getRawPointer());
        }

        /** git_oid orig_commit_id */
        @CheckForNull
        public Oid getOrigCommitId() {
            byte[] rawId = jniHunkGetOrigCommitId(getRawPointer());
            return rawId == null ? null : Oid.of(rawId);
        }

        /** const char *orig_path */
        @CheckForNull
        public String getOrigPath() {
            return jniHunkGetOrigPath(getRawPointer());
        }

        /** size_t orig_start_line_number */
        public int getOrigStartLineNumber() {
            return jniHunkGetOrigStartLineNumber(getRawPointer());
        }

        /** git_signature *orig_signature */
        @CheckForNull
        public Signature getOrigSignature() {
            long ptr = jniHunkGetOrigSignature(getRawPointer());
            return ptr == 0 ? null : new Signature(true, ptr);
        }

        /** char boundary */
        public char getBoundary() {
            return jniHunkGetBoundary(getRawPointer());
        }
    }
}

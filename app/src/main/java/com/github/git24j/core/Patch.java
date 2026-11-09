package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.Internals.JJJCallback;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Patch extends CAutoReleasable {
    /** void git_patch_free(git_patch *patch); */
    static native void jniFree(long patch);

    /**
     *
     *
     * <pre>
     * int git_patch_from_blob_and_buffer(
     *     git_patch **out,
     *     const git_blob *old_blob,
     *     const char *old_as_path,
     *     const void *buffer,
     *     size_t buffer_len,
     *     const char *buffer_as_path,
     *     const git_diff_options *opts);
     * </pre>
     */
    static native int jniFromBlobAndBuffer(
            AtomicLong out,
            long oldBlob,
            String oldAsPath,
            byte[] buffer,
            int bufferLen,
            String bufferAsPath,
            long opts);

    /**
     * int git_patch_from_blobs(git_patch **out, const git_blob *old_blob, const char *old_as_path,
     * const git_blob *new_blob, const char *new_as_path, const git_diff_options *opts);
     */
    static native int jniFromBlobs(
            AtomicLong out,
            long oldBlob,
            String oldAsPath,
            long newBlob,
            String newAsPath,
            long opts);

    /**
     *
     *
     * <pre>
     * int git_patch_from_buffers(
     *     git_patch **out,
     *     const void *old_buffer,
     *     size_t old_len,
     *     const char *old_as_path,
     *     const void *new_buffer,
     *     size_t new_len,
     *     const char *new_as_path,
     *     const git_diff_options *opts);
     * </pre>
     */
    static native int jniFromBuffers(
            AtomicLong out,
            byte[] oldBuffer,
            int oldLen,
            String oldAsPath,
            byte[] newBuffer,
            int newLen,
            String newAsPath,
            long opts);

    /** int git_patch_from_diff(git_patch **out, git_diff *diff, size_t idx); */
    static native int jniFromDiff(AtomicLong out, long diff, int idx);

    /** const git_diff_delta * git_patch_get_delta(const git_patch *patch); */
    static native long jniGetDelta(long patch);

    /**
     * int git_patch_get_hunk(const git_diff_hunk **out, size_t *lines_in_hunk, git_patch *patch,
     * size_t hunk_idx);
     */
    static native int jniGetHunk(
            AtomicLong out, AtomicInteger linesInHunk, long patch, int hunkIdx);

    /**
     * int git_patch_get_line_in_hunk(const git_diff_line **out, git_patch *patch, size_t hunk_idx,
     * size_t line_of_hunk);
     */
    static native int jniGetLineInHunk(AtomicLong out, long patch, int hunkIdx, int lineOfHunk);

    /**
     * int git_patch_line_stats(size_t *total_context, size_t *total_additions, size_t
     * *total_deletions, const git_patch *patch);
     */
    static native int jniLineStats(
            AtomicInteger totalContext,
            AtomicInteger totalAdditions,
            AtomicInteger totalDeletions,
            long patch);

    /** size_t git_patch_num_hunks(const git_patch *patch); */
    static native int jniNumHunks(long patch);

    /** int git_patch_num_lines_in_hunk(const git_patch *patch, size_t hunk_idx); */
    static native int jniNumLinesInHunk(long patch, int hunkIdx);

    /** int git_patch_print(git_patch *patch, git_diff_line_cb print_cb, void *payload); */
    static native int jniPrint(long patch, JJJCallback printCb);

    /**
     * size_t git_patch_size(git_patch *patch, int include_context, int include_hunk_headers, int
     * include_file_headers);
     */
    static native int jniSize(
            long patch, int includeContext, int includeHunkHeaders, int includeFileHeaders);

    /** int git_patch_to_buf(git_buf *out, git_patch *patch); */
    static native int jniToBuf(Buf out, long patch);

    protected Patch(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Return a patch for an entry in the diff list.
     *
     * <p>The `git_patch` is a newly created object contains the text diffs for the delta. You have
     * to call `git_patch_free()` when you are done with it. You can use the patch object to loop
     * over all the hunks and lines in the diff of the one delta.
     *
     * <p>For an unchanged file or a binary file, no `git_patch` will be created, the output will be
     * set to NULL, and the `binary` flag will be set true in the `git_diff_delta` structure.
     *
     * @return patch object
     * @param diff Diff list object
     * @param idx Index into diff list
     * @throws GitException git errors
     */
    @CheckForNull
    public static Patch fromDiff(@Nonnull Diff diff, int idx) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(jniFromDiff(out._rawPtr, diff.getRawPointer(), idx));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }

    /**
     * Directly generate a patch from the difference between two blobs.
     *
     * <p>This is just like `git_diff_blobs()` except it generates a patch object for the difference
     * instead of directly making callbacks. You can use the standard `git_patch` accessor functions
     * to read the patch data, and you must call `git_patch_free()` on the patch when done.
     *
     * @return The generated patch; empty on error
     * @param oldBlob Blob for old side of diff, or NULL for empty blob
     * @param oldAsPath Treat old blob as if it had this filename; can be NULL
     * @param newBlob Blob for new side of diff, or NULL for empty blob
     * @param newAsPath Treat new blob as if it had this filename; can be NULL
     * @param opts Options for diff, or NULL for default options
     * @throws GitException git errors
     */
    @Nonnull
    public static Patch fromBlobs(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable Blob newBlob,
            @Nullable String newAsPath,
            @Nullable Diff.Options opts) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(
                jniFromBlobs(
                        out._rawPtr,
                        oldBlob == null ? 0 : oldBlob.getRawPointer(),
                        oldAsPath,
                        newBlob == null ? 0 : newBlob.getRawPointer(),
                        newAsPath,
                        opts == null ? 0 : opts.getRawPointer()));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }

    /**
     * Directly generate a patch from the difference between a blob and a buffer.
     *
     * <p>This is just like `git_diff_blob_to_buffer()` except it generates a patch object for the
     * difference instead of directly making callbacks. You can use the standard `git_patch`
     * accessor functions to read the patch data, and you must call `git_patch_free()` on the patch
     * when done.
     *
     * @return The generated patch; empty on error
     * @param oldBlob Blob for old side of diff, or NULL for empty blob
     * @param oldAsPath Treat old blob as if it had this filename; can be NULL
     * @param buffer Raw data for new side of diff, or NULL for empty
     * @param bufferAsPath Treat buffer as if it had this filename; can be NULL
     * @param opts Options for diff, or NULL for default options
     * @throws GitException git errors
     */
    @Nullable
    public static Patch fromBlobAndBuffer(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable byte[] buffer,
            @Nullable String bufferAsPath,
            @Nullable Diff.Options opts) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(
                jniFromBlobAndBuffer(
                        out._rawPtr,
                        oldBlob == null ? 0 : oldBlob.getRawPointer(),
                        oldAsPath,
                        buffer,
                        buffer == null ? 0 : buffer.length,
                        bufferAsPath,
                        opts == null ? 0 : opts.getRawPointer()));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }

    /**
     * Directly generate a patch from the difference between two buffers.
     *
     * <p>This is just like `git_diff_buffers()` except it generates a patch object for the
     * difference instead of directly making callbacks. You can use the standard `git_patch`
     * accessor functions to read the patch data, and you must call `git_patch_free()` on the patch
     * when done.
     *
     * @return The generated patch; empty on error
     * @param oldBuffer Raw data for old side of diff, or NULL for empty
     * @param oldAsPath Treat old buffer as if it had this filename; can be NULL
     * @param newBuffer Raw data for new side of diff, or NULL for empty
     * @param newAsPath Treat buffer as if it had this filename; can be NULL
     * @param opts Options for diff, or NULL for default options
     * @throws GitException git errors
     */
    @Nullable
    public static Patch fromBuffers(
            @Nullable byte[] oldBuffer,
            @Nullable String oldAsPath,
            @Nullable byte[] newBuffer,
            @Nullable String newAsPath,
            @Nullable Diff.Options opts) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(
                jniFromBuffers(
                        out._rawPtr,
                        oldBuffer,
                        oldBuffer == null ? 0 : oldBuffer.length,
                        oldAsPath,
                        newBuffer,
                        newBuffer == null ? 0 : newBuffer.length,
                        newAsPath,
                        opts == null ? 0 : opts.getRawPointer()));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Get the delta associated with a patch. This delta points to internal data and you do not have
     * to release it when you are done with it.
     */
    @Nonnull
    public Diff.Delta getDelta() {
        long ptr = jniGetDelta(getRawPointer());
        return new Diff.Delta(ptr);
    }

    /** Get the number of hunks in a patch */
    public int numHunks() {
        return jniNumHunks(getRawPointer());
    }

    /**
     * Get line counts of each type in a patch.
     *
     * <p>This helps imitate a diff --numstat type of output. For that purpose, you only need the
     * `total_additions` and `total_deletions` values, but we include the `total_context` line count
     * in case you want the total number of lines of diff output that will be generated.
     *
     * <p>All outputs are optional. Pass NULL if you don't need a particular count.
     *
     * @return LineState object
     * @throws GitException git errors
     */
    @Nonnull
    public LineStats lineStats() {
        AtomicInteger totalContext = new AtomicInteger();
        AtomicInteger totalAdditions = new AtomicInteger();
        AtomicInteger totalDeletions = new AtomicInteger();
        Error.throwIfNeeded(
                jniLineStats(totalContext, totalAdditions, totalDeletions, getRawPointer()));
        return new LineStats(totalContext.get(), totalAdditions.get(), totalDeletions.get());
    }

    /**
     * Get the information about a hunk in a patch
     *
     * <p>Given a patch and a hunk index into the patch, this returns detailed information about
     * that hunk. Any of the output pointers can be passed as NULL if you don't care about that
     * particular piece of information.
     *
     * @return Information about the hunk or empty if hunk_idx out of range
     * @param hunkIdx Input index of hunk to get information about
     * @throws GitException git errors
     */
    @Nullable
    public HunkInfo getHunk(int hunkIdx) {
        AtomicLong outHunk = new AtomicLong();
        AtomicInteger linesInHunk = new AtomicInteger();
        int e = jniGetHunk(outHunk, linesInHunk, getRawPointer(), hunkIdx);
        if (ENOTFOUND.getCode() == e || outHunk.get() == 0) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new HunkInfo(new Diff.Hunk(outHunk.get()), linesInHunk.get());
    }

    /**
     * Get the number of lines in a hunk.
     *
     * @param hunkIdx Index of the hunk
     * @return Number of lines in hunk
     * @throws GitException GIT_ENOTFOUND if invalid hunk index
     */
    public int numLinesInHunk(int hunkIdx) {
        int r = jniNumLinesInHunk(getRawPointer(), hunkIdx);
        Error.throwIfNeeded(r);
        return r;
    }

    /**
     * Get data about a line in a hunk of a patch.
     *
     * <p>Given a patch, a hunk index, and a line index in the hunk, this will return a lot of
     * details about that line.
     *
     * @return The git_diff_line data for this line or empty if you pass a hunk index larger than
     *     the number of hunks or a line index larger than the number of lines in the hunk, this
     *     will return -1.
     * @param hunkIdx The index of the hunk
     * @param lineOfHunk The index of the line in the hunk
     * @throws GitException git errors
     */
    @Nullable
    public Diff.Line getLineInHunk(int hunkIdx, int lineOfHunk) {
        AtomicLong out = new AtomicLong();
        int e = jniGetLineInHunk(out, getRawPointer(), hunkIdx, lineOfHunk);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Diff.Line(out.get());
    }

    /**
     * Look up size of patch diff data in bytes
     *
     * <p>This returns the raw size of the patch data. This only includes the actual data from the
     * lines of the diff, not the file or hunk headers.
     *
     * <p>If you pass `include_context` as true (non-zero), this will be the size of all of the diff
     * output; if you pass it as false (zero), this will only include the actual changed lines (as
     * if `context_lines` was 0).
     *
     * @param includeContext Include context lines in size if non-zero
     * @param includeHunkHeaders Include hunk header lines if non-zero
     * @param includeFileHeaders Include file header lines if non-zero
     * @return The number of bytes of data
     */
    public int size(
            boolean includeContext, boolean includeHunkHeaders, boolean includeFileHeaders) {
        return jniSize(
                getRawPointer(),
                includeContext ? 1 : 0,
                includeHunkHeaders ? 1 : 0,
                includeFileHeaders ? 1 : 0);
    }

    /**
     * Serialize the patch to text via callback.
     *
     * <p>Returning a non-zero value from the callback will terminate the iteration and return that
     * value to the caller.
     *
     * @param printCb Callback function to output lines of the patch. Will be called for file
     *     headers, hunk headers, and diff lines.
     * @return 0 on success, non-zero callback return value, or error code
     */
    public int print(@Nonnull Diff.LineCb printCb) {
        int e =
                jniPrint(
                        getRawPointer(),
                        (delta, hunk, line) ->
                                printCb.accept(
                                        Diff.Delta.of(delta),
                                        Diff.Hunk.of(hunk),
                                        Diff.Line.of(line)));
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Get the content of a patch as a single diff text.
     *
     * @return patch content in plain text
     * @throws GitException git errors
     */
    @Nonnull
    public String toBuf() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniToBuf(buf, getRawPointer()));
        return buf.getString().orElse("");
    }

    public static class LineStats {
        private final int totalContext;
        private final int totalAdditions;
        private final int totalDeletions;

        public LineStats(int totalContext, int totalAdditions, int totalDeletions) {
            this.totalContext = totalContext;
            this.totalAdditions = totalAdditions;
            this.totalDeletions = totalDeletions;
        }

        public int getTotalContext() {
            return totalContext;
        }

        public int getTotalAdditions() {
            return totalAdditions;
        }

        public int getTotalDeletions() {
            return totalDeletions;
        }
    }

    /** the information about a hunk in a patch */
    public static class HunkInfo {
        private final Diff.Hunk _hunk;
        private final int _lines;

        public HunkInfo(Diff.Hunk hunk, int lines) {
            _hunk = hunk;
            _lines = lines;
        }

        @Nonnull
        public Diff.Hunk getHunk() {
            return _hunk;
        }

        public int getLines() {
            return _lines;
        }
    }
}

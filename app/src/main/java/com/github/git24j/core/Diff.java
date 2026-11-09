package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.git24j.core.Internals.*;

public class Diff extends CAutoReleasable {

    static native void jniDiffOptionsSetPathSpec(long diffOptionsPtr, String[] pathSpecJArr);
    static native String[] jniDiffOptionsGetPathSpec(long diffOptionsPtr);

    static native void jniDiffOptionsSetFlags(long diffOptionsPtr, int flags);
    static native int jniDiffOptionsGetFlags(long diffOptionsPtr);

    /** const char *data */
    static native String jniBinaryFileGetData(long binary_filePtr);

    /** size_t datalen */
    static native int jniBinaryFileGetDatalen(long binary_filePtr);

    /** size_t inflatedlen */
    static native int jniBinaryFileGetInflatedlen(long binary_filePtr);

    /** size_t type */
    static native int jniBinaryFileGetType(long binary_filePtr);

    /** unsigned int contains_data */
    static native int jniBinaryGetContainsData(long binaryPtr);

    /** git_diff_binary_file new_file */
    static native long jniBinaryGetNewFile(long binaryPtr);

    /** git_diff_binary_file old_file */
    static native long jniBinaryGetOldFile(long binaryPtr);

    /**
     * int git_diff_blob_to_buffer(const git_blob *old_blob, const char *old_as_path, const char
     * *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *options,
     * git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb,
     * git_diff_line_cb line_cb, void *payload);
     */
    static native int jniBlobToBuffer(
            long oldBlob,
            String oldAsPath,
            String buffer,
            int bufferLen,
            String bufferAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * int git_diff_blobs( const git_blob *old_blob, const char *old_as_path, const git_blob
     * *new_blob, const char *new_as_path, const git_diff_options *options, git_diff_file_cb
     * file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb,
     * void *payload);
     */
    static native int jniBlobs(
            long oldBlob,
            String oldAsPath,
            long newBlob,
            String newAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * int git_diff_buffers(const void *old_buffer, size_t old_len, const char *old_as_path, const
     * void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *options,
     * git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb,
     * git_diff_line_cb line_cb, void *payload);
     */
    static native int jniBuffers(
            byte[] oldBuffer,
            int oldLen,
            String oldAsPath,
            byte[] newBuffer,
            int newLen,
            String newAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * int git_diff_commit_as_email(git_buf *out, git_repository *repo, git_commit *commit, size_t
     * patch_no, size_t total_patches, git_diff_format_email_flags_t flags, const git_diff_options
     * *diff_opts);
     */
    static native int jniCommitAsEmail(
            Buf out,
            long repoPtr,
            long commit,
            int patchNo,
            int totalPatches,
            int flags,
            long diffOpts);

    /** int flags */
    static native int jniDeltaGetFlags(long deltaPtr);

    /** git_diff_file new_file */
    static native long jniDeltaGetNewFile(long deltaPtr);

    /** int nfiles */
    static native int jniDeltaGetNfiles(long deltaPtr);

    /** git_diff_file old_file */
    static native long jniDeltaGetOldFile(long deltaPtr);

    /** int similarity */
    static native int jniDeltaGetSimilarity(long deltaPtr);

    /** git_delta_t status */
    static native int jniDeltaGetStatus(long deltaPtr);

    /** uint32_t flags */
    static native int jniFileGetFlags(long filePtr);

    /** git_oid id */
    static native byte[] jniFileGetId(long filePtr);

    /** uint16_t id_abbrev */
    static native int jniFileGetIdAbbrev(long filePtr);

    /** uint16_t mode */
    static native int jniFileGetMode(long filePtr);

    /** const char *path */
    static native String jniFileGetPath(long filePtr);

    /** git_object_size_t size */
    static native int jniFileGetSize(long filePtr);

    /** int git_diff_find_init_options(git_diff_find_options *opts, unsigned int version); */
    static native int jniFindInitOptions(AtomicLong outOpts, int version);

    /** int git_diff_find_similar(git_diff *diff, const git_diff_find_options *options); */
    static native int jniFindSimilar(long diff, long options);

    /**
     * int git_diff_foreach(git_diff *diff, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb,
     * git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload);
     */
    static native int jniForeach(
            long diff,
            Internals.JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * int git_diff_format_email(git_buf *out, git_diff *diff, const git_diff_format_email_options
     * *opts);
     */
    static native int jniFormatEmail(Buf out, long diff, long opts);

    /**
     * int git_diff_format_email_init_options(git_diff_format_email_options *opts, unsigned int
     * version);
     */
    static native int jniFormatEmailInitOptions(long opts, int version);

    /** New and init. */
    static native int jniFormatEmailNewOptions(AtomicLong out, int version);

    /** free(git_fiff_format_email_options *opts) */
    static native void jniFormatEmailOptionsFree(long opts);

    /** void git_diff_free(git_diff *diff); */
    static native void jniFree(long diff);

    static native void jniFreeFindOptions(long findOptsPtr);

    /** free diff options */
    static native void jniFreeOptions(long opts);

    /** int git_diff_from_buffer(git_diff **out, const char *content, size_t content_len); */
    static native int jniFromBuffer(AtomicLong out, String content, int contentLen);

    /** const git_diff_delta * git_diff_get_delta(const git_diff *diff, size_t idx); */
    static native long jniGetDelta(long diff, int idx);

    /** int git_diff_get_stats(git_diff_stats **out, git_diff *diff); */
    static native int jniGetStats(AtomicLong out, long diff);

    /** const char* header */
    static native String jniHunkGetHeader(long hunkPtr);

    @Nullable
    static native byte[] jniHunkGetHeaderBytes(long hunkPtr);

    /** size_t header_len */
    static native int jniHunkGetHeaderLen(long hunkPtr);

    /** int new_lines */
    static native int jniHunkGetNewLines(long hunkPtr);

    /** int new_start */
    static native int jniHunkGetNewStart(long hunkPtr);

    /** int old_lines */
    static native int jniHunkGetOldLines(long hunkPtr);

    /** int old_start */
    static native int jniHunkGetOldStart(long hunkPtr);

    /**
     * int git_diff_index_to_index(git_diff **diff, git_repository *repo, git_index *old_index,
     * git_index *new_index, const git_diff_options *opts);
     */
    static native int jniIndexToIndex(
            AtomicLong diff, long repoPtr, long oldIndex, long newIndex, long opts);

    /**
     * int git_diff_index_to_workdir(git_diff **diff, git_repository *repo, git_index *index, const
     * git_diff_options *opts);
     */
    static native int jniIndexToWorkdir(AtomicLong diff, long repoPtr, long index, long opts);

    /** int git_diff_init_options(git_diff_options *opts, unsigned int version); */
    static native int jniInitOptions(AtomicLong outOpts, int version);

    /** int git_diff_is_sorted_icase(const git_diff *diff); */
    static native int jniIsSortedIcase(long diff);

    /** const char *content */
    static native String jniLineGetContent(long linePtr);

    /** size_t content_len */
    static native int jniLineGetContentLen(long linePtr);

    /** size_t content_offset */
    static native int jniLineGetContentOffset(long linePtr);

    /**
     *
     * @param linePtr line ptr
     * @return if content len > 0, return byte arr; else return null
     */
    @Nullable
    static native byte[] jniLineGetContentBytes(long linePtr);

    /** int new_lineno */
    static native int jniLineGetNewLineno(long linePtr);

    /** int num_lines */
    static native int jniLineGetNumLines(long linePtr);

    /** int old_lineno */
    static native int jniLineGetOldLineno(long linePtr);

    /** char origin */
    static native char jniLineGetOrigin(long linePtr);

    /** int git_diff_merge(git_diff *onto, const git_diff *from); */
    static native int jniMerge(long onto, long from);

    /** size_t git_diff_num_deltas(const git_diff *diff); */
    static native int jniNumDeltas(long diff);

    /** size_t git_diff_num_deltas_of_type(const git_diff *diff, git_delta_t type); */
    static native int jniNumDeltasOfType(long diff, int type);

    /** int git_diff_patchid(git_oid *out, git_diff *diff, git_diff_patchid_options *opts); */
    static native int jniPatchid(Oid out, long diff, long opts);

    /** int git_diff_patchid_init_options(git_diff_patchid_options *opts, unsigned int version); */
    static native int jniPatchidInitOptions(long opts, int version);

    static native void jniPatchidOptionsFree(long opts);

    static native int jniPatchidOptionsNew(AtomicLong outOpts, int version);

    /**
     * int git_diff_print(git_diff *diff, git_diff_format_t format, git_diff_line_cb print_cb, void
     * *payload);
     */
    static native int jniPrint(long diff, int format, JJJCallback printCb);

    /** size_t git_diff_stats_deletions(const git_diff_stats *stats); */
    static native int jniStatsDeletions(long stats);

    /** size_t git_diff_stats_files_changed(const git_diff_stats *stats); */
    static native int jniStatsFilesChanged(long stats);

    /** void git_diff_stats_free(git_diff_stats *stats); */
    static native void jniStatsFree(long stats);

    /** size_t git_diff_stats_insertions(const git_diff_stats *stats); */
    static native int jniStatsInsertions(long stats);

    /**
     * int git_diff_stats_to_buf(git_buf *out, const git_diff_stats *stats, git_diff_stats_format_t
     * format, size_t width);
     */
    static native int jniStatsToBuf(Buf out, long stats, int format, int width);

    /** char git_diff_status_char(git_delta_t status); */
    static native char jniStatusChar(int status);

    /** int git_diff_to_buf(git_buf *out, git_diff *diff, git_diff_format_t format); */
    static native int jniToBuf(Buf out, long diff, int format);

    /**
     * int git_diff_tree_to_index(git_diff **diff, git_repository *repo, git_tree *old_tree,
     * git_index *index, const git_diff_options *opts);
     */
    static native int jniTreeToIndex(
            AtomicLong diff, long repoPtr, long oldTree, long index, long opts);

    /**
     * int git_diff_tree_to_tree(git_diff **diff, git_repository *repo, git_tree *old_tree, git_tree
     * *new_tree, const git_diff_options *opts);
     */
    static native int jniTreeToTree(
            AtomicLong diff, long repoPtr, long oldTree, long newTree, long opts);

    /**
     * int git_diff_tree_to_workdir(git_diff **diff, git_repository *repo, git_tree *old_tree, const
     * git_diff_options *opts);
     */
    static native int jniTreeToWorkdir(AtomicLong diff, long repoPtr, long oldTree, long opts);

    /**
     * int git_diff_tree_to_workdir_with_index(git_diff **diff, git_repository *repo, git_tree
     * *old_tree, const git_diff_options *opts);
     */
    static native int jniTreeToWorkdirWithIndex(
            AtomicLong diff, long repoPtr, long oldTree, long opts);

    protected Diff(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Create a diff with the difference between two tree objects.
     *
     * <p>This is equivalent to `git diff <old-tree> <new-tree>`
     *
     * <p>The first tree will be used for the "old_file" side of the delta and the second tree will
     * be used for the "new_file" side of the delta. You can pass NULL to indicate an empty tree,
     * although it is an error to pass NULL for both the `old_tree` and `new_tree`.
     *
     * @param repo The repository containing the trees.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param newTree A git_tree object to diff to, or NULL for empty tree.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @return Diff between {@code oldTree} and {@code newTree}
     * @throws GitException git errors
     */
    public static Diff treeToTree(
            @Nonnull Repository repo,
            @Nullable Tree oldTree,
            @Nullable Tree newTree,
            @Nullable Options opts) {
        Diff diff = new Diff(false, 0);
        int e =
                jniTreeToTree(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree == null ? 0 : oldTree.getRawPointer(),
                        newTree == null ? 0 : newTree.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return diff;
    }

    /**
     * Create a diff between a tree and repository index.
     *
     * <p>This is equivalent to `git diff --cached <treeish>` or if you pass the HEAD tree, then
     * like `git diff --cached`.
     *
     * <p>The tree you pass will be used for the "old_file" side of the delta, and the index will be
     * used for the "new_file" side of the delta.
     *
     * <p>If you pass NULL for the index, then the existing index of the `repo` will be used. In
     * this case, the index will be refreshed from disk (if it has changed) before the diff is
     * generated.
     *
     * @param repo The repository containing the tree and index.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param index The index to diff with; repo index used if NULL.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    public static Diff treeToIndex(Repository repo, Tree oldTree, Index index, Options opts) {
        Diff diff = new Diff(false, 0);
        int e =
                jniTreeToIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        index.getRawPointer(),
                        opts.getRawPointer());
        Error.throwIfNeeded(e);
        return diff;
    }

    /**
     * Create a diff between the repository index and the workdir directory.
     *
     * <p>This matches the `git diff` command. See the note below on `git_diff_tree_to_workdir` for
     * a discussion of the difference between `git diff` and `git diff HEAD` and how to emulate a
     * `git diff <treeish>` using libgit2.
     *
     * <p>The index will be used for the "old_file" side of the delta, and the working directory
     * will be used for the "new_file" side of the delta.
     *
     * <p>If you pass NULL for the index, then the existing index of the `repo` will be used. In
     * this case, the index will be refreshed from disk (if it has changed) before the diff is
     * generated.
     *
     * @param repo The repository.
     * @param index The index to diff from; repo index used if NULL.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    public static Diff indexToWorkdir(Repository repo, Index index, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniIndexToWorkdir(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        index==null ? repo.index().getRawPointer() : index.getRawPointer(),
                        opts==null ? Options.create().getRawPointer() : opts.getRawPointer()));
        return diff;
    }

    /**
     * Create a diff between a tree and the working directory.
     *
     * <p>The tree you provide will be used for the "old_file" side of the delta, and the working
     * directory will be used for the "new_file" side.
     *
     * <p>This is not the same as `git diff <treeish>` or `git diff-index <treeish>`. Those commands
     * use information from the index, whereas this function strictly returns the differences
     * between the tree and the files in the working directory, regardless of the state of the
     * index. Use `git_diff_tree_to_workdir_with_index` to emulate those commands.
     *
     * <p>To see difference between this and `git_diff_tree_to_workdir_with_index`, consider the
     * example of a staged file deletion where the file has then been put back into the working dir
     * and further modified. The tree-to-workdir diff for that file is 'modified', but `git diff`
     * would show status 'deleted' since there is a staged delete.
     *
     * @param repo The repository containing the tree.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @return diff between {@code tree} and the working directory
     * @throws GitException git errors
     */
    public static Diff treeToWorkdir(Repository repo, Tree oldTree, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniTreeToWorkdir(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }

    /**
     * Create a diff between a tree and the working directory using index data to account for staged
     * deletes, tracked files, etc.
     *
     * <p>This emulates `git diff <tree>` by diffing the tree to the index and the index to the
     * working directory and blending the results into a single diff that includes staged deleted,
     * etc.
     *
     * @param repo The repository containing the tree.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    public static Diff treeToWorkdirWithIndex(Repository repo, Tree oldTree, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniTreeToWorkdirWithIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }

    /**
     * Create a diff with the difference between two index objects.
     *
     * <p>The first index will be used for the "old_file" side of the delta and the second index
     * will be used for the "new_file" side of the delta.
     *
     * @param repo The repository containing the indexes.
     * @param oldIndex A git_index object to diff from.
     * @param newIndex A git_index object to diff to.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    @Nonnull
    public static Diff indexToIndex(
            @Nonnull Repository repo,
            @Nonnull Index oldIndex,
            @Nonnull Index newIndex,
            @Nonnull Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniIndexToIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldIndex.getRawPointer(),
                        newIndex.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }

    /**
     * Merge one diff into another.
     *
     * <p>This merges items from the "from" list into the "onto" list. The resulting diff will have
     * all items that appear in either list. If an item appears in both lists, then it will be
     * "merged" to appear as if the old version was from the "onto" list and the new version is from
     * the "from" list (with the exception that if the item has a pending DELETE in the middle, then
     * it will show as deleted).
     *
     * @param onto Diff to merge into.
     * @param from Diff to merge.
     */
    public static void merge(@Nonnull Diff onto, @Nonnull Diff from) {
        Error.throwIfNeeded(jniMerge(onto.getRawPointer(), from.getRawPointer()));
    }

    /**
     * Look up the single character abbreviation for a delta status code.
     *
     * <p>When you run `git diff --name-status` it uses single letter codes in the output such as
     * 'A' for added, 'D' for deleted, 'M' for modified, etc. This function converts a git_delta_t
     * value into these letters for your own purposes. GIT_DELTA_UNTRACKED will return a space (i.e.
     * ' ').
     *
     * @param status The git_delta_t value to look up
     * @return The single character label for that code
     */
    public static char statusChar(@Nonnull DeltaT status) {
        return jniStatusChar(status.getBit());
    }

    /**
     * Directly run a diff on two blobs.
     *
     * <p>Compared to a file, a blob lacks some contextual information. As such, the `git_diff_file`
     * given to the callback will have some fake data; i.e. `mode` will be 0 and `path` will be
     * NULL.
     *
     * <p>NULL is allowed for either `old_blob` or `new_blob` and will be treated as an empty blob,
     * with the `oid` set to NULL in the `git_diff_file` data. Passing NULL for both blobs is a
     * noop; no callbacks will be made at all.
     *
     * <p>We do run a binary content check on the blob content and if either blob looks like binary
     * data, the `git_diff_delta` binary attribute will be set to 1 and no call to the hunk_cb nor
     * line_cb will be made (unless you pass `GIT_DIFF_FORCE_TEXT` of course).
     *
     * @param oldBlob Blob for old side of diff, or NULL for empty blob
     * @param oldAsPath Treat old blob as if it had this filename; can be NULL
     * @param newBlob Blob for new side of diff, or NULL for empty blob
     * @param newAsPath Treat new blob as if it had this filename; can be NULL
     * @param options Options for diff, or NULL for default options
     * @param fileCb Callback for "file"; made once if there is a diff; can be NULL
     * @param binaryCb Callback for binary files; can be NULL
     * @param hunkCb Callback for each hunk in diff; can be NULL
     * @param lineCb Callback for each line in diff; can be NULL
     * @return non-zero callback return value
     * @throws GitException git errors
     */
    public static int blobs(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable Blob newBlob,
            @Nullable String newAsPath,
            @Nullable Options options,
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        long jniOldBlob = oldBlob == null ? 0 : oldBlob.getRawPointer();
        String jniOldAsPath = oldAsPath == null ? null : oldAsPath;
        long jniNewBlob = newBlob == null ? 0 : newBlob.getRawPointer();
        String jniNewAsPath = newAsPath == null ? null : newAsPath;
        long jniOptions =
                options == null
                        ? Options.create(Options.CURRENT_VERSION).getRawPointer()
                        : options.getRawPointer();
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBlobs(
                        jniOldBlob,
                        jniOldAsPath,
                        jniNewBlob,
                        jniNewAsPath,
                        jniOptions,
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Directly run a diff between a blob and a buffer.
     *
     * <p>As with `git_diff_blobs`, comparing a blob and buffer lacks some context, so the
     * `git_diff_file` parameters to the callbacks will be faked a la the rules for
     * `git_diff_blobs()`.
     *
     * <p>Passing NULL for `old_blob` will be treated as an empty blob (i.e. the `file_cb` will be
     * invoked with GIT_DELTA_ADDED and the diff will be the entire content of the buffer added).
     * Passing NULL to the buffer will do the reverse, with GIT_DELTA_REMOVED and blob content
     * removed.
     *
     * @param oldBlob Blob for old side of diff, or NULL for empty blob
     * @param oldAsPath Treat old blob as if it had this filename; can be NULL
     * @param buffer Raw data for new side of diff, or NULL for empty
     * @param bufferAsPath Treat buffer as if it had this filename; can be NULL
     * @param options Options for diff, or NULL for default options
     * @param fileCb Callback for "file"; made once if there is a diff; can be NULL
     * @param binaryCb Callback for binary files; can be NULL
     * @param hunkCb Callback for each hunk in diff; can be NULL
     * @param lineCb Callback for each line in diff; can be NULL
     * @return non-zero callback return value
     * @throws GitException git errors
     */
    public static int blobToBuff(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable String buffer,
            @Nullable String bufferAsPath,
            @Nullable Options options,
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        long oldBlobPtr = oldBlob == null ? 0 : oldBlob.getRawPointer();
        String oldAsPathStr = oldAsPath == null ? null : oldAsPath;
        long optsPtr = options == null ? 0 : options.getRawPointer();
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBlobToBuffer(
                        oldBlobPtr,
                        oldAsPathStr,
                        buffer,
                        buffer == null ? 0 : buffer.length(),
                        bufferAsPath,
                        optsPtr,
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Directly run a diff between two buffers.
     *
     * <p>Even more than with `git_diff_blobs`, comparing two buffer lacks context, so the
     * `git_diff_file` parameters to the callbacks will be faked a la the rules for
     * `git_diff_blobs()`.
     *
     * @param oldBuffer Raw data for old side of diff, or NULL for empty
     * @param oldAsPath Treat old buffer as if it had this filename; can be NULL
     * @param newBuffer Raw data for new side of diff, or NULL for empty
     * @param newAsPath Treat buffer as if it had this filename; can be NULL
     * @param options Options for diff, or NULL for default options
     * @param fileCb Callback for "file"; made once if there is a diff; can be NULL
     * @param binaryCb Callback for binary files; can be NULL
     * @param hunkCb Callback for each hunk in diff; can be NULL
     * @param lineCb Callback for each line in diff; can be NULL
     * @return 0 on success, non-zero callback return value, or error code
     */
    public static int buffers(
            @Nullable byte[] oldBuffer,
            @Nullable String oldAsPath,
            @Nullable byte[] newBuffer,
            @Nullable String newAsPath,
            @Nullable Options options,
            FileCb fileCb,
            BinaryCb binaryCb,
            HunkCb hunkCb,
            LineCb lineCb) {
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBuffers(
                        oldBuffer,
                        oldBuffer == null ? 0 : oldBuffer.length,
                        oldAsPath,
                        newBuffer,
                        newBuffer == null ? 0 : newBuffer.length,
                        newAsPath,
                        options == null ? 0 : options.getRawPointer(),
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Transform a diff marking file renames, copies, etc.
     *
     * <p>This modifies a diff in place, replacing old entries that look like renames or copies with
     * new entries reflecting those changes. This also will, if requested, break modified files into
     * add/remove pairs if the amount of change is above a threshold.
     *
     * @param options Control how detection should be run, NULL for defaults
     * @throws GitException git errors
     */
    public void findSimilar(@Nonnull FindOptions options) {
        Error.throwIfNeeded(jniFindSimilar(getRawPointer(), options.getRawPointer()));
    }

    /**
     * Query how many diff records are there in a diff.
     *
     * @return Count of number of deltas in the list
     */
    public int numDeltas() {
        return jniNumDeltas(getRawPointer());
    }

    /**
     * Query how many diff deltas are there in a diff filtered by type.
     *
     * <p>This works just like `git_diff_entrycount()` with an extra parameter that is a
     * `git_delta_t` and returns just the count of how many deltas match that particular type.
     *
     * @param type A git_delta_t value to filter the count
     * @return Count of number of deltas matching delta_t type
     */
    public int numDeltasOfType(@Nonnull DeltaT type) {
        return jniNumDeltasOfType(getRawPointer(), type.getBit());
    }

    /**
     * Return the diff delta for an entry in the diff list.
     *
     * <p>The `git_diff_delta` pointer points to internal data and you do not have to release it
     * when you are done with it. It will go away when the * `git_diff` (or any associated
     * `git_patch`) goes away.
     *
     * <p>Note that the flags on the delta related to whether it has binary content or not may not
     * be set if there are no attributes set for the file and there has been no reason to load the
     * file data at this point. For now, if you need those flags to be up to date, your only option
     * is to either use `git_diff_foreach` or create a `git_patch`.
     *
     * @param idx Index into diff list
     * @return Pointer to git_diff_delta (or NULL if `idx` out of range)
     */
    @CheckForNull
    public Delta getDelta(int idx) {
        long ptr = jniGetDelta(getRawPointer(), idx);
        if (ptr == 0) {
            return null;
        }
        return new Delta(ptr);
    }

    /**
     * Check if deltas are sorted case sensitively or insensitively.
     *
     * @return false if case sensitive, true if case is ignored
     */
    public boolean isSortedIcase() {
        return jniIsSortedIcase(getRawPointer()) == 1;
    }

    /**
     * Loop over all deltas in a diff issuing callbacks.
     *
     * <p>This will iterate through all of the files described in a diff. You should provide a file
     * callback to learn about each file.
     *
     * <p>The "hunk" and "line" callbacks are optional, and the text diff of the files will only be
     * calculated if they are not NULL. Of course, these callbacks will not be invoked for binary
     * files on the diff or for files whose only changed is a file mode change.
     *
     * <p>Returning a non-zero value from any of the callbacks will terminate the iteration and
     * return the value to the user.
     *
     * @param fileCb Callback function to make per file in the diff.
     * @param binaryCb Optional callback to make for binary files.
     * @param hunkCb Optional callback to make per hunk of text diff. This callback is called to
     *     describe a range of lines in the diff. It will not be issued for binary files.
     * @param lineCb Optional callback to make per line of diff text. This same callback will be
     *     made for context lines, added, and removed lines, and even for a deleted trailing
     *     newline.
     * @return 0 on success, non-zero callback return value
     * @throws GitException git errors
     */
    public int foreach(
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e = jniForeach(getRawPointer(), jniFileCb, jniBinaryCb, jniHunkCb, jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Iterate over a diff generating formatted text output.
     *
     * <p>Returning a non-zero value from the callbacks will terminate the iteration and return the
     * non-zero value to the caller.
     *
     * @param format A git_diff_format_t value to pick the text format.
     * @param lineCb Callback to make per line of diff text.
     * @return 0 on success, non-zero callback return value
     * @throws GitException git errors
     */
    public int print(@Nonnull FormatT format, @Nullable LineCb lineCb) {
        JJJCallback cb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e = jniPrint(getRawPointer(), format.getCode(), cb);
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Produce the complete formatted text output from a diff into a buffer.
     *
     * @param format A git_diff_format_t value to pick the text format.
     * @return formated diff text
     * @throws GitException git errors
     */
    @Nonnull
    public Buf toBuf(@Nonnull FormatT format) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniToBuf(out, getRawPointer(), format.getCode()));
        return out;
    }

    /**
     * Read the contents of a git patch file into a `git_diff` object.
     *
     * <p>The diff object produced is similar to the one that would be produced if you actually
     * produced it computationally by comparing two trees, however there may be subtle differences.
     * For example, a patch file likely contains abbreviated object IDs, so the object IDs in a
     * `git_diff_delta` produced by this function will also be abbreviated.
     *
     * <p>This function will only read patch files created by a git implementation, it will not read
     * unified diffs produced by the `diff` program, nor any other types of patch files.
     *
     * @param content The contents of a patch file
     * @return buffer
     * @throws GitException git errors
     */
    @Nonnull
    public static Diff fromBuffer(@Nonnull String content) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(jniFromBuffer(diff._rawPtr, content, content.getBytes(StandardCharsets.UTF_8).length));
        return diff;
    }

    /**
     * Accumulate diff statistics for all patches.
     *
     * @return Structure containg the diff statistics.
     * @throws GitException git errors
     */
    @Nonnull
    public Stats getStats() {
        Stats out = new Stats(false, 0);
        Error.throwIfNeeded(jniGetStats(out._rawPtr, getRawPointer()));
        return out;
    }

    /**
     * Create an e-mail ready patch from a diff.
     *
     * @param opts structure with options to influence content and formatting.
     * @return buffer to store the e-mail patch in
     * @throws GitException git errors
     */
    @Nonnull
    public Buf formatEmail(@Nonnull FormatEmailOptions opts) {
        Buf out = new Buf();
        jniFormatEmail(out, getRawPointer(), opts.getRawPointer());
        return out;
    }

    /**
     * Create an e-mail ready patch for a commit.
     *
     * <p>Does not support creating patches for merge commits (yet).
     *
     * @param repo containing the commit
     * @param commit pointer to up commit
     * @param patchNo patch number of the commit
     * @param totalPatches total number of patches in the patch set
     * @param flags determines the formatting of the e-mail
     * @param diffOpts structure with options to influence diff or NULL for defaults.
     * @return buffer to store the e-mail patch in
     * @throws GitException git errors
     */
    @Nonnull
    public Buf commitAsEmail(
            @Nonnull Repository repo,
            @Nonnull Commit commit,
            int patchNo,
            int totalPatches,
            @Nonnull FormatEmailFlagT flags,
            @Nullable Options diffOpts) {
        Buf outBuf = new Buf();
        Error.throwIfNeeded(
                jniCommitAsEmail(
                        outBuf,
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        patchNo,
                        totalPatches,
                        flags.getCode(),
                        diffOpts == null ? 0 : diffOpts.getRawPointer()));
        return outBuf;
    }

    /**
     * Calculate the patch ID for the given patch.
     *
     * <p>Calculate a stable patch ID for the given patch by summing the hash of the file diffs,
     * ignoring whitespace and line numbers. This can be used to derive whether two diffs are the
     * same with a high probability.
     *
     * <p>Currently, this function only calculates stable patch IDs, as defined in git-patch-id(1),
     * and should in fact generate the same IDs as the upstream git project does.
     *
     * @param opts Options for how to calculate the patch ID. This is intended for future changes,
     *     as currently no options are available.
     * @return patch id
     * @throws GitException git errors
     */
    @Nonnull
    public Oid patchid(@Nullable PatchidOptions opts) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniPatchid(oid, getRawPointer(), opts == null ? 0 : opts.getRawPointer()));
        return oid;
    }

    //move to: Diff.Options.FlagT
    @Deprecated
    public enum OptionFlag implements IBitEnum {
        /** Normal diff, the default */
        NORMAL0(0),

        /*
         * Options controlling which files will be in the diff
         */

        /** Reverse the sides of the diff */
        REVERSE(1 << 0),

        /** Include ignored files in the diff */
        INCLUDE_IGNORED(1 << 1),

        /**
         * Even with GIT_DIFF_INCLUDE_IGNORED, an entire ignored directory will be marked with only
         * a single entry in the diff; this flag adds all files under the directory as IGNORED
         * entries, too.
         */
        RECURSE_IGNORED_DIRS(1 << 2),

        /** Include untracked files in the diff */
        INCLUDE_UNTRACKED(1 << 3),

        /**
         * Even with GIT_DIFF_INCLUDE_UNTRACKED, an entire untracked directory will be marked with
         * only a single entry in the diff (a la what core Git does in `git status`); this flag adds
         * *all* files under untracked directories as UNTRACKED entries, too.
         */
        RECURSE_UNTRACKED_DIRS(1 << 4),

        /** Include unmodified files in the diff */
        INCLUDE_UNMODIFIED(1 << 5),

        /**
         * Normally, a type change between files will be converted into a DELETED record for the old
         * and an ADDED record for the new; this options enabled the generation of TYPECHANGE delta
         * records.
         */
        INCLUDE_TYPECHANGE(1 << 6),

        /**
         * Even with GIT_DIFF_INCLUDE_TYPECHANGE, blob->tree changes still generally show as a
         * DELETED blob. This flag tries to correctly label blob->tree transitions as TYPECHANGE
         * records with new_file's mode set to tree. Note: the tree SHA will not be available.
         */
        INCLUDE_TYPECHANGE_TREES(1 << 7),

        /** Ignore file mode changes */
        IGNORE_FILEMODE(1 << 8),

        /** Treat all submodules as unmodified */
        IGNORE_SUBMODULES(1 << 9),

        /** Use case insensitive filename comparisons */
        IGNORE_CASE(1 << 10),

        /**
         * May be combined with `GIT_DIFF_IGNORE_CASE` to specify that a file that has changed case
         * will be returned as an add/delete pair.
         */
        INCLUDE_CASECHANGE(1 << 11),

        /**
         * If the pathspec is set in the diff options, this flags indicates that the paths will be
         * treated as literal paths instead of fnmatch patterns. Each path in the list must either
         * be a full path to a file or a directory. (A trailing slash indicates that the path will
         * _only_ match a directory). If a directory is specified, all children will be included.
         */
        DISABLE_PATHSPEC_MATCH(1 << 12),

        /**
         * Disable updating of the `binary` flag in delta records. This is useful when iterating
         * over a diff if you don't need hunk and data callbacks and want to avoid having to load
         * file completely.
         */
        SKIP_BINARY_CHECK(1 << 13),

        /**
         * When diff finds an untracked directory, to match the behavior of core Git, it scans the
         * contents for IGNORED and UNTRACKED files. If *all* contents are IGNORED, then the
         * directory is IGNORED; if any contents are not IGNORED, then the directory is UNTRACKED.
         * This is extra work that may not matter in many cases. This flag turns off that scan and
         * immediately labels an untracked directory as UNTRACKED (changing the behavior to not
         * match core Git).
         */
        ENABLE_FAST_UNTRACKED_DIRS(1 << 14),

        /**
         * When diff finds a file in the working directory with stat information different from the
         * index, but the OID ends up being the same, write the correct stat information into the
         * index. Note: without this flag, diff will always leave the index untouched.
         */
        UPDATE_INDEX(1 << 15),

        /** Include unreadable files in the diff */
        INCLUDE_UNREADABLE(1 << 16),

        /** Include unreadable files in the diff */
        INCLUDE_UNREADABLE_AS_UNTRACKED(1 << 17),

        /*
         * Options controlling how output will be generated
         */

        /**
         * Use a heuristic that takes indentation and whitespace into account which generally can
         * produce better diffs when dealing with ambiguous diff hunks.
         */
        INDENT_HEURISTIC(1 << 18),

        /** Treat all files as text, disabling binary attributes & detection */
        FORCE_TEXT(1 << 20),
        /** Treat all files as binary, disabling text diffs */
        FORCE_BINARY(1 << 21),

        /** Ignore all whitespace */
        IGNORE_WHITESPACE(1 << 22),
        /** Ignore changes in amount of whitespace */
        IGNORE_WHITESPACE_CHANGE(1 << 23),
        /** Ignore whitespace at end of line */
        IGNORE_WHITESPACE_EOL(1 << 24),

        /**
         * When generating patch text, include the content of untracked files. This automatically
         * turns on GIT_DIFF_INCLUDE_UNTRACKED but it does not turn on
         * GIT_DIFF_RECURSE_UNTRACKED_DIRS. Add that flag if you want the content of every single
         * UNTRACKED file.
         */
        SHOW_UNTRACKED_CONTENT(1 << 25),

        /**
         * When generating output, include the names of unmodified files if they are included in the
         * git_diff. Normally these are skipped in the formats that list files (e.g. name-only,
         * name-status, raw). Even with this, these will not be included in patch format.
         */
        SHOW_UNMODIFIED(1 << 26),

        /** Use the "patience diff" algorithm */
        PATIENCE(1 << 28),
        /** Take extra time to find minimal diff */
        MINIMAL(1 << 29),

        /**
         * Include the necessary deflate / delta information so that `git-apply` can apply given
         * diff information to binary files.
         */
        SHOW_BINARY(1 << 30),
        ;

        final int _bit;

        OptionFlag(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum FlagT implements IBitEnum {
        /** < file(s) treated as binary data */
        BINARY(1 << 0),
        /** < file(s) treated as text data */
        NOT_BINARY(1 << 1),
        /** < `id` value is known correct */
        VALID_ID(1 << 2),
        /** < file exists at this side of the delta */
        EXISTS(1 << 3),
        ;

        private final int _bit;

        FlagT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * What type of change is described by a git_diff_delta?
     *
     * <p>`RENAMED` and `COPIED` will only show up if you run `git_diff_find_similar()` on the diff
     * object.
     *
     * <p>`TYPECHANGE` only shows up given `GIT_DIFF_INCLUDE_TYPECHANGE` in the option flags
     * (otherwise type changes will be split into ADDED / DELETED pairs).
     */
    public enum DeltaT implements IBitEnum {
        UNMODIFIED(0),
        /** < no changes */
        ADDED(1),
        /** < entry does not exist in old version */
        DELETED(2),
        /** < entry does not exist in new version */
        MODIFIED(3),
        /** < entry content changed between old and new */
        RENAMED(4),
        /** < entry was renamed between old and new */
        COPIED(5),
        /** < entry was copied from another old entry */
        IGNORED(6),
        /** < entry is ignored item in workdir */
        UNTRACKED(7),
        /** < entry is untracked item in workdir */
        TYPECHANGE(8),
        /** < type of entry changed between old and new */
        UNREADABLE(9),
        /** < entry is unreadable */
        CONFLICTED(10),
        ;
        /** < entry in the index is conflicted */
        private final int _bit;

        DeltaT(int _code) {
            this._bit = _code;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /** Possible output formats for diff data */
    public enum FormatT {
        /** < full git diff */
        PATCH(1),
        /** < just the file headers of patch */
        PATCH_HEADER(2),
        /** < like git diff --raw */
        RAW(3),
        /** < like git diff --name-only */
        NAME_ONLY(4),
        /** < like git diff --name-status */
        NAME_STATUS(5),
        /**< git diff as used by git patch-id */
        PATCH_ID(6),

        ;

        private final int _code;

        FormatT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    public enum StatsFormatT {
        /** No stats */
        NONE(0),

        /** Full statistics, equivalent of `--stat` */
        FULL(1 << 0),

        /** Short statistics, equivalent of `--shortstat` */
        SHORT(1 << 1),

        /** Number statistics, equivalent of `--numstat` */
        NUMBER(1 << 2),

        /**
         * Extended header information such as creations, renames and mode changes, equivalent of
         * `--summary`
         */
        INCLUDE_SUMMARY(1 << 3),
        ;
        private final int _code;

        StatsFormatT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    /** Formatting options for diff e-mail generation */
    public enum FormatEmailFlagT {
        /** Normal patch, the default */
        NONE(0),

        /** Don't insert "[PATCH]" in the subject header */
        EXCLUDE_SUBJECT_PATCH_MARKER(1 << 0);
        private final int _code;

        FormatEmailFlagT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    @FunctionalInterface
    public interface FileCb {
        int accept(Delta delta, float progress);
    }

    @FunctionalInterface
    public interface BinaryCb {
        int accept(Delta delta, Binary binary);
    }

    @FunctionalInterface
    public interface HunkCb {
        int accept(Delta delta, Hunk hunk);
    }

    @FunctionalInterface
    public interface LineCb {
        int accept(Delta delta, Hunk hunk, Line line);
    }

    /**
     * Flags for diff options. A combination of these flags can be passed in via the `flags` value
     * in the `git_diff_options`.
     */
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /**
         * Create and initialize a Diff Options object
         *
         * <p>Initializes a `git_diff_options` with default values. Equivalent to creating an
         * instance with GIT_DIFF_OPTIONS_INIT.
         *
         * @param version The struct version; pass `GIT_DIFF_OPTIONS_VERSION`.
         * @throws GitException git errors
         */
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniInitOptions(opts._rawPtr, version));
            return opts;
        }

        public static Options create() {
            return create(CURRENT_VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFreeOptions(cPtr);
        }

        public String[] getPathSpec() {
            return jniDiffOptionsGetPathSpec(_rawPtr.get());
        }

        public void setPathSpec(String[] pathSpec) {
            jniDiffOptionsSetPathSpec(_rawPtr.get(), pathSpec);
        }

        public EnumSet<Diff.Options.FlagT> getFlags() {
            int flagValue = jniDiffOptionsGetFlags(_rawPtr.get());
            return IBitEnum.parse(flagValue, Diff.Options.FlagT.class);
        }

        public void setFlags(EnumSet<Diff.Options.FlagT> flags) {
            jniDiffOptionsSetFlags(_rawPtr.get(), IBitEnum.bitOrAll(flags));
        }

        /**
         Flags for diff options.  A combination of these flags can be passed
         in via the `flags` value in the `git_diff_options`.
         GIT_DIFF_NORMAL = 0, Normal diff, the default

         Options controlling which files will be in the diff

         Reverse the sides of the diff
         GIT_DIFF_REVERSE = (1u << 0),

         Include ignored files in the diff
         GIT_DIFF_INCLUDE_IGNORED = (1u << 1),

         Even with GIT_DIFF_INCLUDE_IGNORED, an entire ignored directory
         will be marked with only a single entry in the diff; this flag
         adds all files under the directory as IGNORED entries, too.
         GIT_DIFF_RECURSE_IGNORED_DIRS = (1u << 2),

         Include untracked files in the diff
         GIT_DIFF_INCLUDE_UNTRACKED = (1u << 3),

         Even with GIT_DIFF_INCLUDE_UNTRACKED, an entire untracked
         directory will be marked with only a single entry in the diff
         (a la what core Git does in `git status`); this flag adds all
         files under untracked directories as UNTRACKED entries, too.
         GIT_DIFF_RECURSE_UNTRACKED_DIRS = (1u << 4),

         Include unmodified files in the diff
         GIT_DIFF_INCLUDE_UNMODIFIED = (1u << 5),

         Normally, a type change between files will be converted into a
         DELETED record for the old and an ADDED record for the new; this
         options enabled the generation of TYPECHANGE delta records.
         GIT_DIFF_INCLUDE_TYPECHANGE = (1u << 6),

         Even with GIT_DIFF_INCLUDE_TYPECHANGE, blob->tree changes still
         generally show as a DELETED blob.  This flag tries to correctly
         label blob->tree transitions as TYPECHANGE records with new_file's
         mode set to tree.  Note: the tree SHA will not be available.
         GIT_DIFF_INCLUDE_TYPECHANGE_TREES = (1u << 7),

         Ignore file mode changes
         GIT_DIFF_IGNORE_FILEMODE = (1u << 8),

         Treat all submodules as unmodified
         GIT_DIFF_IGNORE_SUBMODULES = (1u << 9),

         Use case insensitive filename comparisons
         GIT_DIFF_IGNORE_CASE = (1u << 10),

         May be combined with `GIT_DIFF_IGNORE_CASE` to specify that a file
         that has changed case will be returned as an add/delete pair.
         GIT_DIFF_INCLUDE_CASECHANGE = (1u << 11),

         If the pathspec is set in the diff options, this flags indicates
         that the paths will be treated as literal paths instead of
         fnmatch patterns.  Each path in the list must either be a full
         path to a file or a directory.  (A trailing slash indicates that
         the path will _only_ match a directory).  If a directory is
         specified, all children will be included.
         GIT_DIFF_DISABLE_PATHSPEC_MATCH = (1u << 12),

         Disable updating of the `binary` flag in delta records.  This is
         useful when iterating over a diff if you don't need hunk and data
         callbacks and want to avoid having to load file completely.
         GIT_DIFF_SKIP_BINARY_CHECK = (1u << 13),

         When diff finds an untracked directory, to match the behavior of
         core Git, it scans the contents for IGNORED and UNTRACKED files.
         If *all* contents are IGNORED, then the directory is IGNORED; if
         any contents are not IGNORED, then the directory is UNTRACKED.
         This is extra work that may not matter in many cases.  This flag
         turns off that scan and immediately labels an untracked directory
         as UNTRACKED (changing the behavior to not match core Git).

         GIT_DIFF_ENABLE_FAST_UNTRACKED_DIRS = (1u << 14),

         When diff finds a file in the working directory with stat
         information different from the index, but the OID ends up being the
         same, write the correct stat information into the index.  Note:
         without this flag, diff will always leave the index untouched.

         GIT_DIFF_UPDATE_INDEX = (1u << 15),

         Include unreadable files in the diff
         GIT_DIFF_INCLUDE_UNREADABLE = (1u << 16),

         Include unreadable files in the diff
         GIT_DIFF_INCLUDE_UNREADABLE_AS_UNTRACKED = (1u << 17),


         Options controlling how output will be generated


         Use a heuristic that takes indentation and whitespace into account
         which generally can produce better diffs when dealing with ambiguous
         diff hunks.
         GIT_DIFF_INDENT_HEURISTIC = (1u << 18),

         Ignore blank lines
         GIT_DIFF_IGNORE_BLANK_LINES = (1u << 19),

         Treat all files as text, disabling binary attributes & detection
         GIT_DIFF_FORCE_TEXT = (1u << 20),

         Treat all files as binary, disabling text diffs
         GIT_DIFF_FORCE_BINARY = (1u << 21),

         Ignore all whitespace
         GIT_DIFF_IGNORE_WHITESPACE = (1u << 22),

         Ignore changes in amount of whitespace
         GIT_DIFF_IGNORE_WHITESPACE_CHANGE = (1u << 23),

         Ignore whitespace at end of line
         GIT_DIFF_IGNORE_WHITESPACE_EOL = (1u << 24),

         When generating patch text, include the content of untracked
         files.  This automatically turns on GIT_DIFF_INCLUDE_UNTRACKED but
         it does not turn on GIT_DIFF_RECURSE_UNTRACKED_DIRS.  Add that
         flag if you want the content of every single UNTRACKED file.
         GIT_DIFF_SHOW_UNTRACKED_CONTENT = (1u << 25),

         When generating output, include the names of unmodified files if
         they are included in the git_diff.  Normally these are skipped in
         the formats that list files (e.g. name-only, name-status, raw).
         Even with this, these will not be included in patch format.
         GIT_DIFF_SHOW_UNMODIFIED = (1u << 26),

         Use the "patience diff" algorithm
         GIT_DIFF_PATIENCE = (1u << 28),

         Take extra time to find minimal diff
         GIT_DIFF_MINIMAL = (1u << 29),

         Include the necessary deflate / delta information so that `git-apply`
         can apply given diff information to binary files.
         GIT_DIFF_SHOW_BINARY = (1u << 30)
         } git_diff_option_t;
         */
        public enum FlagT implements IBitEnum {
            NORMAL(0),
            REVERSE(1<<0),
            INCLUDE_IGNORED(1<<1),
            RECURSE_IGNORED_DIRS(1<<2),
            INCLUDE_UNTRACKED(1<<3),
            RECURSE_UNTRACKED_DIRS(1<<4),
            INCLUDE_UNMODIFIED(1<<5),
            INCLUDE_TYPECHANGE(1<<6),
            INCLUDE_TYPECHANGE_TREES(1<<7),
            IGNORE_FILEMODE(1<<8),
            IGNORE_SUBMODULES(1<<9),
            IGNORE_CASE(1<<10),
            INCLUDE_CASECHANGE(1<<11),
            DISABLE_PATHSPEC_MATCH(1<<12),
            SKIP_BINARY_CHECK(1<<13),
            ENABLE_FAST_UNTRACKED_DIRS(1<<14),
            UPDATE_INDEX(1<<15),
            INCLUDE_UNREADABLE(1<<16),
            INCLUDE_UNREADABLE_AS_UNTRACKED(1<<17),

            /*
             * Options controlling how output will be generated
             */
            INDENT_HEURISTIC(1<<18),
            IGNORE_BLANK_LINES(1<<19),
            FORCE_TEXT(1<<20),
            FORCE_BINARY(1<<21),
            IGNORE_WHITESPACE(1<<22),
            IGNORE_WHITESPACE_CHANGE(1<<23),
            IGNORE_WHITESPACE_EOL(1<<24),
            SHOW_UNTRACKED_CONTENT(1<<25),
            SHOW_UNMODIFIED(1<<26),
            PATIENCE(1<<28),
            MINIMAL(1<<29),
            SHOW_BINARY(1<<30),
            ;

            private final int _bit;

            FlagT(int bit) {
                _bit = bit;
            }

            @Override
            public int getBit() {
                return _bit;
            }
        }
    }

    /**
     * Control behavior of rename and copy detection
     *
     * <p>These options mostly mimic parameters that can be passed to git-diff.
     */
    public static class FindOptions extends CAutoReleasable {
        FindOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /**
         * Create and initialize git_diff_find_options structure
         *
         * <p>Initializes a `git_diff_find_options` with default values. Equivalent to creating an
         * instance with GIT_DIFF_FIND_OPTIONS_INIT.
         *
         * @param version The struct version; pass `GIT_DIFF_FIND_OPTIONS_VERSION`.
         * @throws GitException git errors
         */
        public static FindOptions create(int version) {
            FindOptions opts = new FindOptions(false, 0);
            jniFindInitOptions(opts._rawPtr, version);
            return opts;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFreeFindOptions(cPtr);
        }
    }

    /**
     * Description of changes to one entry.
     *
     * <p>A `delta` is a file pair with an old and new revision. The old version may be absent if
     * the file was just created and the new version may be absent if the file was deleted. A diff
     * is mostly just a list of deltas.
     *
     * <p>When iterating over a diff, this will be passed to most callbacks and you can use the
     * contents to understand exactly what has changed.
     *
     * <p>The `old_file` represents the "from" side of the diff and the `new_file` represents to
     * "to" side of the diff. What those means depend on the function that was used to generate the
     * diff and will be documented below. You can also use the `GIT_DIFF_REVERSE` flag to flip it
     * around.
     *
     * <p>Although the two sides of the delta are named "old_file" and "new_file", they actually may
     * correspond to entries that represent a file, a symbolic link, a submodule commit id, or even
     * a tree (if you are tracking type changes or ignored/untracked directories).
     *
     * <p>Under some circumstances, in the name of efficiency, not all fields will be filled in, but
     * we generally try to fill in as much as possible. One example is that the "flags" field may
     * not have either the `BINARY` or the `NOT_BINARY` flag set to avoid examining file contents if
     * you do not pass in hunk and/or line callbacks to the diff foreach iteration function. It will
     * just use the git attributes for those files.
     *
     * <p>The similarity score is zero unless you call `git_diff_find_similar()` which does a
     * similarity analysis of files in the diff. Use that function to do rename and copy detection,
     * and to split heavily modified files in add/delete pairs. After that call, deltas with a
     * status of GIT_DELTA_RENAMED or GIT_DELTA_COPIED will have a similarity score between 0 and
     * 100 indicating how similar the old and new sides are.
     *
     * <p>If you ask `git_diff_find_similar` to find heavily modified files to break, but to not
     * *actually* break the records, then GIT_DELTA_MODIFIED records may have a non-zero similarity
     * score if the self-similarity is below the split threshold. To display this value like core
     * Git, invert the score (a la `printf("M%03d", 100 - delta->similarity)`).
     */
    public static class Delta extends CAutoReleasable {

        /**
         * Construct a weakref to c struct
         *
         * @param rawPtr c pointer
         */
        protected Delta(long rawPtr) {
            super(true, rawPtr);
        }

        @CheckForNull
        static Delta of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Delta(rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Delta is owned by Diff and should not be released manually");
        }

        /** @return delta status, default {@code DeltaT.UNMODIFIED} */
        @Nonnull
        public DeltaT getStatus() {
            return IBitEnum.valueOf(
                    jniDeltaGetStatus(getRawPointer()), DeltaT.class, DeltaT.UNMODIFIED);
        }

        /** @return {@code FlagT} values */
        public EnumSet<FlagT> getFlags() {
            return IBitEnum.parse(jniDeltaGetFlags(getRawPointer()), FlagT.class);
        }

        /** for RENAMED and COPIED, value 0-100 */
        public int getSimilarity() {
            return jniDeltaGetSimilarity(getRawPointer());
        }

        /** @return number of files in this delta */
        public int getNfiles() {
            return jniDeltaGetNfiles(getRawPointer());
        }

        public File getOldFile() {
            return File.ofWeak(jniDeltaGetOldFile(getRawPointer()));
        }

        public File getNewFile() {
            return File.ofWeak(jniDeltaGetNewFile(getRawPointer()));
        }
    }

    public static class BinaryFile extends CAutoReleasable {
        protected BinaryFile(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        public int getType() {
            return jniBinaryFileGetType(getRawPointer());
        }

        public String getData() {
            return jniBinaryFileGetData(getRawPointer());
        }

        public int getDatalen() {
            return jniBinaryFileGetDatalen(getRawPointer());
        }

        public int getInflatedlen() {
            return jniBinaryFileGetInflatedlen(getRawPointer());
        }
    }

    /**
     * Structure describing the binary contents of a diff.
     *
     * <p>A `binary` file / delta is a file (or pair) for which no text diffs should be generated. A
     * diff can contain delta entries that are binary, but no diff content will be output for those
     * files. There is a base heuristic for binary detection and you can further tune the behavior
     * with git attributes or diff flags and option settings.
     */
    public static class Binary extends CAutoReleasable {
        protected Binary(long rawPtr) {
            super(true, rawPtr);
        }

        @CheckForNull
        static Binary of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Binary(rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Binary is owned by Diff and should not be released manually");
        }

        public int getContainsData() {
            return jniBinaryGetContainsData(getRawPointer());
        }

        public BinaryFile getOldFile() {
            return new BinaryFile(true, jniBinaryGetOldFile(getRawPointer()));
        }

        public BinaryFile getNewFile() {
            return new BinaryFile(true, jniBinaryGetNewFile(getRawPointer()));
        }
    }

    /**
     * Structure describing a hunk of a diff.
     *
     * <p>A `hunk` is a span of modified lines in a delta along with some stable surrounding
     * context. You can configure the amount of context and other properties of how hunks are
     * generated. Each hunk also comes with a header that described where it starts and ends in both
     * the old and new versions in the delta.
     */
    public static class Hunk extends CAutoReleasable {
        protected Hunk(long rawPtr) {
            super(true, rawPtr);
        }

        @CheckForNull
        static Hunk of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Hunk(rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Hunk is owned by Diff and should not be released manually");
        }

        public int getOldStart() {
            return jniHunkGetOldStart(getRawPointer());
        }

        public int getOldLines() {
            return jniHunkGetOldLines(getRawPointer());
        }

        public int getNewStart() {
            return jniHunkGetNewStart(getRawPointer());
        }

        public int getNewLines() {
            return jniHunkGetNewLines(getRawPointer());
        }

        public int getHeaderLen() {
            return jniHunkGetHeaderLen(getRawPointer());
        }

        /**
         * get the bytes of header, the bytes is sized by header len
         * @return if the header length > 0, return a byte arr, else return null
         */
        @Nullable
        public byte[] getHeaderBytes() {
            return jniHunkGetHeaderBytes(getRawPointer());
        }

        /**
         * get header sized by header length
         * @return the header text sized by header length
         */
        public String getHeader() {
            byte[] headerBytes = getHeaderBytes();
            return headerBytes!=null ? new String(headerBytes, StandardCharsets.UTF_8) : "";
        }

        /**
         * get the raw header which not sized by header length,
         * libgit2 doc said the `hunk->header` ends with '\0',
         * and this String created by jni c code `NewStringUTF`, it determines the length by '\0' too,
         * so if everything fine, this should return same content with `getHeader()`,
         * and the performance, I am not sure, maybe this better
         *
         * @return the raw header which not sized by header length
         */
        public String getHeaderRaw() {
            return jniHunkGetHeader(getRawPointer());
        }

        /**
         * use getHeader() instead
         * @return
         */
        @Deprecated
        public String getHeader_Depercated() {
            String rawHeader = jniHunkGetHeader(getRawPointer());
            int headerLen = getHeaderLen();
            byte[] src = rawHeader.getBytes(StandardCharsets.UTF_8);
            // because the libgit2 doc said the header terminated with '\0',
            // so this check should always false, if everything fine
            if(src.length > headerLen) {
                return new String(src, 0, headerLen, StandardCharsets.UTF_8);
            }

            return rawHeader;
        }
    }

    public static class File extends CAutoReleasable {
        protected File(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @CheckForNull
        static File ofWeak(long ptr) {
            return ptr == 0 ? null : new File(true, ptr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        /** @return Oid of the file, it can also be all zeros, which means absense of a file. */
        public Oid getId() {
            return Oid.of(jniFileGetId(getRawPointer()));
        }

        /** @return file path relative to the working directory of the repository */
        public String getPath() {
            return jniFileGetPath(getRawPointer());
        }

        /** @return the size of the entry in bytes. */
        public int getSize() {
            return jniFileGetSize(getRawPointer());
        }

        /** @return combination of the `git_diff_flag_t` types */
        @Nonnull
        public EnumSet<FlagT> getFlags() {
            return IBitEnum.parse(jniFileGetFlags(getRawPointer()), FlagT.class);
        }

        /**
         * @return roughly, the stat() `st_mode` value for the item. This will be restricted to one
         *     of the `FileMode` values.
         */
        @Nonnull
        public FileMode getMode() {
            return IBitEnum.valueOf(
                    jniFileGetMode(getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }

        /**
         * @return `id_abbrev` represents the known length of the `id` field, when converted to a
         *     hex string. It is generally `GIT_OID_HEXSZ`, unless this delta was created from
         *     reading a patch file, in which case it may be abbreviated to something reasonable,
         *     like 7 characters.
         */
        public int getIdAbbrev() {
            return jniFileGetIdAbbrev(getRawPointer());
        }
    }

    /**
     * Structure describing a line (or data span) of a diff.
     *
     * <p>A `line` is a range of characters inside a hunk. It could be a context line (i.e. in both
     * old and new versions), an added line (i.e. only in the new version), or a removed line (i.e.
     * only in the old version). Unfortunately, we don't know anything about the encoding of data in
     * the file being diffed, so we cannot tell you much about the line content. Line data will not
     * be NUL-byte terminated, however, because it will be just a span of bytes inside the larger
     * file.
     */
    public static class Line extends CAutoReleasable {

        /**
         * Line origin constants.
         *
         * These values describe where a line came from and will be passed to
         * the git_diff_line_cb when iterating over a diff.  There are some
         * special origin constants at the end that are used for the text
         * output callbacks to demarcate lines that are actually part of
         * the file or hunk headers.
         *
         * This type is binding for libgit2's: `git_diff_line_t`
         */
        public static class OriginType {
            /* These values will be sent to `git_diff_line_cb` along with the line */
            public static final char CONTEXT   = ' ';
            public static final char ADDITION  = '+';
            public static final char DELETION  = '-';

            public static final char CONTEXT_EOFNL = '='; /**< Both files have no LF at end */
            public static final char ADD_EOFNL = '>';     /**< Old has no LF at end, new does */
            public static final char DEL_EOFNL = '<';     /**< Old has LF at end, new does not */

            /* The following values will only be sent to a `git_diff_line_cb` when
             * the content of a diff is being formatted through `git_diff_print`.
             */
            public static final char FILE_HDR  = 'F';
            public static final char HUNK_HDR  = 'H';
            public static final char BINARY    = 'B'; /**< For "Binary files x and y differ" */
        }

        protected Line(long rawPtr) {
            super(true, rawPtr);
        }

        @CheckForNull
        static Line of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Line(rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Line is owned by Diff and should not be released manually");
        }

        public char getOrigin() {
            return jniLineGetOrigin(getRawPointer());
        }

        public int getOldLineno() {
            return jniLineGetOldLineno(getRawPointer());
        }

        public int getNewLineno() {
            return jniLineGetNewLineno(getRawPointer());
        }

        public int getNumLines() {
            return jniLineGetNumLines(getRawPointer());
        }

        public int getContentLen() {
            return jniLineGetContentLen(getRawPointer());
        }

        public int getContentOffset() {
            return jniLineGetContentOffset(getRawPointer());
        }

        /**
         * get the bytes of content, the bytes sized by content length
         * @return if content len > 0, return byte arr, else return null
         */
        @Nullable
        public byte[] getContentBytes(){
            return jniLineGetContentBytes(getRawPointer());
        }

        /**
         * get the content sized by content length
         * @return
         */
        public String getContent(){
            byte[] bytes = getContentBytes();

            return (bytes!=null) ? new String(bytes, StandardCharsets.UTF_8) : "";
        }


        /**
         * This has more data copy than getContent(), not recommended to use
         * @return
         */
        @Deprecated
        public String getContent_Deprecated() {
            String content = jniLineGetContent(getRawPointer());
            int contentLen = jniLineGetContentLen(getRawPointer());
            // content.length() is "chars count", not "bytes count"!
            // so this code is wrong in some cases! sometimes it will give you more lines than you wanted.
//            if (content.length() >= contentLen) {
//                return content.substring(0, contentLen);
//            }
            byte[] src = content.getBytes(StandardCharsets.UTF_8);
            // bytes.length < contentLen maybe not happen, because contentLen should be a part of content
            if(src.length > contentLen) {  //if content length bigger than contentLen, create a new sub array
                return new String(src, 0, contentLen, StandardCharsets.UTF_8);
            }

            // if content length equals contentLen, just return it, no more operations required
            return content;

        }
    }

    public static class Stats extends CAutoReleasable {
        protected Stats(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniStatsFree(cPtr);
        }

        /** @return total number of files changed in the diff */
        public int filesChanged() {
            return jniStatsFilesChanged(getRawPointer());
        }

        /** @return total number of insertions in the diff */
        public int insertions() {
            return jniStatsInsertions(getRawPointer());
        }

        /** @return total number of deletions in the diff */
        public int deletions() {
            return jniStatsDeletions(getRawPointer());
        }

        /**
         * Print diff statistics to a `git_buf`.
         *
         * @param format Formatting option.
         * @param width Target width for output (only affects GIT_DIFF_STATS_FULL)
         * @return the formatted diff statistics in.
         * @throws GitException git errors
         */
        public Buf toBuf(StatsFormatT format, int width) {
            Buf out = new Buf();
            Error.throwIfNeeded(jniStatsToBuf(out, getRawPointer(), format.getCode(), width));
            return out;
        }
    }

    /** Options for controlling the formatting of the generated e-mail. */
    public static class FormatEmailOptions extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        protected FormatEmailOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /** Create FormatEmailOptions by version. */
        @Nonnull
        public static FormatEmailOptions create(int version) {
            FormatEmailOptions opts = new FormatEmailOptions(false, 0);
            Error.throwIfNeeded(jniFormatEmailNewOptions(opts._rawPtr, version));
            return opts;
        }

        public static FormatEmailOptions defaultOptions() {
            return Holder.__DEFAULT;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFormatEmailOptionsFree(cPtr);
        }

        private static class Holder {
            static final FormatEmailOptions __DEFAULT = FormatEmailOptions.create(CURRENT_VERSION);
        }
    }

    public static class PatchidOptions extends CAutoReleasable {
        protected PatchidOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniPatchidOptionsFree(cPtr);
        }

        public PatchidOptions create(int version) {
            PatchidOptions out = new PatchidOptions(false, 0);
            Error.throwIfNeeded(jniPatchidOptionsNew(out._rawPtr, version));
            return out;
        }
    }
}

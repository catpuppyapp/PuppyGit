package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.Internals.OidArray;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Git merge util functions, all are static */
public class Merge {

    /**
     * int git_merge_analysis( git_merge_analysis_t *analysis_out, git_merge_preference_t
     * *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t
     * their_heads_len);
     */
    static native int jniAnalysis(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long[] theirHeads);

    /**
     * int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t
     * *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit
     * **their_heads, size_t their_heads_len);
     */
    static native int jniAnalysisForRef(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long ourRefPtr,
            long[] theirHeads);;

    /**
     * int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid
     * *two);
     */
    static native int jniBase(Oid out, long repoPtr, Oid one, Oid two);

    /**
     * int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid []
     * input_array);
     */
    static native int jniBaseMany(Oid outOid, long repoPtr, Oid[] inputArray);

    /**
     * int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid
     * [] input_array);
     */
    static native int jniBaseOctopus(Oid outOid, long repoPtr, Oid[] intputArray);

    /**
     * int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const
     * git_oid *two);
     */
    static native int jniBases(OidArray outOids, long repoPtr, Oid one, Oid two);

    /**
     * int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const
     * git_oid [] input_array);
     */
    static native int jniBasesMany(OidArray outOids, long repoPtr, Oid[] inputArray);

    /**
     * int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit,
     * const git_commit *their_commit, const git_merge_options *opts);
     */
    static native int jniCommits(
            AtomicLong out, long repoPtr, long ourCommit, long theirCommit, long opts);

    /**
     * int git_merge_file( git_merge_file_result *out, const git_merge_file_input *ancestor, const
     * git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options
     * *opts);
     */
    static native int jniFile(
            AtomicLong out, long ancestorPtr, long oursPtr, long theirsPtr, long optsPtr);

    /** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
    static native int jniFileInitInput(long opts, int version);

    /** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
    static native int jniFileInitOptions(long opts, int version);

    static native void jniFileInputFree(long optsPtr);

    /** int mode */
    static native int jniFileInputGetMode(long file_inputPtr);

    /** const char *path */
    static native String jniFileInputGetPath(long file_inputPtr);

    /** const char *ptr */
    static native String jniFileInputGetPtr(long file_inputPtr);

    /** size_t size */
    static native int jniFileInputGetSize(long file_inputPtr);

    /** unsigned int version */
    static native int jniFileInputGetVersion(long file_inputPtr);

    static native int jniFileInputNew(AtomicLong outOpts, int version);

    /** int mode */
    static native void jniFileInputSetMode(long file_inputPtr, int mode);

    /** const char *path */
    static native void jniFileInputSetPath(long file_inputPtr, String path);

    /** const char *ptr */
    static native void jniFileInputSetPtr(long file_inputPtr, String ptr);

    /** size_t size */
    static native void jniFileInputSetSize(long file_inputPtr, int size);

    /** unsigned int version */
    static native void jniFileInputSetVersion(long file_inputPtr, int version);

    static native void jniFileOptionsFree(long opts);

    /** const char *ancestor_label */
    static native String jniFileOptionsGetAncestorLabel(long file_optionsPtr);

    /** git_merge_file_favor_t favor */
    static native int jniFileOptionsGetFavor(long file_optionsPtr);

    /** uint32_t flags */
    static native int jniFileOptionsGetFlags(long file_optionsPtr);

    /** int marker_size */
    static native int jniFileOptionsGetMarkerSize(long file_optionsPtr);

    /** const char *our_label */
    static native String jniFileOptionsGetOurLabel(long file_optionsPtr);

    /** const char *their_label */
    static native String jniFileOptionsGetTheirLabel(long file_optionsPtr);

    /** unsigned int version */
    static native int jniFileOptionsGetVersion(long file_optionsPtr);

    static native int jniFileOptionsNew(AtomicLong outOpts, int version);

    /** const char *ancestor_label */
    static native void jniFileOptionsSetAncestorLabel(long file_optionsPtr, String ancestorLabel);

    /** git_merge_file_favor_t favor */
    static native void jniFileOptionsSetFavor(long file_optionsPtr, int favor);

    /** uint32_t flags */
    static native void jniFileOptionsSetFlags(long file_optionsPtr, int flags);

    /** int marker_size */
    static native void jniFileOptionsSetMarkerSize(long file_optionsPtr, int markerSize);

    /** const char *our_label */
    static native void jniFileOptionsSetOurLabel(long file_optionsPtr, String ourLabel);

    /** const char *their_label */
    static native void jniFileOptionsSetTheirLabel(long file_optionsPtr, String theirLabel);

    /** unsigned int version */
    static native void jniFileOptionsSetVersion(long file_optionsPtr, int version);

    /**
     * void git_merge_file_result_free(git_merge_file_result *result); Note: this also frees the
     * resultPtr itself.
     */
    static native void jniFileResultFree(long resultPtr);

    /** unsigned int automergeable */
    static native int jniFileResultGetAutomergeable(long file_resultPtr);

    /** size_t len */
    static native int jniFileResultGetLen(long file_resultPtr);

    /** unsigned int mode */
    static native int jniFileResultGetMode(long file_resultPtr);

    /** const char *path */
    static native String jniFileResultGetPath(long file_resultPtr);

    /** const char *ptr */
    static native String jniFileResultGetPtr(long file_resultPtr);

    /** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /**
     * int git_merge_create( git_repository *repo, const git_annotated_commit **their_heads, size_t
     * their_heads_len, const git_merge_options *merge_opts, const git_checkout_options
     * *checkout_opts);
     */
    static native int jniMerge(
            long repoPtr, long[] theirHeads, long mergeOptsPtr, long checkoutOpts);

    /** -------- Jni Signature ---------- */
    static native void jniOptionsFree(long optsPtr);

    /** const char *default_driver */
    static native String jniOptionsGetDefaultDriver(long optionsPtr);

    /** git_merge_file_favor_t file_favor */
    static native int jniOptionsGetFileFavor(long optionsPtr);

    /** uint32_t file_flags */
    static native int jniOptionsGetFileFlags(long optionsPtr);

    /** uint32_t flags */
    static native int jniOptionsGetFlags(long optionsPtr);

    /** git_diff_similarity_metric *metric */
    static native long jniOptionsGetMetric(long optionsPtr);

    /** unsigned int recursion_limit */
    static native int jniOptionsGetRecursionLimit(long optionsPtr);

    /** unsigned int rename_threshold */
    static native int jniOptionsGetRenameThreshold(long optionsPtr);

    /** unsigned int target_limit */
    static native int jniOptionsGetTargetLimit(long optionsPtr);

    /** unsigned int version */
    static native int jniOptionsGetVersion(long optionsPtr);

    static native int jniOptionsNew(AtomicLong outOpts, int version);

    /** const char *default_driver */
    static native void jniOptionsSetDefaultDriver(long optionsPtr, String defaultDriver);

    /** git_merge_file_favor_t file_favor */
    static native void jniOptionsSetFileFavor(long optionsPtr, int fileFavor);

    /** uint32_t file_flags */
    static native void jniOptionsSetFileFlags(long optionsPtr, int fileFlags);

    /** uint32_t flags */
    static native void jniOptionsSetFlags(long optionsPtr, int flags);

    /** git_diff_similarity_metric *metric */
    static native void jniOptionsSetMetric(long optionsPtr, long metric);

    /** unsigned int recursion_limit */
    static native void jniOptionsSetRecursionLimit(long optionsPtr, int recursionLimit);

    /** unsigned int rename_threshold */
    static native void jniOptionsSetRenameThreshold(long optionsPtr, int renameThreshold);

    /** unsigned int target_limit */
    static native void jniOptionsSetTargetLimit(long optionsPtr, int targetLimit);

    /** unsigned int version */
    static native void jniOptionsSetVersion(long optionsPtr, int version);

    /**
     * int git_merge_trees( git_index **out, git_repository *repo, const git_tree *ancestor_tree,
     * const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts);
     */
    static native int jniTrees(
            AtomicLong out,
            long repoPtr,
            long ancestorTree,
            long ourTree,
            long theirTree,
            long opts);

    /**
     * Find a merge base between two commits
     *
     * @param repo the repository where the commits exist
     * @param one one of the commits
     * @param two the other commit
     * @return the OID of a merge base between 'one' and 'two'
     * @throws GitException git errors 0 on success, GIT_ENOTFOUND if not found or error code
     */
    @Nullable
    public static Oid base(@Nonnull Repository repo, @Nonnull Oid one, @Nonnull Oid two) {
        Oid out = new Oid();
        int e = jniBase(out, repo.getRawPointer(), one, two);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }

    /**
     * Find merge bases between two commits
     *
     * @param repo the repository where the commits exist
     * @param one one of the commits
     * @param two the other commit
     * @return list of merge base ids, empty if not found
     * @throws GitException 0 on success
     */
    @Nonnull
    public static List<Oid> bases(@Nonnull Repository repo, @Nonnull Oid one, @Nonnull Oid two) {
        OidArray outOids = new OidArray();
        int e = jniBases(outOids, repo.getRawPointer(), one, two);
        if (ENOTFOUND.getCode() == e) {
            return Collections.emptyList();
        }
        Error.throwIfNeeded(e);
        return outOids.getOids();
    }

    /**
     * Merge two trees, producing a `git_index` that reflects the result of the merge. The index may
     * be written as-is to the working directory or checked out. If the index is to be converted to
     * a tree, the caller should resolve any conflicts that arose as part of the merge.
     *
     * <p>The returned index must be freed explicitly with `git_index_free`.
     *
     * @param repo repository that contains the given trees
     * @param ancestorTree the common ancestor between the trees (or null if none)
     * @param ourTree the tree that reflects the destination tree
     * @param theirTree the tree to merge in to `our_tree`
     * @param opts the merge tree options (or null for defaults)
     * @return index of the merge
     * @throws GitException git errors
     */
    @Nonnull
    public static Index trees(
            @Nonnull Repository repo,
            @Nullable Tree ancestorTree,
            @Nonnull Tree ourTree,
            @Nonnull Tree theirTree,
            @Nullable Options opts) {
        Index out = new Index(false, 0);
        Error.throwIfNeeded(
                jniTrees(
                        out._rawPtr,
                        repo.getRawPointer(),
                        ancestorTree == null ? 0 : ancestorTree.getRawPointer(),
                        ourTree.getRawPointer(),
                        theirTree.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return out;
    }

    /**
     * Merge two commits, producing a `git_index` that reflects the result of the merge. The index
     * may be written as-is to the working directory or checked out. If the index is to be converted
     * to a tree, the caller should resolve any conflicts that arose as part of the merge.
     *
     * <p>The returned index must be freed explicitly with `git_index_free`.
     *
     * @param repo repository that contains the given trees
     * @param ourCommit the commit that reflects the destination tree
     * @param theirCommit the commit to merge in to `our_commit`
     * @param opts the merge tree options (or null for defaults)
     * @return index reflecting the merge result
     * @throws GitException git errors
     */
    @Nonnull
    public static Index commits(
            @Nonnull Repository repo,
            @Nullable Commit ourCommit,
            @Nullable Commit theirCommit,
            @Nullable Options opts) {
        Index out = new Index(false, 0);
        Error.throwIfNeeded(
                jniCommits(
                        out._rawPtr,
                        repo.getRawPointer(),
                        ourCommit == null ? 0 : ourCommit.getRawPointer(),
                        theirCommit == null ? 0 : theirCommit.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return out;
    }

    /**
     * Analyzes the given branch(es) and determines the opportunities for merging them into the HEAD
     * of the repository.
     *
     * @param repo the repository to merge
     * @param theirHeads the heads to merge into
     * @return out analysis
     * @throws GitException git errors
     */
    @Nonnull
    public static AnalysisPair analysis(
            @Nonnull Repository repo, @Nonnull List<AnnotatedCommit> theirHeads) {
        AtomicInteger outAnalysis = new AtomicInteger();
        AtomicInteger outPreference = new AtomicInteger();
        Error.throwIfNeeded(
                jniAnalysis(
                        outAnalysis,
                        outPreference,
                        repo.getRawPointer(),
                        theirHeads.stream().mapToLong(AnnotatedCommit::getRawPointer).toArray()));

        //outAnalysis and outPreference value is bitmask, if cast to Enum, should be a Set,
        // imaging outAnalysis maybe is 5(binary:101) , it means AnalysisT.NORMAL and AnalysisT.FASTFORWARD are setted

        return new AnalysisPair(
                IBitEnum.parse(outAnalysis.get(), AnalysisT.class),
                IBitEnum.parse(outPreference.get(), PreferenceT.class),
                outAnalysis.get(),
                outPreference.get());

    }

    /**
     * Analyzes the given branch(es) and determines the opportunities for merging them into a
     * reference.
     *
     * @param repo the repository to merge
     * @param ourRef the reference to perform the analysis from
     * @param theirHeads the heads to merge into
     * @return analysis out
     * @throws GitException git errors
     */
    @Nonnull
    public static AnalysisPair analysisForRef(
            @Nonnull Repository repo,
            @Nullable Reference ourRef,
            @Nonnull List<AnnotatedCommit> theirHeads) {
        AtomicInteger outAnalysis = new AtomicInteger();
        AtomicInteger outPreference = new AtomicInteger();
        Error.throwIfNeeded(
                jniAnalysisForRef(
                        outAnalysis,
                        outPreference,
                        repo.getRawPointer(),
                        ourRef == null ? 0 : ourRef.getRawPointer(),
                        theirHeads.stream().mapToLong(AnnotatedCommit::getRawPointer).toArray()));

        return new AnalysisPair(
                IBitEnum.parse(outAnalysis.get(), AnalysisT.class),
                IBitEnum.parse(outPreference.get(), PreferenceT.class),
                outAnalysis.get(),
                outPreference.get());
    }

    /**
     * Find a merge base given a list of commits
     *
     * @param repo the repository where the commits exist
     * @param inputArray oids of the commits
     * @return the OID of a merge base considering all the commits
     * @throws GitException git errors
     */
    @Nullable
    public static Oid baseMany(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        Oid outOid = new Oid();
        int e = jniBaseMany(outOid, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outOid;
    }

    /**
     * Find all merge bases given a list of commits
     *
     * @param repo the repository where the commits exist
     * @param inputArray oids of the commits
     * @return list of merge base commits or empty list if none found
     * @throws GitException git errors
     */
    @Nonnull
    public static List<Oid> basesMany(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        OidArray outOids = new OidArray();
        int e = jniBasesMany(outOids, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return Collections.emptyList();
        }
        Error.throwIfNeeded(e);
        return outOids.getOids();
    }

    /**
     * Find a merge base in preparation for an octopus merge
     *
     * @param repo the repository where the commits exist
     * @param inputArray oids of the commits
     * @return the OID of a merge base considering all the commits or empty if not found
     * @throws GitException git errors
     */
    @Nullable
    public static Oid baseOctopus(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        Oid outOid = new Oid();
        int e = jniBaseOctopus(outOid, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outOid;
    }

    /**
     * Merge two files as they exist in the in-memory data structures, using the given common
     * ancestor as the baseline, producing a `git_merge_file_result` that reflects the merge result.
     * The `git_merge_file_result` must be freed with `git_merge_file_result_free`.
     *
     * <p>Note that this function does not reference a repository and any configuration must be
     * passed as `git_merge_file_options`.
     *
     * @param ancestor The contents of the ancestor file
     * @param ours The contents of the file in "our" side
     * @param theirs The contents of the file in "their" side
     * @param opts The merge file options or `NULL` for defaults
     * @return The merge result
     * @throws GitException git errors
     */
    @Nonnull
    public static FileResult file(
            @Nonnull FileInput ancestor,
            @Nonnull FileInput ours,
            @Nonnull FileInput theirs,
            @Nullable FileOptions opts) {
        FileResult result = new FileResult(false, 0);
        Error.throwIfNeeded(
                jniFile(
                        result._rawPtr,
                        ancestor.getRawPointer(),
                        ours.getRawPointer(),
                        theirs.getRawPointer(),
                        CAutoReleasable.rawPtr(opts)));
        return result;
    }

    /**
     * Merges the given commit(s) into HEAD, writing the results into the working directory. Any
     * changes are staged for commit and any conflicts are written to the index. Callers should
     * inspect the repository's index after this completes, resolve any conflicts and prepare a
     * commit.
     *
     * <p>For compatibility with git, the repository is put into a merging state. Once the commit is
     * done (or if the uses wishes to abort), you should clear this state by calling
     * `git_repository_state_cleanup()`.
     *
     * @param repo the repository to merge
     * @param theirHeads the heads to merge into
     * @param mergeOpts merge options
     * @param checkoutOpts checkout options
     * @throws GitException git errors
     */
    public static void merge(
            @Nonnull Repository repo,
            @Nonnull List<AnnotatedCommit> theirHeads,
            @Nullable Options mergeOpts,
            @Nullable Checkout.Options checkoutOpts) {
        Error.throwIfNeeded(
                jniMerge(
                        repo.getRawPointer(),
                        theirHeads.stream().mapToLong(CAutoReleasable::getRawPointer).toArray(),
                        CAutoReleasable.rawPtr(mergeOpts),
                        CAutoReleasable.rawPtr(checkoutOpts)));
    }

    /**
     * Flags for `git_merge` options. A combination of these flags can be passed in via the `flags`
     * value in the `git_merge_options`.
     */
    public enum FlagT implements IBitEnum {
        /**
         * Detect renames that occur between the common ancestor and the "ours" side or the common
         * ancestor and the "theirs" side. This will enable the ability to merge between a modified
         * and renamed file.
         */
        FIND_RENAMES(1 << 0),

        /**
         * If a conflict occurs, exit immediately instead of attempting to continue resolving
         * conflicts. The merge operation will fail with GIT_EMERGECONFLICT and no index will be
         * returned.
         */
        FAIL_ON_CONFLICT(1 << 1),

        /** Do not write the REUC extension on the generated index */
        SKIP_REUC(1 << 2),

        /**
         * If the commits being merged have multiple merge bases, do not build a recursive merge
         * base (by merging the multiple merge bases), instead simply use the first base. This flag
         * provides a similar merge base to `git-merge-resolve`.
         */
        NO_RECURSIVE(1 << 3);

        private final int _bit;

        FlagT(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Merge file favor options for `git_merge_options` instruct the file-level merging
     * functionality how to deal with conflicting regions of the files.
     */
    public enum FileFavorT {
        /**
         * When a region of a file is changed in both branches, a conflict will be recorded in the
         * index so that `git_checkout` can produce a merge file with conflict markers in the
         * working directory. This is the default.
         */
        NORMAL(0),

        /**
         * When a region of a file is changed in both branches, the file created in the index will
         * contain the "ours" side of any conflicting region. The index will not record a conflict.
         */
        OURS(1),

        /**
         * When a region of a file is changed in both branches, the file created in the index will
         * contain the "theirs" side of any conflicting region. The index will not record a
         * conflict.
         */
        THEIRS(2),

        /**
         * When a region of a file is changed in both branches, the file created in the index will
         * contain each unique line from each side) which has the result of combining both files.
         * The index will not record a conflict.
         */
        UNION(3);

        private final int bit;

        FileFavorT(int bit) {
            this.bit = bit;
        }
    }

    /** File merging flags */
    public enum FileFlagT implements IBitEnum {
        /** Defaults */
        DEFAULT(0),

        /** Create standard conflicted merge files */
        STYLE_MERGE(1 << 0),

        /** Create diff3-style files */
        STYLE_DIFF3(1 << 1),

        /** Condense non-alphanumeric regions for simplified diff file */
        SIMPLIFY_ALNUM(1 << 2),

        /** Ignore all whitespace */
        IGNORE_WHITESPACE(1 << 3),

        /** Ignore changes in amount of whitespace */
        IGNORE_WHITESPACE_CHANGE(1 << 4),

        /** Ignore whitespace at end of line */
        IGNORE_WHITESPACE_EOL(1 << 5),

        /** Use the "patience diff" algorithm */
        DIFF_PATIENCE(1 << 6),

        /** Take extra time to find minimal diff */
        DIFF_MINIMAL(1 << 7);

        private final int _bit;

        FileFlagT(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /** The results of `git_merge_analysis` indicate the merge opportunities. */
    public enum AnalysisT implements IBitEnum {
        /** No merge is possible. (Unused.) */
        NONE(0),

        /**
         * A "normal" merge; both HEAD and the given merge input have diverged from their common
         * ancestor. The divergent commits must be merged.
         */
        NORMAL(1 << 0),

        /**
         * All given merge inputs are reachable from HEAD, meaning the repository is up-to-date and
         * no merge needs to be performed.
         */
        UP_TO_DATE(1 << 1),

        /**
         * The given merge input is a fast-forward from HEAD and no merge needs to be performed.
         * Instead, the client can check out the given merge input.
         */
        FASTFORWARD(1 << 2),

        /**
         * The HEAD of the current repository is "unborn" and does not point to a valid commit. No
         * merge can be performed, but the caller may wish to simply set HEAD to the target
         * commit(s).
         */
        UNBORN(1 << 3);

        private final int _bit;

        AnalysisT(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /** The user's stated preference for merges. */
    public enum PreferenceT implements IBitEnum {
        /** No configuration was found that suggests a preferred behavior for merge. */
        NONE(0),

        /**
         * There is a `merge.ff=false` configuration setting, suggesting that the user does not want
         * to allow a fast-forward merge.
         */
        NO_FASTFORWARD(1 << 0),

        /**
         * There is a `merge.ff=only` configuration setting, suggesting that the user only wants
         * fast-forward merges.
         */
        FASTFORWARD_ONLY(1 << 1);

        private final int _bit;

        PreferenceT(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * The file inputs to `git_merge_file`. Callers should populate the `git_merge_file_input`
     * structure with descriptions of the files in each side of the conflict for use in producing
     * the merge file.
     */
    public static class FileInput extends CAutoReleasable {
        public static final int VERSION = 1;

        FileInput(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        public static FileInput createDefault() {
            return create(VERSION);
        }

        @Nonnull
        public static FileInput create(int version) {
            FileInput out = new FileInput(false, 0);
            Error.throwIfNeeded(jniFileInputNew(out._rawPtr, version));
            return out;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileInputFree(cPtr);
        }

        public int getVersion() {
            return jniFileInputGetVersion(getRawPointer());
        }

        public void setVersion(int version) {
            jniFileInputSetVersion(getRawPointer(), version);
        }

        public String getPtr() {
            return jniFileInputGetPtr(getRawPointer());
        }

        public void setPtr(String ptr) {
            jniFileInputSetPtr(getRawPointer(), ptr);
            jniFileInputSetSize(getRawPointer(), ptr.length());
        }

        public int getSize() {
            return jniFileInputGetSize(getRawPointer());
        }

        public String getPath() {
            return jniFileInputGetPath(getRawPointer());
        }

        public void setPath(String path) {
            jniFileInputSetPath(getRawPointer(), path);
        }

        public int getMode() {
            return jniFileInputGetMode(getRawPointer());
        }

        public void setMode(int mode) {
            jniFileInputSetMode(getRawPointer(), mode);
        }
    }

    /** options for merging a file */
    public static class FileOptions extends CAutoReleasable {
        FileOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static FileOptions create(int version) {
            FileOptions opts = new FileOptions(false, 0);
            Error.throwIfNeeded(jniFileOptionsNew(opts._rawPtr, version));
            return opts;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileOptionsFree(cPtr);
        }

        public int getVersion() {
            return jniFileOptionsGetVersion(getRawPointer());
        }

        public void setVersion(int version) {
            jniFileOptionsSetVersion(getRawPointer(), version);
        }

        public String getAncestorLabel() {
            return jniFileOptionsGetAncestorLabel(getRawPointer());
        }

        public void setAncestorLabel(String ancestorLabel) {
            jniFileOptionsSetAncestorLabel(getRawPointer(), ancestorLabel);
        }

        public String getOurLabel() {
            return jniFileOptionsGetOurLabel(getRawPointer());
        }

        public void setOurLabel(String ourLabel) {
            jniFileOptionsSetOurLabel(getRawPointer(), ourLabel);
        }

        public String getTheirLabel() {
            return jniFileOptionsGetTheirLabel(getRawPointer());
        }

        public void setTheirLabel(String theirLabel) {
            jniFileOptionsSetTheirLabel(getRawPointer(), theirLabel);
        }

        public int getFavor() {
            return jniFileOptionsGetFavor(getRawPointer());
        }

        public void setFavor(int favor) {
            jniFileOptionsSetFavor(getRawPointer(), favor);
        }

        public int getFlags() {
            return jniFileOptionsGetFlags(getRawPointer());
        }

        public void setFlags(int flags) {
            jniFileOptionsSetFlags(getRawPointer(), flags);
        }

        public int getMarkerSize() {
            return jniFileOptionsGetMarkerSize(getRawPointer());
        }

        public void setMarkerSize(int markerSize) {
            jniFileOptionsSetMarkerSize(getRawPointer(), markerSize);
        }
    }

    public static class FileResult extends CAutoReleasable {

        protected FileResult(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileResultFree(cPtr);
        }

        public boolean getAutomergeable() {
            return jniFileResultGetAutomergeable(getRawPointer()) != 0;
        }

        public String getPath() {
            return jniFileResultGetPath(getRawPointer());
        }

        public int getMode() {
            return jniFileResultGetMode(getRawPointer());
        }

        public String getPtr() {
            return jniFileResultGetPtr(getRawPointer());
        }

        public int getLen() {
            return jniFileResultGetLen(getRawPointer());
        }
    }

    /** Merging options */
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        @Nonnull
        public static Options create() {
            return create(CURRENT_VERSION);
        }

        @Nonnull
        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }

        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }

        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }

        public int getFlags() {
            return jniOptionsGetFlags(getRawPointer());
        }

        public void setFlags(int flags) {
            jniOptionsSetFlags(getRawPointer(), flags);
        }

        public int getRenameThreshold() {
            return jniOptionsGetRenameThreshold(getRawPointer());
        }

        public void setRenameThreshold(int renameThreshold) {
            jniOptionsSetRenameThreshold(getRawPointer(), renameThreshold);
        }

        public int getTargetLimit() {
            return jniOptionsGetTargetLimit(getRawPointer());
        }

        public void setTargetLimit(int targetLimit) {
            jniOptionsSetTargetLimit(getRawPointer(), targetLimit);
        }

        public long getMetric() {
            return jniOptionsGetMetric(getRawPointer());
        }

        public void setMetric(long metric) {
            jniOptionsSetMetric(getRawPointer(), metric);
        }

        public int getRecursionLimit() {
            return jniOptionsGetRecursionLimit(getRawPointer());
        }

        public void setRecursionLimit(int recursionLimit) {
            jniOptionsSetRecursionLimit(getRawPointer(), recursionLimit);
        }

        public String getDefaultDriver() {
            return jniOptionsGetDefaultDriver(getRawPointer());
        }

        public void setDefaultDriver(String defaultDriver) {
            jniOptionsSetDefaultDriver(getRawPointer(), defaultDriver);
        }

        public int getFileFavor() {
            return jniOptionsGetFileFavor(getRawPointer());
        }

        public void setFileFavor(int fileFavor) {
            jniOptionsSetFileFavor(getRawPointer(), fileFavor);
        }

        public int getFileFlags() {
            return jniOptionsGetFileFlags(getRawPointer());
        }

        public void setFileFlags(int fileFlags) {
            jniOptionsSetFileFlags(getRawPointer(), fileFlags);
        }
    }

    /**
     * POJO contains analysis result from {@code Merge::analysis} and {@code Merge::analysisForRef}
     * Can use `Set.contains()` for check a bit mask of Enum is ON or OFF, or get the value do bit operate by your self.
     * eg: `analysisValue & AnalysisT.NORMAL.getBit()`, if >0, means the `AnalysisT.NORMAL` bit field is ON.
     * eg2: `analysisSet.contains(AnalysisT.NORMAL)`, if true, means the `AnalysisT.NORMAL` bit field is ON.
     */
    public static class AnalysisPair {
        private final EnumSet<AnalysisT> analysisSet;
        private final EnumSet<PreferenceT> preferenceSet;
        private final int analysisValue;  // bitmask value, eg: 5(binary 101), means include AnalysisT.NORMAL and AnalysisT.FASTFORWARD
        private final int preferenceValue;  // bitmask value

        public AnalysisPair(EnumSet<AnalysisT> analysisSet, EnumSet<PreferenceT> preferenceSet, int analysisValue ,int preferenceValue) {
            this.analysisSet = analysisSet;
            this.preferenceSet = preferenceSet;
            this.analysisValue = analysisValue;
            this.preferenceValue = preferenceValue;
        }

        public EnumSet<AnalysisT> getAnalysisSet() {
            return analysisSet;
        }

        public EnumSet<PreferenceT> getPreferenceSet() {
            return preferenceSet;
        }

        public int getAnalysisValue() {
            return analysisValue;
        }

        public int getPreferenceValue() {
            return preferenceValue;
        }
    }
}

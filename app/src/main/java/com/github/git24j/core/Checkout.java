package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Checkout {

    /** int git_checkout_head(git_repository *repo, const git_checkout_options *opts); */
    static native int jniHead(long repoPtr, long opts);

    /**
     * int git_checkout_index(git_repository *repo, git_index *index, const git_checkout_options
     * *opts);
     */
    static native int jniIndex(long repoPtr, long index, long opts);

    /** int git_checkout_init_options(git_checkout_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    static native String jniOptionsGetAncestorLabel(long opts);

    static native long jniOptionsGetBaseline(long opts);

    static native long jniOptionsGetBaselineIndex(long opts);

    /** unsigned int git_checkout_options_get_dir_mode(const git_checkout_options *opts); */
    static native int jniOptionsGetDirMode(long opts);

    /** int git_checkout_options_get_disable_filters(const git_checkout_options *opts); */
    static native int jniOptionsGetDisableFilters(long opts);

    /** unsigned int git_checkout_options_get_file_mode(const git_checkout_options *opts); */
    static native int jniOptionsGetFileMode(long opts);

    /** int git_checkout_options_get_file_open_flags(const git_checkout_options *opts); */
    static native int jniOptionsGetFileOpenFlags(long opts);

    /** unsigned int git_checkout_options_get_notify_flags(const git_checkout_options *opts); */
    static native int jniOptionsGetNotifyFlags(long opts);

    static native String jniOptionsGetOurLabel(long opts);

    static native void jniOptionsGetPaths(long opts, List<String> outPathList);

    /** unsigned int git_checkout_options_get_strategy(const git_checkout_options *opts); */
    static native int jniOptionsGetStrategy(long opts);

    /** -------- Jni Signature ---------- */
    static native String jniOptionsGetTargetDirectory(long opts);

    static native String jniOptionsGetTheirLable(long opts);

    /** unsigned int git_checkout_options_get_version(const git_checkout_options *opts); */
    static native int jniOptionsGetVersion(long opts);

    static native int jniOptionsNew(AtomicLong outPots, int version);

    /**
     * void git_checkout_options_set_ancestor_label(const git_checkout_options *opts, const char
     * *ancestor_label);
     */
    static native void jniOptionsSetAncestorLabel(long opts, String ancestorLabel);

    /**
     * void git_checkout_options_set_baseline(const git_checkout_options *opts, git_tree *baseline);
     */
    static native void jniOptionsSetBaseline(long opts, long baseline);

    /**
     * void git_checkout_options_set_baseline_index(const git_checkout_options *opts, git_index
     * *baseline_index);
     */
    static native void jniOptionsSetBaselineIndex(long opts, long baselineIndex);

    /**
     * void git_checkout_options_set_dir_mode(const git_checkout_options *opts, unsigned int mode);
     */
    static native void jniOptionsSetDirMode(long opts, int mode);

    /**
     * void git_checkout_options_set_disable_filters(const git_checkout_options *opts, int
     * disalbe_filters);
     */
    static native void jniOptionsSetDisableFilters(long opts, int disalbeFilters);

    /**
     * void git_checkout_options_set_file_mode(const git_checkout_options *opts, unsigned int mode);
     */
    static native void jniOptionsSetFileMode(long opts, int mode);

    /**
     * void git_checkout_options_set_file_open_flags(const git_checkout_options *opts, int flags);
     */
    static native void jniOptionsSetFileOpenFlags(long opts, int flags);

    static native void jniOptionsSetNotifyCb(long optsPtr, Internals.ISJJJCallback notifyCb);

    /**
     * void git_checkout_options_set_notify_flags(const git_checkout_options *opts, unsigned int
     * flags);
     */
    static native void jniOptionsSetNotifyFlags(long opts, int flags);

    /**
     * void git_checkout_options_set_our_label(const git_checkout_options *opts, const char
     * *our_label);
     */
    static native void jniOptionsSetOurLabel(long opts, String ourLabel);

    /**
     * void git_checkout_options_set_paths(const git_checkout_options *opts, git_strarray paths);
     */
    static native void jniOptionsSetPaths(long opts, String[] paths);

    static native void jniOptionsSetPerfdataCb(long optsPtr, PerfdataCb perfdataCb);

    static native void jniOptionsSetProgressCb(long optsPtr, ProgressCb progressCb);

    /**
     * void git_checkout_options_set_strategy(const git_checkout_options *opts, unsigned int
     * strategy);
     */
    static native void jniOptionsSetStrategy(long opts, int strategy);

    /**
     * void git_checkout_options_set_target_directory(const git_checkout_options *opts, const char
     * *target_directory);
     */
    static native void jniOptionsSetTargetDirectory(long opts, String targetDirectory);

    /**
     * void git_checkout_options_set_their_lable(const git_checkout_options *opts, const char
     * *their_label);
     */
    static native void jniOptionsSetTheirLable(long opts, String theirLabel);

    /**
     * void git_checkout_options_set_version(const git_checkout_options *opts, unsigned int
     * version);
     */
    static native void jniOptionsSetVersion(long opts, int version);

    /**
     * int git_checkout_tree(git_repository *repo, const git_object *treeish, const
     * git_checkout_options *opts);
     */
    static native int jniTree(long repoPtr, long treeish, long opts);

    /**
     * Updates files in the index and the working tree to match the content of the commit pointed at
     * by HEAD.
     *
     * <p>Note that this is _not_ the correct mechanism used to switch branches; do not change your
     * `HEAD` and then call this method, that would leave you with checkout conflicts since your
     * working directory would then appear to be dirty. Instead, checkout the target of the branch
     * and then update `HEAD` using `git_repository_set_head` to point to the branch you checked
     * out.
     *
     * @param repo repository to check out (must be non-bare)
     * @param opts specifies checkout options (may be NULL)
     * @return code returned by `notify_cb`
     * @throws GitException GIT_EUNBORNBRANCH if HEAD points to a non existing branch non-zero value
     *     returned by `notify_cb`, or other error code < 0 (use git_error_last for error details)
     */
    public static int head(@Nonnull Repository repo, @Nullable Options opts) {
        int e = jniHead(repo.getRawPointer(), opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Updates files in the working tree to match the content of the index.
     *
     * @param repo repository into which to check out (must be non-bare)
     * @param index index to be checked out (or NULL to use repository index)
     * @param opts specifies checkout options (may be NULL)
     * @return non-zero return value from `notify_cb`,
     * @throws GitException git errors
     */
    public static int index(
            @Nonnull Repository repo, @Nullable Index index, @Nullable Options opts) {
        int e =
                jniIndex(
                        repo.getRawPointer(),
                        index == null ? 0 : index.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * Updates files in the index and working tree to match the content of the tree pointed at by
     * the treeish.
     *
     * @param repo repository to check out (must be non-bare)
     * @param treeish a commit, tag or tree which content will be used to update the working
     *     directory (or NULL to use HEAD)
     * @param opts specifies checkout options (may be NULL)
     * @return non-zero return value from `notify_cb`,
     * @throws GitException git errors
     */
    public static int tree(
            @Nonnull Repository repo, @Nullable GitObject treeish, @Nullable Options opts) {
        int e =
                jniTree(
                        repo.getRawPointer(),
                        treeish == null ? 0 : treeish.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }

    public enum StrategyT implements IBitEnum {
        /** Allow safe updates that cannot overwrite uncommitted data */
        // updated from 1 to 0, since libgit2 1.9.0
        SAFE(0),

        /** Allow all updates to force working directory to look like index */
        FORCE(1 << 1),

        /** Allow checkout to recreate missing files */
        RECREATE_MISSING(1 << 2),

        /** Allow checkout to make safe updates even if conflicts are found */
        ALLOW_CONFLICTS(1 << 4),

        /** Remove untracked files not in index (that are not ignored) */
        REMOVE_UNTRACKED(1 << 5),

        /** Remove ignored files not in index */
        REMOVE_IGNORED(1 << 6),

        /** Only update existing files, don't create new ones */
        UPDATE_ONLY(1 << 7),

        /**
         * Normally checkout updates index entries as it goes; this stops that. Implies
         * `DONT_WRITE_INDEX`.
         */
        DONT_UPDATE_INDEX(1 << 8),

        /** Don't refresh index/config/etc before doing checkout */
        NO_REFRESH(1 << 9),

        /** Allow checkout to skip unmerged files */
        SKIP_UNMERGED(1 << 10),
        /** For unmerged files, checkout stage 2 from index */
        USE_OURS(1 << 11),
        /** For unmerged files, checkout stage 3 from index */
        USE_THEIRS(1 << 12),

        /** Treat pathspec as simple list of exact match file paths */
        DISABLE_PATHSPEC_MATCH(1 << 13),

        /** Ignore directories in use, they will be left empty */
        SKIP_LOCKED_DIRECTORIES(1 << 18),

        /** Don't overwrite ignored files that exist in the checkout target */
        DONT_OVERWRITE_IGNORED(1 << 19),

        /** Write normal merge files for conflicts */
        CONFLICT_STYLE_MERGE(1 << 20),

        /** Include common ancestor data in diff3 format files for conflicts */
        CONFLICT_STYLE_DIFF3(1 << 21),

        /** Don't overwrite existing files or folders */
        DONT_REMOVE_EXISTING(1 << 22),

        /** Normally checkout writes the index upon completion; this prevents that. */
        DONT_WRITE_INDEX(1 << 23),


        /**
         * Perform a "dry run", reporting what _would_ be done but
         * without actually making changes in the working directory
         * or the index.
         */
        DRY_RUN(1 << 24),

        /** Include common ancestor data in zdiff3 format for conflicts */
        CONFLICT_STYLE_ZDIFF3(1 << 25),

        /**
         * Do not do a checkout and do not fire callbacks; this is primarily
         * useful only for internal functions that will perform the
         * checkout themselves but need to pass checkout options into
         * another function, for example, `git_clone`.
        */
        NONE(1 << 30),


        /*
         * THE FOLLOWING OPTIONS ARE NOT YET IMPLEMENTED
         */


        /** Recursively checkout submodules with same options (NOT IMPLEMENTED) */
        UPDATE_SUBMODULES(1 << 16),

        /** Recursively checkout submodules if HEAD moved in super repo (NOT IMPLEMENTED) */
        UPDATE_SUBMODULES_IF_CHANGED(1 << 17);


        private final int _bit;

        StrategyT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum NotifyT implements IBitEnum {
        NONE(0),
        CONFLICT(1 << 0),
        DIRTY(1 << 1),
        UPDATED(1 << 2),
        UNTRACKED(1 << 3),
        IGNORED(1 << 4),
        ALL(0x0FFFF);
        private final int _bit;

        NotifyT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface NotifyCb {
        int accept(
                NotifyT why, String path, Diff.File baseline, Diff.File target, Diff.File workdir);
    }

    @FunctionalInterface
    public interface ProgressCb {
        void accept(@CheckForNull String path, int completedSteps, int totalSteps);
    }

    @FunctionalInterface
    public interface PerfdataCb {
        void accept(int mkdirCalls, int statCalls, int chmodCalls);
    }

    /**
     * Checkout options structure
     *
     * <p>Initialize with `GIT_CHECKOUT_OPTIONS_INIT`. Alternatively, you can use
     * `git_checkout_init_options`.
     */
    public static class Options extends CAutoReleasable {
        public static int GIT_CHECKOUT_OPTIONS_VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }

        public static Options defaultOptions() {
            return create(GIT_CHECKOUT_OPTIONS_VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }

        public void setVersion(int ver) {
            jniOptionsSetVersion(getRawPointer(), ver);
        }

        /** @return checkout strategy, default is SAFE */
        @Nonnull
        public EnumSet<StrategyT> getStrategy() {
            return IBitEnum.parse(jniOptionsGetStrategy(getRawPointer()), StrategyT.class);
        }

        public void setStrategy(@Nonnull EnumSet<StrategyT> strategies) {
            jniOptionsSetStrategy(getRawPointer(), IBitEnum.bitOrAll(strategies));
        }

        public boolean getDisableFilter() {
            return jniOptionsGetDisableFilters(getRawPointer()) == 1;
        }

        /** don't apply filters like CRLF conversion */
        public void setDisableFilter(boolean disableFilter) {
            jniOptionsSetDisableFilters(getRawPointer(), disableFilter ? 1 : 0);
        }

        /** default is 0755 */
        public int getDirMode() {
            return jniOptionsGetDirMode(getRawPointer());
        }

        public void setDirMode(int mode) {
            jniOptionsSetDirMode(getRawPointer(), mode);
        }

        /** default is 0644 or 0755 as dictated by blob */
        public int getFileMode() {
            return jniOptionsGetFileMode(getRawPointer());
        }

        public void setFileMode(int mode) {
            jniOptionsSetFileMode(getRawPointer(), mode);
        }

        /** default is O_CREAT | O_TRUNC | O_WRONLY */
        public int getOpenFlags() {
            return jniOptionsGetFileOpenFlags(getRawPointer());
        }

        public void setOpenFlags(int flags) {
            jniOptionsSetFileOpenFlags(getRawPointer(), flags);
        }

        /** see `git_checkout_notify_t` above */
        public EnumSet<NotifyT> getNotifyFlags() {
            int flags = jniOptionsGetNotifyFlags(getRawPointer());
            EnumSet<NotifyT> candidates = EnumSet.allOf(NotifyT.class);
            candidates.remove(NotifyT.NONE);
            candidates.remove(NotifyT.ALL);
            EnumSet<NotifyT> res = IBitEnum.parse(flags, candidates);
            if (res == null) {
                return EnumSet.of(NotifyT.NONE);
            }
            if (res.size() == candidates.size()) {
                return EnumSet.of(NotifyT.ALL);
            }
            return res;
        }

        public void setNotifyFlags(EnumSet<NotifyT> flags) {
            jniOptionsSetNotifyFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }

        public void setNotifyCb(@Nonnull NotifyCb callback) {
            jniOptionsSetNotifyCb(
                    getRawPointer(),
                    (why, s, basePtr, targePtr, workdirPtr) ->
                            callback.accept(
                                    IBitEnum.valueOf(why, NotifyT.class),
                                    s,
                                    Diff.File.ofWeak(basePtr),
                                    Diff.File.ofWeak(targePtr),
                                    Diff.File.ofWeak(workdirPtr)));
        }

        public void setProgressCb(@Nonnull ProgressCb callback) {
            jniOptionsSetProgressCb(getRawPointer(), callback);
        }

        public void setPerfdataCb(@Nonnull PerfdataCb callback) {
            jniOptionsSetPerfdataCb(getRawPointer(), callback);
        }

        public List<String> getPaths() {
            List<String> out = new ArrayList<>();
            jniOptionsGetPaths(getRawPointer(), out);
            return out;
        }

        public void setPaths(@Nonnull String[] paths) {
            jniOptionsSetPaths(getRawPointer(), paths);
        }

        @Nullable
        public Tree getBaseline() {
            long ptr = jniOptionsGetBaseline(getRawPointer());
            return ptr == 0 ? null : new Tree(true, ptr);
        }

        public void setBaseline(@Nonnull Tree baseline) {
            jniOptionsSetBaseline(getRawPointer(), baseline.getRawPointer());
        }

        @Nullable
        public Index getBaselineIndex() {
            long ptr = jniOptionsGetBaselineIndex(getRawPointer());
            return ptr == 0 ? null : new Index(true, ptr);
        }

        public void setBaselineIndex(@Nonnull Index baselineIndex) {
            jniOptionsSetBaselineIndex(getRawPointer(), baselineIndex.getRawPointer());
        }

        public String getTargetDirectory() {
            return jniOptionsGetTargetDirectory(getRawPointer());
        }

        public void setTargetDirectory(@Nonnull String targetDirectory) {
            jniOptionsSetTargetDirectory(getRawPointer(), targetDirectory);
        }

        public String getAncestorLabel() {
            return jniOptionsGetAncestorLabel(getRawPointer());
        }

        public void setAncestorLabel(@Nonnull String ancestorLabel) {
            jniOptionsSetAncestorLabel(getRawPointer(), ancestorLabel);
        }

        public String getOurLabel() {
            return jniOptionsGetOurLabel(getRawPointer());
        }

        public void setOurLabel(@Nonnull String ourLabel) {
            jniOptionsSetOurLabel(getRawPointer(), ourLabel);
        }

        public String getTheirLabel() {
            return jniOptionsGetTheirLable(getRawPointer());
        }

        public void setTheirLabel(@Nonnull String theirLabel) {
            jniOptionsSetTheirLable(getRawPointer(), theirLabel);
        }
    }
}

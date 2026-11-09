package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Status {

    /** const git_status_entry * git_status_byindex(git_status_list *statuslist, size_t idx); */
    static native long jniByindex(long statuslist, int idx);

    /** git_diff_delta *head_to_index */
    static native long jniEntryGetHeadToIndex(long entryPtr);

    /** git_diff_delta *index_to_workdir */
    static native long jniEntryGetIndexToWorkdir(long entryPtr);

    /** git_status_t status */
    static native int jniEntryGetStatus(long entryPtr);

    /** int git_status_file(unsigned int *status_flags, git_repository *repo, const char *path); */
    static native int jniFile(AtomicInteger statusFlags, long repoPtr, String path);

    /** int git_status_init_options(git_status_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /** size_t git_status_list_entrycount(git_status_list *statuslist); */
    static native int jniListEntrycount(long statuslist);

    /** void git_status_list_free(git_status_list *statuslist); */
    static native void jniListFree(long statuslist);

    /**
     * int git_status_list_new(git_status_list **out, git_repository *repo, const git_status_options
     * *opts);
     */
    static native int jniListNew(AtomicLong out, long repoPtr, long opts);

    /** git_tree *baseline */
    static native long jniOptionsGetBaseline(long optionsPtr);

    /** unsigned int flags */
    static native int jniOptionsGetFlags(long optionsPtr);

    /** git_strarray pathspec */
    static native void jniOptionsGetPathspec(long optionsPtr, List<String> outPathSpec);

    /** git_status_show_t show */
    static native int jniOptionsGetShow(long optionsPtr);

    /** unsigned int version */
    static native int jniOptionsGetVersion(long optionsPtr);

    static native int jniOptionsNew(AtomicLong outOpts, int version);

    /** git_tree *baseline */
    static native void jniOptionsSetBaseline(long optionsPtr, long baseline);

    /** unsigned int flags */
    static native void jniOptionsSetFlags(long optionsPtr, int flags);

    /** git_strarray pathspec */
    static native void jniOptionsSetPathspec(long optionsPtr, String[] pathspec);

    /** git_status_show_t show */
    static native void jniOptionsSetShow(long optionsPtr, int show);

    /** unsigned int version */
    static native void jniOptionsSetVersion(long optionsPtr, int version);

    /** int git_status_should_ignore(int *ignored, git_repository *repo, const char *path); */
    static native int jniShouldIgnore(AtomicInteger ignored, long repoPtr, String path);

    /**
     * Test if the ignore rules apply to a given file.
     *
     * <p>This function checks the ignore rules to see if they would apply to the given file. This
     * indicates if the file would be ignored regardless of whether the file is already in the index
     * or committed to the repository.
     *
     * <p>One way to think of this is if you were to do "git add ." on the directory containing the
     * file, would it be added or not?
     *
     * @param repo A repository object
     * @param path The file to check ignores for, rooted at the repo's workdir.
     * @return false if the file is not ignored, true if it is
     * @throws GitException error if ignore rules could not be processed for the file (regardless of
     *     whether it exists or not)
     */
    public static boolean shouldIgnore(@Nonnull Repository repo, @Nonnull String path) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniShouldIgnore(out, repo.getRawPointer(), path));
        return out.get() == 1;
    }

    /**
     * Get file status for a single file.
     *
     * <p>This tries to get status for the filename that you give. If no files match that name (in
     * either the HEAD, index, or working directory), this returns GIT_ENOTFOUND.
     *
     * <p>If the name matches multiple files (for example, if the `path` names a directory or if
     * running on a case- insensitive filesystem and yet the HEAD has two entries that both match
     * the path), then this returns GIT_EAMBIGUOUS because it cannot give correct results.
     *
     * <p>This does not do any sort of rename detection. Renames require a set of targets and
     * because of the path filtering, there is not enough information to check renames correctly. To
     * check file status with rename detection, there is no choice but to do a full
     * `git_status_list_new` and scan through looking for the path that you are interested in.
     *
     * @param repo A repository object
     * @param path The exact path to retrieve status for relative to the repository working
     *     directory
     * @return combination of StatusT values for file
     * @throws GitException GIT_ENOTFOUND if the file is not found in the HEAD, index, and work
     *     tree, GIT_EAMBIGUOUS if `path` matches multiple files or if it refers to a folder, or
     *     other errors.
     */
    public static EnumSet<StatusT> file(@Nonnull Repository repo, @Nonnull String path) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniFile(out, repo.getRawPointer(), path));
        return IBitEnum.parse(out.get(), StatusT.class);
    }

    /**
     * Status flags for a single file.
     *
     * <p>A combination of these values will be returned to indicate the status of a file. Status
     * compares the working directory, the index, and the current HEAD of the repository. The
     * `GIT_STATUS_INDEX` set of flags represents the status of file in the index relative to the
     * HEAD, and the `GIT_STATUS_WT` set of flags represent the status of the file in the working
     * directory relative to the index.
     */
    public enum StatusT implements IBitEnum {
        CURRENT(0),

        INDEX_NEW(1 << 0),
        INDEX_MODIFIED(1 << 1),
        INDEX_DELETED(1 << 2),
        INDEX_RENAMED(1 << 3),
        INDEX_TYPECHANGE(1 << 4),

        WT_NEW(1 << 7),
        WT_MODIFIED(1 << 8),
        WT_DELETED(1 << 9),
        WT_TYPECHANGE(1 << 10),
        WT_RENAMED(1 << 11),
        WT_UNREADABLE(1 << 12),

        IGNORED(1 << 14),
        CONFLICTED(1 << 15),
        ;
        private final int _bit;

        StatusT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Select the files on which to report status.
     *
     * <p>With `git_status_foreach_ext`, this will control which changes get callbacks. With
     * `git_status_list_new`, these will control which changes are included in the list.
     *
     * <pre>
     * - GIT_STATUS_SHOW_INDEX_AND_WORKDIR is the default.  This roughly
     *   matches `git status --porcelain` regarding which files are
     *   included and in what order.
     * - GIT_STATUS_SHOW_INDEX_ONLY only gives status based on HEAD to index
     *   comparison, not looking at working directory changes.
     * - GIT_STATUS_SHOW_WORKDIR_ONLY only gives status based on index to
     *   working directory comparison, not comparing the index to the HEAD.
     *   </pre>
     */
    public enum ShowT implements IBitEnum {
        INDEX_AND_WORKDIR(0),
        INDEX_ONLY(1),
        WORKDIR_ONLY(2),
        ;
        private final int _bit;

        ShowT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Flags to control status callbacks
     *
     * <pre>
     * - GIT_STATUS_OPT_INCLUDE_UNTRACKED says that callbacks should be made
     *   on untracked files.  These will only be made if the workdir files are
     *   included in the status "show" option.
     * - GIT_STATUS_OPT_INCLUDE_IGNORED says that ignored files get callbacks.
     *   Again, these callbacks will only be made if the workdir files are
     *   included in the status "show" option.
     * - GIT_STATUS_OPT_INCLUDE_UNMODIFIED indicates that callback should be
     *   made even on unmodified files.
     * - GIT_STATUS_OPT_EXCLUDE_SUBMODULES indicates that submodules should be
     *   skipped.  This only applies if there are no pending typechanges to
     *   the submodule (either from or to another type).
     * - GIT_STATUS_OPT_RECURSE_UNTRACKED_DIRS indicates that all files in
     *   untracked directories should be included.  Normally if an entire
     *   directory is new, then just the top-level directory is included (with
     *   a trailing slash on the entry name).  This flag says to include all
     *   of the individual files in the directory instead.
     * - GIT_STATUS_OPT_DISABLE_PATHSPEC_MATCH indicates that the given path
     *   should be treated as a literal path, and not as a pathspec pattern.
     * - GIT_STATUS_OPT_RECURSE_IGNORED_DIRS indicates that the contents of
     *   ignored directories should be included in the status.  This is like
     *   doing `git ls-files -o -i --exclude-standard` with core git.
     * - GIT_STATUS_OPT_RENAMES_HEAD_TO_INDEX indicates that rename detection
     *   should be processed between the head and the index and enables
     *   the GIT_STATUS_INDEX_RENAMED as a possible status flag.
     * - GIT_STATUS_OPT_RENAMES_INDEX_TO_WORKDIR indicates that rename
     *   detection should be run between the index and the working directory
     *   and enabled GIT_STATUS_WT_RENAMED as a possible status flag.
     * - GIT_STATUS_OPT_SORT_CASE_SENSITIVELY overrides the native case
     *   sensitivity for the file system and forces the output to be in
     *   case-sensitive order
     * - GIT_STATUS_OPT_SORT_CASE_INSENSITIVELY overrides the native case
     *   sensitivity for the file system and forces the output to be in
     *   case-insensitive order
     * - GIT_STATUS_OPT_RENAMES_FROM_REWRITES indicates that rename detection
     *   should include rewritten files
     * - GIT_STATUS_OPT_NO_REFRESH bypasses the default status behavior of
     *   doing a "soft" index reload (i.e. reloading the index data if the
     *   file on disk has been modified outside libgit2).
     * - GIT_STATUS_OPT_UPDATE_INDEX tells libgit2 to refresh the stat cache
     *   in the index for files that are unchanged but have out of date stat
     *   information in the index.  It will result in less work being done on
     *   subsequent calls to get status.  This is mutually exclusive with the
     *   NO_REFRESH option.
     * </pre>
     *
     * Calling `git_status_foreach()` is like calling the extended version with:
     * GIT_STATUS_OPT_INCLUDE_IGNORED, GIT_STATUS_OPT_INCLUDE_UNTRACKED, and
     * GIT_STATUS_OPT_RECURSE_UNTRACKED_DIRS. Those options are bundled together as
     * `GIT_STATUS_OPT_DEFAULTS` if you want them as a baseline.
     */
    public enum OptT implements IBitEnum {
        OPT_INCLUDE_UNTRACKED(1 << 0),
        OPT_INCLUDE_IGNORED(1 << 1),
        OPT_INCLUDE_UNMODIFIED(1 << 2),
        OPT_EXCLUDE_SUBMODULES(1 << 3),
        OPT_RECURSE_UNTRACKED_DIRS(1 << 4),
        OPT_DISABLE_PATHSPEC_MATCH(1 << 5),
        OPT_RECURSE_IGNORED_DIRS(1 << 6),
        OPT_RENAMES_HEAD_TO_INDEX(1 << 7),
        OPT_RENAMES_INDEX_TO_WORKDIR(1 << 8),
        OPT_SORT_CASE_SENSITIVELY(1 << 9),
        OPT_SORT_CASE_INSENSITIVELY(1 << 10),
        OPT_RENAMES_FROM_REWRITES(1 << 11),
        OPT_NO_REFRESH(1 << 12),
        OPT_UPDATE_INDEX(1 << 13),
        OPT_INCLUDE_UNREADABLE(1 << 14),
        OPT_INCLUDE_UNREADABLE_AS_UNTRACKED(1 << 15),
        ;
        private final int _bit;

        OptT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }

        public static Options newDefault() {
            return create(CURRENT_VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }

        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }

        public int getShow() {
            return jniOptionsGetShow(getRawPointer());
        }

        public void setShow(int show) {
            jniOptionsSetShow(getRawPointer(), show);
        }

        public EnumSet<OptT> getFlags() {
            return IBitEnum.parse(jniOptionsGetFlags(getRawPointer()), OptT.class);
        }

        public void setFlags(EnumSet<OptT> flags) {
            jniOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }

        public List<String> getPathspec() {
            List<String> out = new ArrayList<>();
            jniOptionsGetPathspec(getRawPointer(), out);
            return out;
        }

        public void setPathspec(List<String> pathspec) {
            jniOptionsSetPathspec(getRawPointer(), pathspec.toArray(new String[0]));
        }

        public Tree getBaseline() {
            return new Tree(true, jniOptionsGetBaseline(getRawPointer()));
        }

        public void setBaseline(@Nullable Tree baseline) {
            jniOptionsSetBaseline(getRawPointer(), baseline == null ? 0 : baseline.getRawPointer());
        }
    }

    public static class StatusList extends CAutoReleasable {
        protected StatusList(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /**
         * Gather file status information and populate the `git_status_list`.
         *
         * <p>Note that if a `pathspec` is given in the `git_status_options` to filter the status,
         * then the results from rename detection (if you enable it) may not be accurate. To do
         * rename detection properly, this must be called with no `pathspec` so that all files can
         * be considered.
         *
         * @param repo Repository object
         * @param opts Status options structure
         * @return 0 on success or error code
         */
        @Nonnull
        public static StatusList listNew(@Nonnull Repository repo, @Nullable Options opts) {
            StatusList out = new StatusList(false, 0);
            Error.throwIfNeeded(
                    jniListNew(
                            out._rawPtr,
                            repo.getRawPointer(),
                            opts == null ? 0 : opts.getRawPointer()));
            return out;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniListFree(cPtr);
        }

        /**
         * Gets the count of status entries in this list.
         *
         * <p>If there are no changes in status (at least according the options given when the
         * status list was created), this can return 0.
         *
         * @return the number of status entries
         */
        public int entryCount() {
            return jniListEntrycount(getRawPointer());
        }

        /**
         * Get an Entry to one of the entries in the status list.
         *
         * <p>The entry is not modifiable and should not be freed.
         *
         * @param idx Position of the entry
         * @return Pointer to the entry; NULL if out of bounds
         */
        @Nonnull
        public Entry byIndex(int idx) {
            long ptr = jniByindex(getRawPointer(), idx);
            if (ptr == 0) {
                throw new IndexOutOfBoundsException(
                        String.format(
                                "index: %d is out of the boundary, total: %d", idx, entryCount()));
            }
            return new Entry(ptr);
        }
    }

    public static class Entry extends CAutoReleasable {
        protected Entry(long rawPtr) {
            super(true, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new RuntimeException("Entries are owned by StatusList and should not be freed");
        }

        /** @return status flags for this file */
        public EnumSet<StatusT> getStatus() {
            return IBitEnum.parse(jniEntryGetStatus(getRawPointer()), StatusT.class);
        }

        /**
         * @return detailed information about the differences between the file in HEAD and the file
         *     in the index.
         */
        @Nullable
        public Diff.Delta getHeadToIndex() {
            return Diff.Delta.of(jniEntryGetHeadToIndex(getRawPointer()));
        }

        /**
         * @return detailed information about the * differences between the file in the index and
         *     the file in the working directory.
         */
        @Nullable
        public Diff.Delta getIndexToWorkdir() {
            return Diff.Delta.of(jniEntryGetIndexToWorkdir(getRawPointer()));
        }
    }
}

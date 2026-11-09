package com.github.git24j.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class Pathspec extends CAutoReleasable {
    /** void git_pathspec_free(git_pathspec *ps); */
    static native void jniFree(long ps);

    /**
     * int git_pathspec_match_diff(git_pathspec_match_list **out, git_diff *diff, uint32_t flags,
     * git_pathspec *ps);
     */
    static native int jniMatchDiff(AtomicLong out, long diff, int flags, long ps);

    /**
     * int git_pathspec_match_index(git_pathspec_match_list **out, git_index *index, uint32_t flags,
     * git_pathspec *ps);
     */
    static native int jniMatchIndex(AtomicLong out, long index, int flags, long ps);

    /**
     * const git_diff_delta * git_pathspec_match_list_diff_entry(const git_pathspec_match_list *m,
     * size_t pos);
     */
    static native long jniMatchListDiffEntry(long m, int pos);

    /** const char * git_pathspec_match_list_entry(const git_pathspec_match_list *m, size_t pos); */
    static native String jniMatchListEntry(long m, int pos);

    /** size_t git_pathspec_match_list_entrycount(const git_pathspec_match_list *m); */
    static native int jniMatchListEntrycount(long m);

    /**
     * const char * git_pathspec_match_list_failed_entry(const git_pathspec_match_list *m, size_t
     * pos);
     */
    static native String jniMatchListFailedEntry(long m, int pos);

    /** size_t git_pathspec_match_list_failed_entrycount(const git_pathspec_match_list *m); */
    static native int jniMatchListFailedEntrycount(long m);

    /** void git_pathspec_match_list_free(git_pathspec_match_list *m); */
    static native void jniMatchListFree(long m);

    /**
     * int git_pathspec_match_tree(git_pathspec_match_list **out, git_tree *tree, uint32_t flags,
     * git_pathspec *ps);
     */
    static native int jniMatchTree(AtomicLong out, long tree, int flags, long ps);

    /**
     * int git_pathspec_match_workdir(git_pathspec_match_list **out, git_repository *repo, uint32_t
     * flags, git_pathspec *ps);
     */
    static native int jniMatchWorkdir(AtomicLong out, long repoPtr, int flags, long ps);

    /** int git_pathspec_matches_path(const git_pathspec *ps, uint32_t flags, const char *path); */
    static native int jniMatchesPath(long ps, int flags, String path);

    /** int git_pathspec_new(git_pathspec **out, const git_strarray *pathspec); */
    static native int jniNew(AtomicLong out, String[] pathspec);

    protected Pathspec(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Nonnull
    public static Pathspec create(List<String> pathspec) {
        Pathspec out = new Pathspec(false, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr, pathspec.toArray(new String[0])));
        return out;
    }

    /** Get filenames as a list from the matchlist */
    private static List<String> getEntries(long matchListPtr) {
        int n = jniMatchListEntrycount(matchListPtr);
        if (n <= 0) {
            return Collections.emptyList();
        }
        List<String> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(jniMatchListEntry(matchListPtr, i));
        }
        return res;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Try to match a path against a pathspec
     *
     * <p>Unlike most of the other pathspec matching functions, this will not fall back on the
     * native case-sensitivity for your platform. You must explicitly pass flags to control case
     * sensitivity or else this will fall back on being case sensitive.
     *
     * @param flags Combination of git_pathspec_flag_t options to control match
     * @param path The pathname to attempt to match
     * @return true if path matches spec, false if it does not
     */
    public boolean matchesPath(EnumSet<FlagT> flags, @Nonnull String path) {
        return jniMatchesPath(getRawPointer(), IBitEnum.bitOrAll(flags), path) == 1;
    }

    /**
     * Match a pathspec against the working directory of a repository.
     *
     * <p>This matches the pathspec against the current files in the working directory of the
     * repository. It is an error to invoke this on a bare repo. This handles git ignores (i.e.
     * ignored files will not be considered to match the `pathspec` unless the file is tracked in
     * the index).
     *
     * @return a list of matches
     * @param repo The repository in which to match; bare repo is an error
     * @param flags Combination of git_pathspec_flag_t options to control match
     * @throws GitException GIT_ENOTFOUND if no matches and the GIT_PATHSPEC_NO_MATCH_ERROR flag was
     *     given
     */
    public List<String> matchWorkdir(@Nonnull Repository repo, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchWorkdir(
                        outMatchList,
                        repo.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        List<String> res = getEntries(outMatchList.get());
        jniMatchListFree(outMatchList.get());
        return res;
    }

    public List<String> matchIndex(@Nonnull Index index, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchIndex(
                        outMatchList,
                        index.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        long matchListPtr = outMatchList.get();
        List<String> res = getEntries(matchListPtr);
        jniMatchListFree(matchListPtr);
        return res;
    }

    public List<String> matchTree(@Nonnull Tree tree, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchTree(
                        outMatchList,
                        tree.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        long matchListPtr = outMatchList.get();
        List<String> res = getEntries(matchListPtr);
        jniMatchListFree(matchListPtr);
        return res;
    }

    public List<Diff.Delta> matchDiff(@Nonnull Diff diff, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchDiff(
                        outMatchList,
                        diff.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        long matchListPtr = outMatchList.get();
        List<Diff.Delta> deltas = new ArrayList<>();
        int n = jniMatchListEntrycount(matchListPtr);
        for (int i = 0; i < n; i++) {
            deltas.add(new Diff.Delta(jniMatchListDiffEntry(matchListPtr, i)));
        }
        return deltas;
    }

    public enum FlagT implements IBitEnum {
        DEFAULT(0),
        /**
         * GIT_PATHSPEC_IGNORE_CASE forces match to ignore case; otherwise match will use native
         * case sensitivity of platform filesystem
         */
        IGNORE_CASE(1 << 0),

        /**
         * GIT_PATHSPEC_USE_CASE forces case sensitive match; otherwise match will use native case
         * sensitivity of platform filesystem
         */
        USE_CASE(1 << 1),

        /**
         * GIT_PATHSPEC_NO_GLOB disables glob patterns and just uses simple string comparison for
         * matching
         */
        NO_GLOB(1 << 2),

        /**
         * GIT_PATHSPEC_NO_MATCH_ERROR means the match functions return error code GIT_ENOTFOUND if
         * no matches are found; otherwise no matches is still success (return 0) but
         * `git_pathspec_match_list_entrycount` will indicate 0 matches.
         */
        NO_MATCH_ERROR(1 << 3),

        /**
         * GIT_PATHSPEC_FIND_FAILURES means that the `git_pathspec_match_list` should track which
         * patterns matched which files so that at the end of the match we can identify patterns
         * that did not match any files.
         */
        FIND_FAILURES(1 << 4),

        /**
         * GIT_PATHSPEC_FAILURES_ONLY means that the `git_pathspec_match_list` does not need to keep
         * the actual matching filenames. Use this to just test if there were any matches at all or
         * in combination with GIT_PATHSPEC_FIND_FAILURES to validate a pathspec.
         */
        FAILURES_ONLY(1 << 5);
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

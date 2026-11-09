package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class Graph {
    /**
     * int git_graph_ahead_behind(size_t *ahead, size_t *behind, git_repository *repo, const git_oid
     * *local, const git_oid *upstream);
     */
    static native int jniAheadBehind(
            AtomicInteger ahead, AtomicInteger behind, long repoPtr, Oid local, Oid upstream);

    /**
     * int git_graph_descendant_of(git_repository *repo, const git_oid *commit, const git_oid
     * *ancestor);
     */
    static native int jniDescendantOf(long repoPtr, Oid commit, Oid ancestor);

    /**
     * Count the number of unique commits between two commit objects
     *
     * <p>There is no need for branches containing the commits to have any upstream relationship,
     * but it helps to think of one as a branch and the other as its upstream, the `ahead` and
     * `behind` values will be what git would report for the branches.
     *
     * @param repo the repository where the commits exist
     * @param local the commit for local
     * @param upstream the commit for upstream
     * @return number of unique from commits in `upstream` and number of unique from commits in
     *     `local`
     * @throws GitException git2 exception
     */
    public static Count aheadBehind(
            @Nonnull Repository repo, @Nonnull Oid local, @Nonnull Oid upstream) {
        AtomicInteger ahead = new AtomicInteger();
        AtomicInteger behind = new AtomicInteger();
        Error.throwIfNeeded(jniAheadBehind(ahead, behind, repo.getRawPointer(), local, upstream));
        return new Count(ahead.get(), behind.get());
    }

    /**
     * Determine if a commit is the descendant of another commit.
     *
     * <p>Note that a commit is not considered a descendant of itself, in contrast to `git
     * merge-base --is-ancestor`.
     *
     * @param commit a previously loaded commit.
     * @param ancestor a potential ancestor commit.
     * @return 1 if the given commit is a descendant of the potential ancestor, 0 if not, error code
     *     otherwise.
     */
    public static boolean descendantOf(
            @Nonnull Repository repo, @Nonnull Oid commit, @Nonnull Oid ancestor) {
        return jniDescendantOf(repo.getRawPointer(), commit, ancestor) != 0;
    }

    public static class Count {
        private final int _ahead;
        private final int _behind;

        public Count(int ahead, int behind) {
            _ahead = ahead;
            _behind = behind;
        }

        public int getAhead() {
            return _ahead;
        }

        public int getBehind() {
            return _behind;
        }
    }
}

package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Reset {

    /**
     * int git_reset_default(git_repository *repo, const git_object *target, const git_strarray
     * *pathspecs);
     */
    static native int jniDefault(long repoPtr, long target, String[] pathspecs);

    /**
     * int git_reset_from_annotated(git_repository *repo, const git_annotated_commit *commit, int
     * reset_type, const git_checkout_options *checkout_opts);
     */
    static native int jniFromAnnotated(long repoPtr, long commit, int resetType, long checkoutOpts);

    /**
     * int git_reset_reset(git_repository *repo, const git_object *target, git_reset_t reset_type,
     * const git_checkout_options *checkout_opts);
     */
    static native int jniReset(long repoPtr, long target, int resetType, long checkoutOpts);

    public static void resetDefault(
            @Nonnull Repository repo, @Nonnull GitObject target, String[] pathspecs) {
        int e = jniDefault(repo.getRawPointer(), target.getRawPointer(), pathspecs);
        Error.throwIfNeeded(e);
    }

    /**
     * Sets the current head to the specified commit oid and optionally resets the index and working
     * tree to match.
     *
     * <p>SOFT reset means the Head will be moved to the commit.
     *
     * <p>MIXED reset will trigger a SOFT reset, plus the index will be replaced with the content of
     * the commit tree.
     *
     * <p>HARD reset will trigger a MIXED reset and the working directory will be replaced with the
     * content of the index. (Untracked and ignored files will be left alone, however.)
     *
     * <p>TODO: Implement remaining kinds of resets.
     *
     * @param repo Repository where to perform the reset operation.
     * @param target Committish to which the Head should be moved to. This object must belong to the
     *     given `repo` and can either be a git_commit or a git_tag. When a git_tag is being passed,
     *     it should be dereferencable to a git_commit which oid will be used as the target of the
     *     branch.
     * @param resetType Kind of reset operation to perform.
     * @param checkoutOpts Optional checkout options to be used for a HARD reset. The
     *     checkout_strategy field will be overridden (based on reset_type). This parameter can be
     *     used to propagate notify and progress callbacks.
     * @throws GitException git2 exceptions
     */
    public static void reset(
            @Nonnull Repository repo,
            @Nonnull GitObject target,
            @Nonnull ResetT resetType,
            @Nullable Checkout.Options checkoutOpts) {
        int e =
                jniReset(
                        repo.getRawPointer(),
                        target.getRawPointer(),
                        resetType.getBit(),
                        checkoutOpts == null ? 0 : checkoutOpts.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public static void resetFromAnnotated(
            @Nonnull Repository repo,
            @Nonnull AnnotatedCommit commit,
            @Nonnull ResetT resetType,
            @Nullable Checkout.Options checkoutOpts) {
        int e =
                jniFromAnnotated(
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        resetType.getBit(),
                        checkoutOpts == null ? 0 : checkoutOpts.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public enum ResetT implements IBitEnum {
        SOFT(1),
        MIXED(2),
        HARD(3);
        private final int _bit;

        ResetT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }
}

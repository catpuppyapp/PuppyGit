package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public final class Revert {

    /**
     * int git_revert_commit(git_index **out, git_repository *repo, git_commit *revert_commit,
     * git_commit *our_commit, unsigned int mainline, const git_merge_options *merge_options);
     */
    static native int jniCommit(
            AtomicLong out,
            long repoPtr,
            long revertCommit,
            long ourCommit,
            int mainline,
            long mergeOptions);

    static native void jniOptionsFree(long optionsPtr);

    /** git_checkout_options checkout_opts */
    static native long jniOptionsGetCheckoutOpts(long optionsPtr);

    /** unsigned int mainline */
    static native int jniOptionsGetMainline(long optionsPtr);

    /** git_merge_options merge_opts */
    static native long jniOptionsGetMergeOpts(long optionsPtr);

    static native int jniOptionsNew(AtomicLong outPtr, int version);

    /** unsigned int mainline */
    static native void jniOptionsSetMainline(long optionsPtr, int mainline);

    /**
     * int git_revert_revert(git_repository *repo, git_commit *commit, const git_revert_options
     * *given_opts);
     */
    static native int jniRevert(long repoPtr, long commit, long givenOpts);

    public static Index revertCommit(
            @Nonnull Repository repo,
            @Nonnull Commit revertCommit,
            @Nonnull Commit ourCommit,
            int mainline,
            @Nullable Merge.Options mergeOptions) {
        Index outIdx = new Index(false, 0);
        int e =
                jniCommit(
                        ourCommit._rawPtr,
                        repo.getRawPointer(),
                        revertCommit.getRawPointer(),
                        ourCommit.getRawPointer(),
                        mainline,
                        mergeOptions == null ? 0 : mergeOptions.getRawPointer());
        Error.throwIfNeeded(e);
        return outIdx;
    }

    /**
     * Reverts the given commit, producing changes in the index and working directory.
     *
     * @param repo the repository to revert
     * @param commit the commit to revert
     * @param revertOpts the revert options (or null for defaults)
     * @return zero on success, -1 on failure.
     */
    public void revert(
            @Nonnull Repository repo, @Nonnull Commit commit, @Nullable Options revertOpts) {
        int e =
                jniRevert(
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        revertOpts == null ? 0 : revertOpts.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public static class Options extends CAutoReleasable {
        public static int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        public Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }

        public Options createDefault() {
            return create(VERSION);
        }

        public int getMainline() {
            return jniOptionsGetMainline(getRawPointer());
        }

        public void setMainline(int mainline) {
            jniOptionsSetMainline(getRawPointer(), mainline);
        }

        public Merge.Options getMergeOpts() {
            return new Merge.Options(true, jniOptionsGetMergeOpts(getRawPointer()));
        }

        public Checkout.Options getCheckoutOpts() {
            return new Checkout.Options(true, jniOptionsGetCheckoutOpts(getRawPointer()));
        }
    }
}

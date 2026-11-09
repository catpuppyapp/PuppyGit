package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Cherrypick {
    /**
     * int git_cherrypick_cherrypick(git_repository *repo, git_commit *commit, const
     * git_cherrypick_options *cherrypick_options);
     */
    static native int jniCherrypick(long repoPtr, long commit, long cherrypickOptions);

    /**
     * int git_cherrypick_commit(git_index **out, git_repository *repo, git_commit
     * *cherrypick_commit, git_commit *our_commit, unsigned int mainline, const git_merge_options
     * *merge_options);
     */
    static native int jniCommit(
            AtomicLong out,
            long repoPtr,
            long cherrypickCommit,
            long ourCommit,
            int mainline,
            long mergeOptions);

    static native void jniOptionsFree(long optsPtr);

    static native int jniOptionsNew(AtomicLong outOpts, int version);

    /** unsigned int mainline */
    static native int jniOptionsGetMainline(long optionsPtr);
    static native void jniOptionsSetMainline(long optionsPtr, int mainline);
    /** git_merge_options merge_opts */
    static native long jniOptionsGetMergeOpts(long optionsPtr);
    /** git_checkout_options checkout_opts */
    static native long jniOptionsGetCheckoutOpts(long optionsPtr);

    /**
     * Cherry-picks the given commit against the given "our" commit, producing an index that
     * reflects the result of the cherry-pick.
     *
     * <p>The returned index must be freed explicitly with `git_index_free`.
     *
     * @return the resulted index result
     * @param repo the repository that contains the given commits
     * @param cherryPickCommit the commit to cherry-pick
     * @param ourCommit the commit to cherry-pick against (eg, HEAD)
     * @param mainline the parent of the `cherrypick_commit`, if it is a merge
     * @param mergeOptions the merge options (or null for defaults)
     * @return zero on success, -1 on failure.
     */
    @Nonnull
    public static Index commit(
            @Nonnull Repository repo,
            @Nonnull Commit cherryPickCommit,
            @Nonnull Commit ourCommit,
            int mainline,
            @Nullable Merge.Options mergeOptions) {
        Index out = new Index(false, 0);
        int e =
                jniCommit(
                        out._rawPtr,
                        repo.getRawPointer(),
                        cherryPickCommit.getRawPointer(),
                        ourCommit.getRawPointer(),
                        mainline,
                        mergeOptions == null ? 0 : mergeOptions.getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }

    public static void cherrypick(
            @Nonnull Repository repo, @Nonnull Commit commit, @Nullable Options options) {
        int e =
                jniCherrypick(
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }

        @Nonnull
        public static Options createDefault() {
            return create(VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
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

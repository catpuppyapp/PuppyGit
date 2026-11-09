package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Stash {
    /**
     * int git_stash_apply(git_repository *repo, size_t index, const git_stash_apply_options
     * *options);
     */
    static native int jniApply(long repoPtr, int index, long options);

    static native void jniApplyOptionsFree(long applyOptionsPtr);

    /** git_checkout_options checkout_options */
    static native long jniApplyOptionsGetCheckoutOptions(long apply_optionsPtr);

    /** uint32_t flags */
    static native int jniApplyOptionsGetFlags(long apply_optionsPtr);

    static native int jniApplyOptionsNew(AtomicLong outPtr, int version);

    /** uint32_t flags */
    static native void jniApplyOptionsSetFlags(long apply_optionsPtr, int flags);

    /** git_stash_apply_progress_cb progress_cb */
    static native void jniApplyOptionsSetProgressCb(
            long apply_optionsPtr, Internals.ICallback progressCb);

    /** int git_stash_drop(git_repository *repo, size_t index); */
    static native int jniDrop(long repoPtr, int index);

    /** int git_stash_foreach(git_repository *repo, git_stash_cb callback, void *payload); */
    static native int jniForeach(long repoPtr, Internals.ISBarrCalback callback);

    /**
     * int git_stash_pop(git_repository *repo, size_t index, const git_stash_apply_options
     * *options);
     */
    static native int jniPop(long repoPtr, int index, long options);

    /**
     * int git_stash_save(git_oid *out, git_repository *repo, const git_signature *stasher, const
     * char *message, uint32_t flags);
     */
    static native int jniSave(Oid out, long repoPtr, long stasher, String message, int flags);

    /**
     * Apply a single stashed state from the stash list.
     *
     * <p>If local changes in the working directory conflict with changes in the stash then
     * GIT_EMERGECONFLICT will be returned. In this case, the index will always remain unmodified
     * and all files in the working directory will remain unmodified. However, if you are restoring
     * untracked files or ignored files and there is a conflict when applying the modified files,
     * then those files will remain in the working directory.
     *
     * <p>If passing the GIT_STASH_APPLY_REINSTATE_INDEX flag and there would be conflicts when
     * reinstating the index, the function will return GIT_EMERGECONFLICT and both the working
     * directory and index will be left unmodified.
     *
     * <p>Note that a minimum checkout strategy of `GIT_CHECKOUT_SAFE` is implied.
     *
     * @param repo The owning repository.
     * @param index The position within the stash list. 0 points to the most recent stashed state.
     * @param options Optional options to control how stashes are applied.
     * @throws GitException git2 exceptions like GIT_ENOTFOUND if there's no stashed state for the
     *     given index, GIT_EMERGECONFLICT if changes exist in the working directory, or an error
     *     code
     */
    public static void apply(@Nonnull Repository repo, int index, @Nullable ApplyOptions options) {
        int e =
                jniApply(
                        repo.getRawPointer(), index, options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public static void drop(@Nonnull Repository repo, int index) {
        Error.throwIfNeeded(jniDrop(repo.getRawPointer(), index));
    }
    /** unsigned int version */
    public static void foreach(@Nonnull Repository repo, @Nonnull StashCb callback) {
        int e =
                jniForeach(
                        repo.getRawPointer(),
                        (index, message, rawId) -> callback.accept(index, message, Oid.of(rawId)));
        Error.throwIfNeeded(e);
    }

    /**
     * Apply a single stashed state from the stash list and remove it from the list if successful.
     *
     * @param repo The owning repository.
     * @param index The position within the stash list. 0 points to the most recent stashed state.
     * @param options Optional options to control how stashes are applied.
     * @throws GitException git2 exceptions like GIT_ENOTFOUND if there's no stashed state for the
     *     given index, or error code. (see apply() above for details)
     */
    public static void pop(@Nonnull Repository repo, int index, @Nullable ApplyOptions options) {
        int e = jniPop(repo.getRawPointer(), index, options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }

    /**
     * Save the local modifications to a new stash.
     *
     * @return Object id of the commit containing the stashed state. This commit is also the target
     *     of the direct reference refs/stash.
     * @param repo The owning repository.
     * @param stasher The identity of the person performing the stashing.
     * @param message Optional description along with the stashed state.
     * @param flags Flags to control the stashing process. (see GIT_STASH_* above)
     * @throws GitException git2 exceptions like GIT_ENOTFOUND where there's nothing to stash, or
     *     error code.
     */
    public static Oid save(
            @Nonnull Repository repo,
            @Nonnull Signature stasher,
            @Nonnull String message,
            @Nullable EnumSet<Flags> flags) {
        Oid out = new Oid();
        int e =
                jniSave(
                        out,
                        repo.getRawPointer(),
                        stasher.getRawPointer(),
                        message,
                        IBitEnum.bitOrAll(flags));
        Error.throwIfNeeded(e);
        return out;
    }

    /** Stash application flags. */
    public enum ApplyFlags implements IBitEnum {
        DEFAULT(0),
        /* Try to reinstate not only the working tree's changes,
         * but also the index's changes.
         */
        REINSTATE_INDEX(1 << 0);
        private final int _bit;

        ApplyFlags(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum Flags implements IBitEnum {
        /** No option, default */
        DEFAULT(0),

        /** All changes already added to the index are left intact in the working directory */
        KEEP_INDEX(1 << 0),

        /** All untracked files are also stashed and then cleaned up from the working directory */
        INCLUDE_UNTRACKED(1 << 1),

        /** All ignored files are also stashed and then cleaned up from the working directory */
        INCLUDE_IGNORED(1 << 2);
        private final int _bit;

        Flags(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum ProgressT implements IBitEnum {
        NONE(0),

        /** Loading the stashed data from the object database. */
        LOADING_STASH(1),

        /** The stored index is being analyzed. */
        ANALYZE_INDEX(2),

        /** The modified files are being analyzed. */
        ANALYZE_MODIFIED(3),

        /** The untracked and ignored files are being analyzed. */
        ANALYZE_UNTRACKED(4),

        /** The untracked files are being written to disk. */
        CHECKOUT_UNTRACKED(5),

        /** The modified files are being written to disk. */
        CHECKOUT_MODIFIED(6),

        /** The stash was applied successfully. */
        DONE(7);
        private final int _bit;

        ProgressT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * int git_stash_cb(size_t index, const char *message, const git_oid *stash_id, void *payload);
     */
    public interface StashCb {
        int accept(int index, String message, Oid stashId);
    }

    @FunctionalInterface
    public interface ProgressCb {
        int accept(ProgressT progress);
    }

    public static class ApplyOptions extends CAutoReleasable {
        public static final int VERSION = 1;

        protected ApplyOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static ApplyOptions create(int version) {
            ApplyOptions options = new ApplyOptions(false, 0);
            int e = jniApplyOptionsNew(options._rawPtr, version);
            Error.throwIfNeeded(e);
            return options;
        }

        @Nonnull
        public static ApplyOptions createDefault() {
            return create(VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniApplyOptionsFree(cPtr);
        }

        @Nonnull
        public EnumSet<ApplyFlags> getFlags() {
            int r = jniApplyOptionsGetFlags(getRawPointer());
            return IBitEnum.parse(r, ApplyFlags.class);
        }

        public void setFlags(EnumSet<ApplyFlags> flags) {
            jniApplyOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }

        @Nonnull
        public Checkout.Options getCheckoutOptions() {
            long ptr = jniApplyOptionsGetCheckoutOptions(getRawPointer());
            return new Checkout.Options(true, ptr);
        }

        public void setProgressCb(@Nonnull ProgressCb progressCb) {
            jniApplyOptionsSetProgressCb(
                    getRawPointer(),
                    x -> progressCb.accept(IBitEnum.valueOf(x, ProgressT.class, ProgressT.NONE)));
        }
    }
}

package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Apply {

    /**
     * int git_apply_apply(git_repository *repo, git_diff *diff, int location, const
     * git_apply_options *options);
     */
    static native int jniApply(long repoPtr, long diff, int location, long options);

    static native void jniOptionsFree(long optionsPtra);

    /** unsigned int flags */
    static native int jniOptionsGetFlags(long optionsPtr);

    static native int jniOptionsNew(
            int version,
            AtomicLong outOpts,
            Internals.JCallback deltaCb,
            Internals.JCallback hunkCb);

    /** unsigned int flags */
    static native void jniOptionsSetFlags(long optionsPtr, int flags);

    /**
     * int git_apply_to_tree(git_index **out, git_repository *repo, git_tree *preimage, git_diff
     * *diff, const git_apply_options *options);
     */
    static native int jniToTree(
            AtomicLong out, long repoPtr, long preimage, long diff, long options);

    public static Index toTree(
            @Nonnull Repository repo,
            @Nonnull Tree preimage,
            @Nonnull Diff diff,
            @Nullable Options options) {
        Index outIndex = new Index(false, 0);
        int e =
                jniToTree(
                        outIndex._rawPtr,
                        repo.getRawPointer(),
                        preimage.getRawPointer(),
                        diff.getRawPointer(),
                        options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
        return outIndex;
    }

    /**
     * Apply a `git_diff` to the given repository, making changes directly in the working directory,
     * the index, or both.
     *
     * @param repo the repository to apply to
     * @param diff the diff to apply
     * @param location the location to apply (workdir, index or both)
     * @param options the options for the apply (or null for defaults)
     */
    public static void apply(
            @Nonnull Repository repo,
            @Nonnull Diff diff,
            @Nonnull LocationT location,
            @Nullable Options options) {
        int e =
                jniApply(
                        repo.getRawPointer(),
                        diff.getRawPointer(),
                        location.getBit(),
                        options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public enum FlagsT implements IBitEnum {
        /**
         * Don't actually make changes, just test that the patch applies. This is the equivalent of
         * `git apply --check`.
         */
        CHECK(1 << 0);
        private final int _bit;

        FlagsT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum LocationT implements IBitEnum {
        /**
         * Apply the patch to the workdir, leaving the index untouched.
         * This is the equivalent of `git apply` with no location argument.
         */
        WORKDIR(0),

        /**
         * Apply the patch to the index, leaving the working directory
         * untouched.  This is the equivalent of `git apply --cached`.
         */
        INDEX(1),

        /**
         * Apply the patch to both the working directory and the index.
         * This is the equivalent of `git apply --index`.
         */
        BOTH(2),
        ;
        private final int _bit;

        LocationT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface DeltaCb {
        int accept(@Nonnull Diff.Delta delta);
    }

    @FunctionalInterface
    public interface HunkCb {
        int accept(@Nonnull Diff.Hunk hunk);
    }

    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        public static Options create(
                int version, @Nullable DeltaCb deltaCb, @Nullable HunkCb hunkCb) {
            Options out = new Options(false, 0);
            int e =
                    jniOptionsNew(
                            version,
                            out._rawPtr,
                            ptr -> {
                                if (deltaCb != null) {
                                    return deltaCb.accept(new Diff.Delta(ptr));
                                }
                                return 0;
                            },
                            ptr -> {
                                if (hunkCb != null) {
                                    return hunkCb.accept(new Diff.Hunk(ptr));
                                }
                                return 0;
                            });
            Error.throwIfNeeded(e);
            return out;
        }

        public static Options createDefault(@Nullable DeltaCb deltaCb, @Nullable HunkCb hunkCb) {
            return create(VERSION, deltaCb, hunkCb);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        @Nonnull
        public EnumSet<FlagsT> getFlags() {
            return IBitEnum.parse(jniOptionsGetFlags(getRawPointer()), FlagsT.class);
        }

        public void setFlags(@Nonnull EnumSet<FlagsT> flags) {
            jniOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }
    }
}

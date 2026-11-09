package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Describe {
    /**
     * int git_describe_commit(git_describe_result **result, git_object *committish,
     * git_describe_options *opts);
     */
    static native int jniCommit(AtomicLong result, long committish, long opts);

    /**
     * int git_describe_format(git_buf *out, const git_describe_result *result, const
     * git_describe_format_options *opts);
     */
    static native int jniFormat(Buf out, long result, long opts);

    /** void git_describe_format_options_free(git_describe_format_options *opts); */
    static native void jniFormatOptionsFree(long opts);

    /** unsigned int abbreviated_size */
    static native int jniFormatOptionsGetAbbreviatedSize(long format_optionsPtr);

    /** int always_use_long_format */
    static native int jniFormatOptionsGetAlwaysUseLongFormat(long format_optionsPtr);

    /** const char *dirty_suffix */
    static native String jniFormatOptionsGetDirtySuffix(long format_optionsPtr);

    /** unsigned int version */
    static native int jniFormatOptionsGetVersion(long format_optionsPtr);

    /**
     * int git_describe_format_options_new(git_describe_format_options **out_opts, unsigned int
     * version);
     */
    static native int jniFormatOptionsNew(AtomicLong outOpts, int version);

    /** unsigned int abbreviated_size */
    static native void jniFormatOptionsSetAbbreviatedSize(
            long format_optionsPtr, int abbreviatedSize);

    /** int always_use_long_format */
    static native void jniFormatOptionsSetAlwaysUseLongFormat(
            long format_optionsPtr, int alwaysUseLongFormat);

    /** const char *dirty_suffix */
    static native void jniFormatOptionsSetDirtySuffix(long format_optionsPtr, String dirtySuffix);

    /** unsigned int version */
    static native void jniFormatOptionsSetVersion(long format_optionsPtr, int version);

    /** void git_describe_options_free(git_describe_options *opts); */
    static native void jniOptionsFree(long opts);

    /** unsigned int describe_strategy */
    static native int jniOptionsGetDescribeStrategy(long optionsPtr);

    /** unsigned int max_candidates_tags */
    static native int jniOptionsGetMaxCandidatesTags(long optionsPtr);

    /** int only_follow_first_parent */
    static native int jniOptionsGetOnlyFollowFirstParent(long optionsPtr);

    /** const char *pattern */
    static native String jniOptionsGetPattern(long optionsPtr);

    /** int show_commit_oid_as_fallback */
    static native int jniOptionsGetShowCommitOidAsFallback(long optionsPtr);

    /** int git_describe_options_new(git_describe_options **out_opts, unsigned int version); */
    static native int jniOptionsNew(AtomicLong outOpts, int version);

    /** unsigned int describe_strategy */
    static native void jniOptionsSetDescribeStrategy(long optionsPtr, int describeStrategy);

    /** unsigned int max_candidates_tags */
    static native void jniOptionsSetMaxCandidatesTags(long optionsPtr, int maxCandidatesTags);

    /** int only_follow_first_parent */
    static native void jniOptionsSetOnlyFollowFirstParent(
            long optionsPtr, int onlyFollowFirstParent);

    /** const char *pattern */
    static native void jniOptionsSetPattern(long optionsPtr, String pattern);

    /** int show_commit_oid_as_fallback */
    static native void jniOptionsSetShowCommitOidAsFallback(
            long optionsPtr, int showCommitOidAsFallback);

    /** void git_describe_result_free(git_describe_result *result); */
    static native void jniResultFree(long result);

    /**
     * int git_describe_workdir(git_describe_result **out, git_repository *repo,
     * git_describe_options *opts);
     */
    static native int jniWorkdir(AtomicLong out, long repoPtr, long opts);

    @Nonnull
    public static Result commit(@Nonnull GitObject commitish, @Nullable Options options) {
        Result res = new Result(false, 0);
        int e =
                jniCommit(
                        res._rawPtr,
                        commitish.getRawPointer(),
                        options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
        return res;
    }

    @Nonnull
    public static Result workdir(@Nonnull Repository repo, @Nullable Options options) {
        Result res = new Result(false, 0);
        int e =
                jniWorkdir(
                        res._rawPtr,
                        repo.getRawPointer(),
                        options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
        return res;
    }

    public enum StrategyT implements IBitEnum {
        DEFAULT(0),
        TAGS(1),
        ALL(2),
        ;
        private final int _bit;

        StrategyT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class Options extends CAutoReleasable {
        public static int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Options create(int version) {
            Options options = new Options(false, 0);
            int e = jniOptionsNew(options._rawPtr, version);
            Error.throwIfNeeded(e);
            return options;
        }

        public static Options createDefault() {
            return create(VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        public int getMaxCandidatesTags() {
            return jniOptionsGetMaxCandidatesTags(getRawPointer());
        }

        public void setMaxCandidatesTags(int maxCandidatesTags) {
            jniOptionsSetMaxCandidatesTags(getRawPointer(), maxCandidatesTags);
        }

        @Nonnull
        public StrategyT getDescribeStrategy() {
            int r = jniOptionsGetDescribeStrategy(getRawPointer());
            return IBitEnum.valueOf(r, StrategyT.class, StrategyT.DEFAULT);
        }

        public void setDescribeStrategy(StrategyT describeStrategy) {
            jniOptionsSetDescribeStrategy(getRawPointer(), describeStrategy.getBit());
        }

        public String getPattern() {
            return jniOptionsGetPattern(getRawPointer());
        }

        public void setPattern(String pattern) {
            jniOptionsSetPattern(getRawPointer(), pattern);
        }

        public boolean getOnlyFollowFirstParent() {
            return jniOptionsGetOnlyFollowFirstParent(getRawPointer()) != 0;
        }

        public void setOnlyFollowFirstParent(boolean onlyFollowFirstParent) {
            jniOptionsSetOnlyFollowFirstParent(getRawPointer(), onlyFollowFirstParent ? 1 : 0);
        }

        public boolean getShowCommitOidAsFallback() {
            return jniOptionsGetShowCommitOidAsFallback(getRawPointer()) != 0;
        }

        public void setShowCommitOidAsFallback(boolean showCommitOidAsFallback) {
            jniOptionsSetShowCommitOidAsFallback(getRawPointer(), showCommitOidAsFallback ? 1 : 0);
        }
    }

    public static class FormatOptions extends CAutoReleasable {
        public static final int VERSION = 1;

        protected FormatOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static FormatOptions create(int version) {
            FormatOptions options = new FormatOptions(false, 0);
            Error.throwIfNeeded(jniFormatOptionsNew(options._rawPtr, version));
            return options;
        }

        public static FormatOptions createDefault() {
            return create(VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFormatOptionsFree(cPtr);
        }

        public int getAbbreviatedSize() {
            return jniFormatOptionsGetAbbreviatedSize(getRawPointer());
        }

        public void setAbbreviatedSize(int abbreviatedSize) {
            jniFormatOptionsSetAbbreviatedSize(getRawPointer(), abbreviatedSize);
        }

        public boolean getAlwaysUseLongFormat() {
            return jniFormatOptionsGetAlwaysUseLongFormat(getRawPointer()) != 0;
        }

        public void setAlwaysUseLongFormat(boolean alwaysUseLongFormat) {
            jniFormatOptionsSetAlwaysUseLongFormat(getRawPointer(), alwaysUseLongFormat ? 1 : 0);
        }

        public String getDirtySuffix() {
            return jniFormatOptionsGetDirtySuffix(getRawPointer());
        }

        public void setDirtySuffix(String dirtySuffix) {
            jniFormatOptionsSetDirtySuffix(getRawPointer(), dirtySuffix);
        }
    }

    public static class Result extends CAutoReleasable {
        protected Result(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniResultFree(cPtr);
        }

        /**
         * Print the describe result to a buffer
         *
         * @return formatted result
         * @param options the formatting options (or NULL for defaults)
         */
        @CheckForNull
        String format(@Nullable FormatOptions options) {
            Buf out = new Buf();
            int e = jniFormat(out, getRawPointer(), options == null ? 0 : options.getRawPointer());
            Error.throwIfNeeded(e);
            return out.getString().orElse(null);
        }
    }
}

#include "j_describe.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** long git_describe_options_new(unsigned int version); */
JNIEXPORT int JNICALL J_MAKE_METHOD(Describe_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_describe_options *opts = (git_describe_options *)malloc(sizeof(git_describe_options));
    opts->pattern = NULL;
    int r = git_describe_options_init(opts, (unsigned int)version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

/** void git_describe_options_free(git_describe_options *opts); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_describe_options *opts = (git_describe_options *)optsPtr;
    free((void *)opts->pattern);
    free(opts);
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_describe_options *)optionsPtr)->version;
}

/** unsigned int max_candidates_tags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetMaxCandidatesTags)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_describe_options *)optionsPtr)->max_candidates_tags;
}

/** unsigned int describe_strategy*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetDescribeStrategy)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_describe_options *)optionsPtr)->describe_strategy;
}

/** const char *pattern*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Describe_jniOptionsGetPattern)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_describe_options *)optionsPtr)->pattern);
}

/** int only_follow_first_parent*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetOnlyFollowFirstParent)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_describe_options *)optionsPtr)->only_follow_first_parent;
}

/** int show_commit_oid_as_fallback*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetShowCommitOidAsFallback)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_describe_options *)optionsPtr)->show_commit_oid_as_fallback;
}

/** unsigned int max_candidates_tags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetMaxCandidatesTags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint maxCandidatesTags)
{
    ((git_describe_options *)optionsPtr)->max_candidates_tags = (unsigned int)maxCandidatesTags;
}

/** unsigned int describe_strategy*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetDescribeStrategy)(JNIEnv *env, jclass obj, jlong optionsPtr, jint describeStrategy)
{
    ((git_describe_options *)optionsPtr)->describe_strategy = (unsigned int)describeStrategy;
}

/** const char *pattern*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetPattern)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring pattern)
{
    char *c_pattern = j_copy_of_jstring(env, pattern, true);
    ((git_describe_options *)optionsPtr)->pattern = (const char *)c_pattern;
}

/** int only_follow_first_parent*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetOnlyFollowFirstParent)(JNIEnv *env, jclass obj, jlong optionsPtr, jint onlyFollowFirstParent)
{
    ((git_describe_options *)optionsPtr)->only_follow_first_parent = (int)onlyFollowFirstParent;
}

/** int show_commit_oid_as_fallback*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetShowCommitOidAsFallback)(JNIEnv *env, jclass obj, jlong optionsPtr, jint showCommitOidAsFallback)
{
    ((git_describe_options *)optionsPtr)->show_commit_oid_as_fallback = (int)showCommitOidAsFallback;
}

/** long git_describe_format_options_new(unsigned int version); */
JNIEXPORT int JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_describe_format_options *opts = (git_describe_format_options *)malloc(sizeof(git_describe_format_options));
    opts->dirty_suffix = NULL;
    int r = git_describe_format_options_init(opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

/** void git_describe_format_options_free(git_describe_format_options *opts); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_describe_format_options *opts = (git_describe_format_options *)optsPtr;
    free((void *)opts->dirty_suffix);
    free(opts);
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetVersion)(JNIEnv *env, jclass obj, jlong formatOptionsPtr)
{
    return ((git_describe_format_options *)formatOptionsPtr)->version;
}

/** unsigned int abbreviated_size*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetAbbreviatedSize)(JNIEnv *env, jclass obj, jlong formatOptionsPtr)
{
    return ((git_describe_format_options *)formatOptionsPtr)->abbreviated_size;
}

/** int always_use_long_format*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetAlwaysUseLongFormat)(JNIEnv *env, jclass obj, jlong formatOptionsPtr)
{
    return ((git_describe_format_options *)formatOptionsPtr)->always_use_long_format;
}

/** const char *dirty_suffix*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetDirtySuffix)(JNIEnv *env, jclass obj, jlong formatOptionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_describe_format_options *)formatOptionsPtr)->dirty_suffix);
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetVersion)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint version)
{
    ((git_describe_format_options *)formatOptionsPtr)->version = (unsigned int)version;
}

/** unsigned int abbreviated_size*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetAbbreviatedSize)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint abbreviatedSize)
{
    ((git_describe_format_options *)formatOptionsPtr)->abbreviated_size = (unsigned int)abbreviatedSize;
}

/** int always_use_long_format*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetAlwaysUseLongFormat)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint alwaysUseLongFormat)
{
    ((git_describe_format_options *)formatOptionsPtr)->always_use_long_format = (int)alwaysUseLongFormat;
}

/** const char *dirty_suffix*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetDirtySuffix)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jstring dirtySuffix)
{
    char *dirty_suffix = j_copy_of_jstring(env, dirtySuffix, true);
    ((git_describe_format_options *)formatOptionsPtr)->dirty_suffix = (const char *)dirty_suffix;
}

/** int git_describe_commit(git_describe_result **result, git_object *committish, git_describe_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniCommit)(JNIEnv *env, jclass obj, jobject result, jlong committishPtr, jlong optsPtr)
{
    git_describe_result *c_result;
    int r = git_describe_commit(&c_result, (git_object *)committishPtr, (git_describe_options *)optsPtr);
    (*env)->CallVoidMethod(env, result, jniConstants->midAtomicLongSet, (long)c_result);
    /* git_describe_result_free(c_result); */
    return r;
}

/** int git_describe_workdir(git_describe_result **out, git_repository *repo, git_describe_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniWorkdir)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr)
{
    git_describe_result *c_out;
    int r = git_describe_workdir(&c_out, (git_repository *)repoPtr, (git_describe_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_describe_result_free(c_out); */
    return r;
}

/** int git_describe_format(git_buf *out, const git_describe_result *result, const git_describe_format_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormat)(JNIEnv *env, jclass obj, jobject out, jlong resultPtr, jlong optsPtr)
{
    git_buf c_out = {0};
    int r = git_describe_format(&c_out, (git_describe_result *)resultPtr, (git_describe_format_options *)optsPtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** void git_describe_result_free(git_describe_result *result); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniResultFree)(JNIEnv *env, jclass obj, jlong resultPtr)
{
    git_describe_result_free((git_describe_result *)resultPtr);
}

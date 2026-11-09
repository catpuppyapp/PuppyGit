#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_DESCRIBE_H__
#define __GIT24J_DESCRIBE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- git_describe_options ---------- */
    /** long git_describe_options_new(unsigned int version); */
    JNIEXPORT int JNICALL J_MAKE_METHOD(Describe_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    /** void git_describe_options_free(git_describe_options *opts); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int max_candidates_tags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetMaxCandidatesTags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int describe_strategy*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetDescribeStrategy)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** const char *pattern*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Describe_jniOptionsGetPattern)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** int only_follow_first_parent*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetOnlyFollowFirstParent)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** int show_commit_oid_as_fallback*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetShowCommitOidAsFallback)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    /** unsigned int max_candidates_tags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetMaxCandidatesTags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint maxCandidatesTags);
    /** unsigned int describe_strategy*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetDescribeStrategy)(JNIEnv *env, jclass obj, jlong optionsPtr, jint describeStrategy);
    /** const char *pattern*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetPattern)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring pattern);
    /** int only_follow_first_parent*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetOnlyFollowFirstParent)(JNIEnv *env, jclass obj, jlong optionsPtr, jint onlyFollowFirstParent);
    /** int show_commit_oid_as_fallback*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetShowCommitOidAsFallback)(JNIEnv *env, jclass obj, jlong optionsPtr, jint showCommitOidAsFallback);

    /** -------- git_describe_format_options ---------- */
    /** long git_describe_format_options_new(unsigned int version); */
    JNIEXPORT int JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    /** void git_describe_format_options_free(git_describe_format_options *opts); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetVersion)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    /** unsigned int abbreviated_size*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetAbbreviatedSize)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    /** int always_use_long_format*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetAlwaysUseLongFormat)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    /** const char *dirty_suffix*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetDirtySuffix)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetVersion)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint version);
    /** unsigned int abbreviated_size*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetAbbreviatedSize)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint abbreviatedSize);
    /** int always_use_long_format*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetAlwaysUseLongFormat)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint alwaysUseLongFormat);
    /** const char *dirty_suffix*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetDirtySuffix)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jstring dirtySuffix);

    /** int git_describe_commit(git_describe_result **result, git_object *committish, git_describe_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniCommit)(JNIEnv *env, jclass obj, jobject result, jlong committishPtr, jlong optsPtr);

    /** int git_describe_workdir(git_describe_result **out, git_repository *repo, git_describe_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniWorkdir)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);

    /** int git_describe_format(git_buf *out, const git_describe_result *result, const git_describe_format_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormat)(JNIEnv *env, jclass obj, jobject out, jlong resultPtr, jlong optsPtr);

    /** void git_describe_result_free(git_describe_result *result); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniResultFree)(JNIEnv *env, jclass obj, jlong resultPtr);

#ifdef __cplusplus
}
#endif
#endif
#include "j_common.h"
#include <jni.h>

#ifndef __GIT24J_STATUS_H__
#define __GIT24J_STATUS_H__
#ifdef __cplusplus
extern "C"
{
#endif

    // no matching type found for 'git_status_cb callback'
    /** int git_status_foreach(git_repository *repo, git_status_cb callback, void *payload); */
    // no matching type found for 'git_status_cb callback'
    /** int git_status_foreach_ext(git_repository *repo, const git_status_options *opts, git_status_cb callback, void *payload); */
    /** -------- Signature of the header ---------- */
    /** int git_status_init_options(git_status_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);

    /** int git_status_file(unsigned int *status_flags, git_repository *repo, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniFile)(JNIEnv *env, jclass obj, jobject statusFlags, jlong repoPtr, jstring path);

    /** int git_status_list_new(git_status_list **out, git_repository *repo, const git_status_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);

    /** size_t git_status_list_entrycount(git_status_list *statuslist); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListEntrycount)(JNIEnv *env, jclass obj, jlong statuslistPtr);

    /** const git_status_entry * git_status_byindex(git_status_list *statuslist, size_t idx); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniByindex)(JNIEnv *env, jclass obj, jlong statuslistPtr, jint idx);

    /** void git_status_list_free(git_status_list *statuslist); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniListFree)(JNIEnv *env, jclass obj, jlong statuslistPtr);

    /** int git_status_should_ignore(int *ignored, git_repository *repo, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniShouldIgnore)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path);

    /** -------- Signature of the header ---------- */
    /** unsigned int      version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_status_show_t show*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetShow)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int      flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_strarray      pathspec*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsGetPathspec)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject outListPathSpec);
    /** git_tree          *baseline*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniOptionsGetBaseline)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int      version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    /** git_status_show_t show*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetShow)(JNIEnv *env, jclass obj, jlong optionsPtr, jint show);
    /** unsigned int      flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags);
    /** git_strarray      pathspec*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetPathspec)(JNIEnv *env, jclass obj, jlong optionsPtr, jobjectArray pathspec);
    /** git_tree          *baseline*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetBaseline)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong baseline);

    /** git_status_t status*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniEntryGetStatus)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** git_diff_delta *head_to_index*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniEntryGetHeadToIndex)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** git_diff_delta *index_to_workdir*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniEntryGetIndexToWorkdir)(JNIEnv *env, jclass obj, jlong entryPtr);

#ifdef __cplusplus
}
#endif
#endif
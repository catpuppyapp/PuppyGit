#include "j_status.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <stdio.h>

// no matching type found for 'git_status_cb callback'
/** int git_status_foreach(git_repository *repo, git_status_cb callback, void *payload); */
// no matching type found for 'git_status_cb callback'
/** int git_status_foreach_ext(git_repository *repo, const git_status_options *opts, git_status_cb callback, void *payload); */
/** -------- Wrapper Body ---------- */
/** int git_status_init_options(git_status_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_status_init_options((git_status_options *)optsPtr, version);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_status_options *out = (git_status_options *)malloc(sizeof(git_status_options));
    int r = git_status_init_options(out, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)out);
    return r;
}

/** int git_status_file(unsigned int *status_flags, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniFile)(JNIEnv *env, jclass obj, jobject statusFlags, jlong repoPtr, jstring path)
{
    unsigned int c_status_flags;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_status_file(&c_status_flags, (git_repository *)repoPtr, c_path);
    (*env)->CallVoidMethod(env, statusFlags, jniConstants->midAtomicIntSet, c_status_flags);
    free(c_path);
    return r;
}

/** int git_status_list_new(git_status_list **out, git_repository *repo, const git_status_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr)
{
    git_status_list *c_out = 0;
    int r = git_status_list_new(&c_out, (git_repository *)repoPtr, (git_status_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_status_list_free(c_out); */
    return r;
}

/** size_t git_status_list_entrycount(git_status_list *statuslist); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListEntrycount)(JNIEnv *env, jclass obj, jlong statuslistPtr)
{
    size_t r = git_status_list_entrycount((git_status_list *)statuslistPtr);
    return r;
}

/** const git_status_entry * git_status_byindex(git_status_list *statuslist, size_t idx); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniByindex)(JNIEnv *env, jclass obj, jlong statuslistPtr, jint idx)
{
    const git_status_entry *r = git_status_byindex((git_status_list *)statuslistPtr, idx);
    return (jlong)r;
}

/** void git_status_list_free(git_status_list *statuslist); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniListFree)(JNIEnv *env, jclass obj, jlong statuslistPtr)
{
    git_status_list_free((git_status_list *)statuslistPtr);
}

/** int git_status_should_ignore(int *ignored, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniShouldIgnore)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path)
{
    int c_ignored;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_status_should_ignore(&c_ignored, (git_repository *)repoPtr, c_path);
    (*env)->CallVoidMethod(env, ignored, jniConstants->midAtomicIntSet, c_ignored);
    free(c_path);
    return r;
}

/** -------- Wrapper Body ---------- */
/** unsigned int      version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_status_options *)optionsPtr)->version;
}

/** git_status_show_t show*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetShow)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_status_options *)optionsPtr)->show;
}

/** unsigned int      flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_status_options *)optionsPtr)->flags;
}

/** git_strarray      pathspec*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsGetPathspec)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject outListPathSpec)
{
    j_strarray_to_java_list(env, &(((git_status_options *)optionsPtr)->pathspec), outListPathSpec);
}

/** git_tree          *baseline*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniOptionsGetBaseline)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (jlong)((git_status_options *)optionsPtr)->baseline;
}

/** unsigned int      version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version)
{
    ((git_status_options *)optionsPtr)->version = (unsigned int)version;
}

/** git_status_show_t show*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetShow)(JNIEnv *env, jclass obj, jlong optionsPtr, jint show)
{
    ((git_status_options *)optionsPtr)->show = (git_status_show_t)show;
}

/** unsigned int      flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags)
{
    ((git_status_options *)optionsPtr)->flags = (unsigned int)flags;
}

/** git_strarray      pathspec*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetPathspec)(JNIEnv *env, jclass obj, jlong optionsPtr, jobjectArray outPathspec)
{
    j_strarray_from_java(env, &(((git_status_options *)optionsPtr)->pathspec), outPathspec);
}

/** git_tree          *baseline*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetBaseline)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong baseline)
{
    ((git_status_options *)optionsPtr)->baseline = (git_tree *)baseline;
}

/** git_status_t status*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniEntryGetStatus)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_status_entry *)entryPtr)->status;
}

/** git_diff_delta *head_to_index*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniEntryGetHeadToIndex)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return (jlong)((git_status_entry *)entryPtr)->head_to_index;
}

/** git_diff_delta *index_to_workdir*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniEntryGetIndexToWorkdir)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return (jlong)((git_status_entry *)entryPtr)->index_to_workdir;
}

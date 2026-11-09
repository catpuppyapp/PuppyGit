#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_FILTER_LIST_H__
#define __GIT24J_FILTER_LIST_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_filterList_load(git_filter_list **filters, git_repository *repo, git_blob *blob, const char *path, int mode, int flags); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniLoad)(JNIEnv *env, jclass obj, jobject filters, jlong repoPtr, jlong blobPtr, jstring path, jint mode, jint flags);

    /** int git_filterList_contains(git_filter_list *filters, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniContains)(JNIEnv *env, jclass obj, jlong filtersPtr, jstring name);

    /** int git_filterList_apply_to_data(git_buf *out, git_filter_list *filters, git_buf *in); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToData)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jstring in);

    /** int git_filterList_apply_to_file(git_buf *out, git_filter_list *filters, git_repository *repo, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToFile)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jlong repoPtr, jstring path);

    /** int git_filterList_apply_to_blob(git_buf *out, git_filter_list *filters, git_blob *blob); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToBlob)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jlong blobPtr);

    /** int git_filterList_stream_data(git_filter_list *filters, git_buf *data, git_writestream *target); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamData)(JNIEnv *env, jclass obj, jlong filtersPtr, jstring data, jlong targetPtr);

    /** int git_filterList_stream_file(git_filter_list *filters, git_repository *repo, const char *path, git_writestream *target); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamFile)(JNIEnv *env, jclass obj, jlong filtersPtr, jlong repoPtr, jstring path, jlong targetPtr);

    /** int git_filterList_stream_blob(git_filter_list *filters, git_blob *blob, git_writestream *target); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamBlob)(JNIEnv *env, jclass obj, jlong filtersPtr, jlong blobPtr, jlong targetPtr);

    /** void git_filterList_free(git_filter_list *filters); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(FilterList_jniFree)(JNIEnv *env, jclass obj, jlong filtersPtr);

#ifdef __cplusplus
}
#endif
#endif
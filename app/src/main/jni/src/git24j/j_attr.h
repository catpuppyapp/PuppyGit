#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_ATTR_H__
#define __GIT24J_ATTR_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** git_attr_value_t git_attr_value(const char *attr); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniValue)(JNIEnv *env, jclass obj, jstring attr);

    /** int git_attr_get(const char **value_out, git_repository *repo, int flags, const char *path, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniGet)(JNIEnv *env, jclass obj, jobject valueOut, jlong repoPtr, jint flags, jstring path, jstring name);

    /** int git_attr_get_many(const char **values_out, git_repository *repo, int flags, const char *path, size_t num_attr, const char **names); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniGetMany)(JNIEnv *env, jclass obj, jobject valuesOut, jlong repoPtr, jint flags, jstring path, jobjectArray names);

    /** int git_attr_foreach(git_repository *repo, int flags, const char *path, git_attr_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jint flags, jstring path, jobject callback);

    /** int git_attr_cache_flush(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniCacheFlush)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_attr_add_macro(git_repository *repo, const char *name, const char *values); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniAddMacro)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring values);

    /** int git_attr_foreach_cb(const char *name, const char *value, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniForeachCb)(JNIEnv *env, jclass obj, jstring name, jstring value);

#ifdef __cplusplus
}
#endif
#endif
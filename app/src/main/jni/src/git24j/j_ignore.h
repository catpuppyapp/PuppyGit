#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_IGNORE_H__
#define __GIT24J_IGNORE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_ignore_add_rule(git_repository *repo, const char *rules); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniAddRule)(JNIEnv *env, jclass obj, jlong repoPtr, jstring rules);

    /** int git_ignore_clear_internal_rules(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniClearInternalRules)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_ignore_path_is_ignored(int *ignored, git_repository *repo, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniPathIsIgnored)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path);

#ifdef __cplusplus
}
#endif
#endif
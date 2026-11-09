#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_GRAPH_H__
#define __GIT24J_GRAPH_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_graph_ahead_behind(size_t *ahead, size_t *behind, git_repository *repo, const git_oid *local, const git_oid *upstream); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Graph_jniAheadBehind)(JNIEnv *env, jclass obj, jobject ahead, jobject behind, jlong repoPtr, jobject local, jobject upstream);

    /** int git_graph_descendant_of(git_repository *repo, const git_oid *commit, const git_oid *ancestor); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Graph_jniDescendantOf)(JNIEnv *env, jclass obj, jlong repoPtr, jobject commit, jobject ancestor);

#ifdef __cplusplus
}
#endif
#endif
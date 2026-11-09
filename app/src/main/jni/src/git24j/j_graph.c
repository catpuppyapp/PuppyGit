#include "j_graph.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_graph_ahead_behind(size_t *ahead, size_t *behind, git_repository *repo, const git_oid *local, const git_oid *upstream); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Graph_jniAheadBehind)(JNIEnv *env, jclass obj, jobject ahead, jobject behind, jlong repoPtr, jobject local, jobject upstream)
{
    size_t c_ahead;
    size_t c_behind;
    git_oid c_local;
    j_git_oid_from_java(env, local, &c_local);
    git_oid c_upstream;
    j_git_oid_from_java(env, upstream, &c_upstream);
    int r = git_graph_ahead_behind(&c_ahead, &c_behind, (git_repository *)repoPtr, &c_local, &c_upstream);
    (*env)->CallVoidMethod(env, ahead, jniConstants->midAtomicIntSet, c_ahead);
    (*env)->CallVoidMethod(env, behind, jniConstants->midAtomicIntSet, c_behind);
    return r;
}

/** int git_graph_descendant_of(git_repository *repo, const git_oid *commit, const git_oid *ancestor); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Graph_jniDescendantOf)(JNIEnv *env, jclass obj, jlong repoPtr, jobject commit, jobject ancestor)
{
    git_oid c_commit;
    j_git_oid_from_java(env, commit, &c_commit);
    git_oid c_ancestor;
    j_git_oid_from_java(env, ancestor, &c_ancestor);
    int r = git_graph_descendant_of((git_repository *)repoPtr, &c_commit, &c_ancestor);
    return r;
}

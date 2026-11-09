#include "j_reset.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_reset_reset(git_repository *repo, const git_object *target, git_reset_t reset_type, const git_checkout_options *checkout_opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniReset)(JNIEnv *env, jclass obj, jlong repoPtr, jlong targetPtr, jint reset_type, jlong checkoutOptsPtr)
{
    int r = git_reset((git_repository *)repoPtr, (git_object *)targetPtr, reset_type, (git_checkout_options *)checkoutOptsPtr);
    return r;
}

/** int git_reset_from_annotated(git_repository *repo, const git_annotated_commit *commit, git_reset_t reset_type, const git_checkout_options *checkout_opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniFromAnnotated)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jint reset_type, jlong checkoutOptsPtr)
{
    int r = git_reset_from_annotated((git_repository *)repoPtr, (git_annotated_commit *)commitPtr, reset_type, (git_checkout_options *)checkoutOptsPtr);
    return r;
}

/** int git_reset_default(git_repository *repo, const git_object *target, const git_strarray *pathspecs); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniDefault)(JNIEnv *env, jclass obj, jlong repoPtr, jlong targetPtr, jobjectArray pathspecs)
{
    git_strarray c_pathspecs;
    git_strarray_of_jobject_array(env, pathspecs, &c_pathspecs);
    int r = git_reset_default((git_repository *)repoPtr, (git_object *)targetPtr, &c_pathspecs);
    git_strarray_free(&c_pathspecs);
    return r;
}

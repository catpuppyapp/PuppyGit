#include "j_cherrypick.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** int git_cherrypick_options_init(git_cherrypick_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_cherrypick_options_init((git_cherrypick_options *)optsPtr, version);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_cherrypick_options *opts = (git_cherrypick_options *)malloc(sizeof(git_cherrypick_options));
    int r = git_cherrypick_options_init(opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

/** unsigned int mainline*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_cherrypick_options *)optionsPtr)->mainline;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsSetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr, jint mainline)
{
    ((git_cherrypick_options *)optionsPtr)->mainline = (unsigned int)mainline;
}

/** git_merge_options merge_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetMergeOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (jlong)(&(((git_cherrypick_options *)optionsPtr)->merge_opts));
}

/** git_checkout_options checkout_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_checkout_options *r = &((git_cherrypick_options *)optionsPtr)->checkout_opts;
    return (jlong)r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_cherrypick_options *opts = (git_cherrypick_options *)optsPtr;
    free(opts);
}

/** int git_cherrypick_commit(git_index **out, git_repository *repo, git_commit *cherrypick_commit, git_commit *our_commit, unsigned int mainline, const git_merge_options *merge_options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniCommit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong cherrypickCommitPtr, jlong ourCommitPtr, jint mainline, jlong mergeOptionsPtr)
{
    git_index *c_out;
    int r = git_cherrypick_commit(&c_out, (git_repository *)repoPtr, (git_commit *)cherrypickCommitPtr, (git_commit *)ourCommitPtr, mainline, (git_merge_options *)mergeOptionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_index_free(c_out); */
    return r;
}

/** int git_cherrypick_cherrypick(git_repository *repo, git_commit *commit, const git_cherrypick_options *cherrypick_options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniCherrypick)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jlong cherrypickOptionsPtr)
{
    int r = git_cherrypick((git_repository *)repoPtr, (git_commit *)commitPtr, (git_cherrypick_options *)cherrypickOptionsPtr);
    return r;
}

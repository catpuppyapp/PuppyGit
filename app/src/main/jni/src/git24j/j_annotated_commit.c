#include "j_annotated_commit.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
extern j_constants_t *jniConstants;

/** int git_annotated_commit_from_ref(git_annotated_commit **out, git_repository *repo, const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromRef)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jlong refPtr)
{
    git_annotated_commit *c_ac = 0;
    int e = git_annotated_commit_from_ref(&c_ac, (git_repository *)repoPtr, (git_reference *)refPtr);
    (*env)->CallVoidMethod(env, outAc, jniConstants->midAtomicLongSet, (jlong)c_ac);
    return e;
}

/** int git_annotated_commit_from_fetchhead(git_annotated_commit **out, git_repository *repo, const char *branch_name, const char *remote_url, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromFetchHead)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jstring branchName, jstring remoteUrl, jobject oid)
{
    assert(repoPtr && branchName && remoteUrl && oid && "bad user input");
    git_annotated_commit *c_ac = 0;
    char *branch_name = j_copy_of_jstring(env, branchName, true);
    char *remote_url = j_copy_of_jstring(env, remoteUrl, true);
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int e = git_annotated_commit_from_fetchhead(&c_ac, (git_repository *)repoPtr, branch_name, remote_url, &c_oid);
    (*env)->CallVoidMethod(env, outAc, jniConstants->midAtomicLongSet, (jlong)c_ac);
    free(branch_name);
    free(remote_url);
    return e;
}

/** int git_annotated_commit_lookup(git_annotated_commit **out, git_repository *repo, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniLookup)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jobject oid)
{
    git_annotated_commit *c_out = 0;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int e = git_annotated_commit_lookup(&c_out, (git_repository *)repoPtr, &c_oid);
    (*env)->CallVoidMethod(env, outAc, jniConstants->midAtomicLongSet, (jlong)c_out);
    return e;
}

/** int git_annotated_commit_from_revspec(git_annotated_commit **out, git_repository *repo, const char *revspec); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromRevspec)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jstring revspec)
{
    git_annotated_commit *c_out = 0;
    char *c_revspec = j_copy_of_jstring(env, revspec, false);
    int e = git_annotated_commit_from_revspec(&c_out, (git_repository *)repoPtr, c_revspec);
    (*env)->CallVoidMethod(env, outAc, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_revspec);
    return e;
}

/** const git_oid * git_annotated_commit_id(const git_annotated_commit *commit); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(AnnotatedCommit_jniId)(JNIEnv *env, jclass obj, jlong acPtr)
{
    const git_oid *c_oid = git_annotated_commit_id((git_annotated_commit *)acPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** const char * git_annotated_commit_ref(const git_annotated_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(AnnotatedCommit_jniRef)(JNIEnv *env, jclass obj, jlong acPtr)
{
    const char *ref = git_annotated_commit_ref((git_annotated_commit *)acPtr);
    return (*env)->NewStringUTF(env, ref);
}

/** void git_annotated_commit_free(git_annotated_commit *commit); */
JNIEXPORT void JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFree)(JNIEnv *env, jclass obj, jlong acPtr)
{
    git_annotated_commit_free((git_annotated_commit *)acPtr);
}

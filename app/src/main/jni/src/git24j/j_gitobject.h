#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_GITOBJECT_H__
#define __GIT24J_GITOBJECT_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** void git_object_free(git_object *object); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(GitObject_jniFree)(JNIEnv *env, jclass obj, jlong objPtr);

    /** git_object_t git_object_type(const git_object *obj); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniType)(JNIEnv *env, jclass obj, jlong objPtr);

    /** const git_oid * git_object_id(const git_object *obj); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(GitObject_jniId)(JNIEnv *env, jclass obj, jlong objPtr, jobject outId);

    /**int git_object_short_id(git_buf *out, const git_object *obj); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniShortId)(JNIEnv *env, jclass obj, jobject outBuf, jlong objPtr);

    /** int git_object_lookup(git_object **object, git_repository *repo, const git_oid *id, git_object_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniLookup)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jobject oid, jint objType);

    /**int git_object_lookup_prefix(git_object **object_out, git_repository *repo, const git_oid *id, size_t len, git_object_t type);*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring oidStr, jint objType);

    /**git_repository * git_object_owner(const git_object *obj); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(GitObject_jniOwner)(JNIEnv *env, jclass obj, jlong objPtr);

    /** int git_object_peel(git_object **peeled, const git_object *object, git_object_t target_type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong objPtr, jint objType);

    /** int git_object_dup(git_object **dest, git_object *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniDup)(JNIEnv *env, jclass obj, jobject outObj, jlong objPtr);

#ifdef __cplusplus
}
#endif
#endif

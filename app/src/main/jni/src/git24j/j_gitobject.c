#include "j_gitobject.h"
#include "j_mappers.h"
#include "j_util.h"
#include <stdio.h>
extern j_constants_t *jniConstants;

JNIEXPORT void JNICALL J_MAKE_METHOD(GitObject_jniFree)(JNIEnv *env, jclass obj, jlong objPtr)
{
    git_object_free((git_object *)objPtr);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniType)(JNIEnv *env, jclass obj, jlong objPtr)
{
    return git_object_type((git_object *)objPtr);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(GitObject_jniId)(JNIEnv *env, jclass obj, jlong objPtr, jobject outId)
{
    const git_oid *c_oid = git_object_id((git_object *)objPtr);
    j_git_oid_to_java(env, c_oid, outId);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniShortId)(JNIEnv *env, jclass obj, jobject outBuf, jlong objPtr)
{
    git_buf c_buf = {0};
    int error = git_object_short_id(&c_buf, (git_object *)objPtr);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniLookup)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jobject oid, jint objType)
{
    git_object *out_obj = 0;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int error = git_object_lookup(&out_obj, (git_repository *)repoPtr, &c_oid, (git_object_t)objType);
    (*env)->CallVoidMethod(env, outObj, jniConstants->midAtomicLongSet, (jlong)out_obj);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring oidStr, jint objType)
{
    git_object *out_obj = 0;
    git_oid c_oid;
    int short_id_len;
    int e = j_git_short_id_from_java(env, oidStr, &c_oid, &short_id_len);
    if (e != 0)
    {
        return e;
    }

    int error = git_object_lookup_prefix(&out_obj, (git_repository *)repoPtr, &c_oid, (size_t)short_id_len, (git_object_t)objType);
    (*env)->CallVoidMethod(env, outObj, jniConstants->midAtomicLongSet, (jlong)out_obj);
    return error;
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(GitObject_jniOwner)(JNIEnv *env, jclass obj, jlong objPtr)
{
    return (long)git_object_owner((git_object *)objPtr);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong objPtr, jint objType)
{
    git_object *out_obj = 0;
    int error = git_object_peel(&out_obj, (git_object *)objPtr, (git_object_t)objType);
    (*env)->CallVoidMethod(env, outObj, jniConstants->midAtomicLongSet, (jlong)out_obj);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniDup)(JNIEnv *env, jclass obj, jobject outObj, jlong objPtr)
{
    git_object *out_obj = 0;
    int error = git_object_dup(&out_obj, (git_object *)objPtr);
    (*env)->CallVoidMethod(env, outObj, jniConstants->midAtomicLongSet, (jlong)out_obj);
    return error;
}

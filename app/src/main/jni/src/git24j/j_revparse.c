#include "j_revparse.h"
#include "j_ensure.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
extern j_constants_t *jniConstants;

void j_save_revspec_c_value(JNIEnv *env, git_revspec *rev_spec, jobject revSpec)
{
    assert(revSpec && "receiving object must not be null");
    jclass clz = (*env)->GetObjectClass(env, revSpec);
    assert(clz && "Could not find Revspec class from given revspec object");
    if (rev_spec->from)
    {
        j_call_setter_long(env, clz, revSpec, "setFrom", (jlong)(rev_spec->from));
    }

    if (rev_spec->to)
    {
        j_call_setter_long(env, clz, revSpec, "setTo", (jlong)(rev_spec->to));
    }
    j_call_setter_int(env, clz, revSpec, "setFlags", (jlong)(rev_spec->flags));
    (*env)->DeleteLocalRef(env, clz);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniLookup)(JNIEnv *env, jclass obj, jobject revspec, jlong repoPtr, jstring spec)
{
    git_revspec c_revspec;
    char *c_spec = j_copy_of_jstring(env, spec, false);
    int error = git_revparse(&c_revspec, (git_repository *)repoPtr, c_spec);
    j_save_revspec_c_value(env, &c_revspec, revspec);
    free(c_spec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniSingle)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring spec)
{
    git_object *c_out = 0;
    char *c_spec = j_copy_of_jstring(env, spec, false);
    int error = git_revparse_single(&c_out, (git_repository *)repoPtr, c_spec);
    (*env)->CallVoidMethod(env, outObj, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_spec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniExt)(JNIEnv *env, jclass obj, jobject outObj, jobject outRef, jlong repoPtr, jstring spec)
{
    git_object *c_out_obj = 0;
    git_reference *c_out_ref = 0;
    char *c_spec = j_copy_of_jstring(env, spec, false);
    int error = git_revparse_ext(&c_out_obj, &c_out_ref, (git_repository *)repoPtr, c_spec);
    (*env)->CallVoidMethod(env, outObj, jniConstants->midAtomicLongSet, (jlong)c_out_obj);
    (*env)->CallVoidMethod(env, outRef, jniConstants->midAtomicLongSet, (jlong)c_out_ref);
    free(c_spec);
    return error;
}

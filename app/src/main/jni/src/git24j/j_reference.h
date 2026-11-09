#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REFERENCE_H__
#define __GIT24J_REFERENCE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /**int git_reference_lookup(git_reference **out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name);
    /**int git_reference_name_to_id(git_oid *out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNameToId)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring name);
    /**int git_reference_dwim(git_reference **out, git_repository *repo, const char *shorthand); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDwim)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring shorthand);
    /**int git_reference_symbolic_create_matching(git_reference **out, git_repository *repo, const char *name, const char *target, int force, const char *current_value, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicCreateMatching)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jstring target, jint force, jstring currentValue, jstring logMessage);
    /**int git_reference_symbolic_create(git_reference **out, git_repository *repo, const char *name, const char *target, int force, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jstring target, jint force, jstring logMessage);
    /**int git_reference_create(git_reference **out, git_repository *repo, const char *name, const git_oid *id, int force, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jobject oid, jint force, jstring logMessage);
    /**int git_reference_create_matching(git_reference **out, git_repository *repo, const char *name, const git_oid *id, int force, const git_oid *current_id, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCreateMatching)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jobject oid, jint force, jobject currentId, jstring logMessage);
    /**const git_oid * git_reference_target(const git_reference *ref); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reference_jniTarget)(JNIEnv *env, jclass obj, jlong refPtr);
    /**const git_oid * git_reference_target_peel(const git_reference *ref); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reference_jniTargetPeel)(JNIEnv *env, jclass obj, jlong refPtr);
    /**const char * git_reference_symbolic_target(const git_reference *ref); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniSymbolicTarget)(JNIEnv *env, jclass obj, jlong refPtr);
    /**git_reference_t git_reference_type(const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniType)(JNIEnv *env, jclass obj, jlong refPtr);
    /**const char * git_reference_name(const git_reference *ref); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniName)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_resolve(git_reference **out, const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniResolve)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr);
    /**git_repository * git_reference_owner(const git_reference *ref); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reference_jniOwner)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_symbolic_set_target(git_reference **out, git_reference *ref, const char *target, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring target, jstring logMessage);
    /**int git_reference_set_target(git_reference **out, git_reference *ref, const git_oid *id, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jobject oid, jstring logMessage);
    /**int git_reference_rename(git_reference **new_ref, git_reference *ref, const char *new_name, int force, const char *log_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRename)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring newName, jint force, jstring logMessage);
    /**int git_reference_delete(git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_remove(git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);
    /**int git_reference_list(git_strarray *array, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniList)(JNIEnv *env, jclass obj, jobject strList, jlong repoPtr);
    /**int git_reference_foreach(git_repository *repo, git_reference_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback);
    /**int git_reference_foreach_name(git_repository *repo, git_reference_foreach_name_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachName)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);
    /**int git_reference_dup(git_reference **dest, git_reference *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong sourcePtr);
    /**void git_reference_free(git_reference *ref); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniFree)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_cmp(const git_reference *ref1, const git_reference *ref2); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCmp)(JNIEnv *env, jclass obj, jlong ref1Ptr, jlong ref2Ptr);
    /**int git_reference_iterator_new(git_reference_iterator **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr);
    /**int git_reference_iterator_glob_new(git_reference_iterator **out, git_repository *repo, const char *glob); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr, jstring glob);
    /**int git_reference_next(git_reference **out, git_reference_iterator *iter); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jlong iterPtr);
    /**int git_reference_next_name(const char **out, git_reference_iterator *iter); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNextName)(JNIEnv *env, jclass obj, jobject outName, jlong iterPtr);
    /**void git_reference_iterator_free(git_reference_iterator *iter); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);
    /**int git_reference_foreach_glob(git_repository *repo, const char *glob, git_reference_foreach_name_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachGlob)(JNIEnv *env, jclass obj, jlong repoPtr, jstring glob, jobject callback);
    /**int git_reference_has_log(git_repository *repo, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniHasLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname);
    /**int git_reference_ensure_log(git_repository *repo, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniEnsureLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname);
    /**int git_reference_is_branch(const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsBranch)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_is_remote(const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsRemote)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_is_tag(const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsTag)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_is_note(const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsNote)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_normalize_name(char *buffer_out, size_t buffer_size, const char *name, unsigned int flags); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNormalizeName)(JNIEnv *env, jclass obj, jobject bufferOut, jstring name, jint flags);
    /**int git_reference_peel(git_object **out, const git_reference *ref, git_object_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong refPtr, jint objType);
    /**int git_reference_is_valid_name(const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsValidName)(JNIEnv *env, jclass obj, jstring refname);
    /**const char * git_reference_shorthand(const git_reference *ref); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniShorthand)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_reference_foreach_cb(git_reference *reference, void *payload); */
    /**int git_reference_foreach_name_cb(const char *name, void *payload); */

#ifdef __cplusplus
}
#endif
#endif

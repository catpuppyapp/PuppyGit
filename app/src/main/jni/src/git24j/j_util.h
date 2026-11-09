
#ifndef __GIT24J_UTIL_H__
#define __GIT24J_UTIL_H__
#ifdef __cplusplus
extern "C"
{
#endif

#include <git2.h>
#include <jni.h>
#include <stdbool.h>

    /**TODO: move this to mappers.h */
    /** cast c pointer to long and save it in jobject, which should have method like "object.setterName(long ptr)". */
    void j_save_c_pointer(JNIEnv *env, void *ptr, jobject object, const char *setterName);

    /**
     * Copy values from java String[] to git_strarray.
     * 
     * Note 1: this will not free existing data in git_strarray
     * Note 2: returned {@code jstrarray} needs to be free-ed by git_strarray_free separately.
     */
    void git_strarray_of_jobject_array(JNIEnv *env, jobjectArray jstrarr, git_strarray *out);

    /** Make a copy of sub string, returned destination, returned string must be freed by the caller */
    char *new_substr(const char *str, size_t len);

#ifdef __cplusplus
}
#endif
#endif

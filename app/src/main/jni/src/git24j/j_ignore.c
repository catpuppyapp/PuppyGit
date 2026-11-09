#include "j_ignore.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** int git_ignore_add_rule(git_repository *repo, const char *rules); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniAddRule)(JNIEnv *env, jclass obj, jlong repoPtr, jstring rules)
{
    char *c_rules = j_copy_of_jstring(env, rules, true);
    int r = git_ignore_add_rule((git_repository *)repoPtr, c_rules);
    free(c_rules);
    return r;
}

/** int git_ignore_clear_internal_rules(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniClearInternalRules)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    int r = git_ignore_clear_internal_rules((git_repository *)repoPtr);
    return r;
}

/** int git_ignore_path_is_ignored(int *ignored, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniPathIsIgnored)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path)
{
    int c_ignored;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_ignore_path_is_ignored(&c_ignored, (git_repository *)repoPtr, c_path);
    (*env)->CallVoidMethod(env, ignored, jniConstants->midAtomicIntSet, c_ignored);
    free(c_path);
    return r;
}


#ifndef __GIT24J_COMMON_H__
#define __GIT24J_COMMON_H__
#ifdef __cplusplus
extern "C"
{
#endif

#define J_MAKE_METHOD(CM) Java_com_github_git24j_core_##CM
#define J_CLZ_PREFIX "com/github/git24j/core/"

/** default max length for building string buffer. */
#define J_DEFAULT_MAX_MSG_LEN 4096

#define J_NO_CLASS_ERROR "java/lang/NoClassDefFoundError"

#ifdef __cplusplus
}
#endif
#endif

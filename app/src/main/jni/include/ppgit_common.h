
#ifndef __GIT24J_COMMON_H__
#define __GIT24J_COMMON_H__
#ifdef __cplusplus
extern "C"
{
#endif


#define J_MAKE_METHOD(CM) Java_com_catpuppyapp_puppygit_jni_##CM
#define J_CLZ_PREFIX "com/catpuppyapp/puppygit/jni/"

/** default max length for building string buffer. */
#define J_DEFAULT_MAX_MSG_LEN 4096

#define J_NO_CLASS_ERROR "java/lang/NoClassDefFoundError"


#define ALOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

//计算数组有几个元素，NELEM含义为 N Element，sizeof(x)计算数组总共占多少字节，sizeof(x[0])计算的是单个元素的大小，总字节数除单个元素占的字节数，得到的就是数组元素数量
//注册native方法时用，手动指定也行
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))



#ifdef __cplusplus
}
#endif
#endif

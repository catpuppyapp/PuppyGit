#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <unistd.h>
#include <errno.h> //x86_64  arm64 errno in this
#include <sys/stat.h>
#include <sys/types.h>
#include <dlfcn.h>
#include <inttypes.h>
#include <stdint.h>
#include <stdbool.h>
#include <fcntl.h>
#include <sys/system_properties.h>
#include <android/api-level.h>
#include <android/log.h>
#include <sys/time.h>
#include "bytehook.h"

#define SAF_HOOK_J_MAKE_METHOD(CM) Java_com_catpuppyapp_puppygit_utils_saf_##CM


static JavaVM* g_vm;
static JNIEnv* g_jnienv;
static jclass g_javaClass = NULL;
static jmethodID g_javaGetFD = NULL;
static jmethodID g_javaMkdir = NULL;
static jmethodID g_javaRemove = NULL;
//static JNINativeMethod g_Methods[] = {
//        {"nativeHookFile","(Ljava/lang/String;Ljava/lang/String;)V", (void*)nativeHookFile},
//        {"nativeInitSafJavaCallbacks", "()V", (void*)nativeInitSafJavaCallbacks},
//};

#ifndef TAG
#define TAG "#YURIC_safhook"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#endif
#define PATH_MAX_LEN 256

typedef FILE* (*fopen_t)(const char *restrict pathname, const char *restrict mode);
typedef int (*remove_t)(const char *pathname);
typedef int (*mkdir_t)(const char *pathname, mode_t mode);


char* jstr2cstr(JNIEnv* env, jstring jstr) 
{
	char* rtn = NULL;
 
	jclass clsstring = (*env)->FindClass(env, "java/lang/String");
	jstring strencode = (*env)->NewStringUTF(env, "utf-8"); // "GB2312"
	jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid, strencode);
	jsize alen = (*env)->GetArrayLength(env, barr);
	jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char *)malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	(*env)->ReleaseByteArrayElements(env, barr, ba, 0);
	return rtn;
}

FILE *fopen_saf(const char *pathname, const char *mode)
{
    FILE* fp=NULL;
    JNIEnv* env = NULL;
    (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);
    if(!env)
    {
        LOGE("fopen_asf, env AttachCurrentThread failed!\n");
        FILE* result = BYTEHOOK_CALL_PREV(fopen_saf, fopen_t, pathname, mode);

        // 执行 stack 清理（不可省略）
        BYTEHOOK_POP_STACK();
        return result;
    }

    int mode2=0;
    if(mode[0] == 'w') mode2=1;
    
    fp = BYTEHOOK_CALL_PREV(fopen_saf, fopen_t, pathname, mode);
    if(!(fp || mode2 == 0 || errno != EACCES))
    {
        char buf[PATH_MAX_LEN];
        getcwd(buf, PATH_MAX_LEN);
        //LOGI("before fopen(%s, %s), cwd=%s\n", pathname, mode, buf);    
        jstring s_pathname = (*env)->NewStringUTF(env, pathname);
        jstring s_curdir = (*env)->NewStringUTF(env, buf);
        
        int fd = (*env)->CallStaticIntMethod(env, g_javaClass, g_javaGetFD, s_curdir, s_pathname, mode2 );
        (*env)->DeleteLocalRef(env, s_curdir);
        (*env)->DeleteLocalRef(env, s_pathname);
        fp = fdopen(fd, mode);
        //LOGI("after fopen_saf(%s, %s),fp=%x, cwd=%s\n", pathname, mode, (unsigned int)fp,buf);
    }

    BYTEHOOK_POP_STACK();
    return fp;
}

int mkdir_saf(const char *pathname, mode_t mode)
{
    LOGI("mkdir_saf, called");
    int ret = BYTEHOOK_CALL_PREV(mkdir_saf, mkdir_t, pathname, mode);
    BYTEHOOK_POP_STACK();

    LOGI("mkdir_saf, called_2, ret=%d", ret);
    if(ret == 0) {
        return ret;
    }

    JNIEnv* env = NULL;
    (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);
    if(!env)
    {
        LOGE("mkdir_saf, env AttachCurrentThread failed!\n");

        int result = BYTEHOOK_CALL_PREV(mkdir_saf, mkdir_t, pathname, mode);
        // 执行 stack 清理（不可省略）
        BYTEHOOK_POP_STACK();
        return result;
    }


    LOGI("before mkdir_saf(%s, %d)!\n", pathname, mode);
    char buf[PATH_MAX_LEN];
    getcwd(buf, PATH_MAX_LEN);
    jstring s_pathname = (*env)->NewStringUTF(env, pathname);
    jstring s_curdir = (*env)->NewStringUTF(env, buf);

    ret = (*env)->CallStaticIntMethod(env, g_javaClass, g_javaMkdir, s_curdir, s_pathname, mode);
    (*env)->DeleteLocalRef(env, s_curdir);
    (*env)->DeleteLocalRef(env, s_pathname);
    return ret;
}

int remove_saf(const char *pathname){

    int ret=remove(pathname);
    if(ret==0) return ret;

    JNIEnv* env = NULL;
    (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);
    if(!env)
    {
        LOGE("remove_saf, env AttachCurrentThread failed!\n");
        int result = BYTEHOOK_CALL_PREV(remove_saf, remove_t, pathname);

        // 执行 stack 清理（不可省略）
        BYTEHOOK_POP_STACK();
        return result;
    }

    //LOGI("beforre remove_saf(%s)", pathname);
    char buf[PATH_MAX_LEN];
    getcwd(buf, PATH_MAX_LEN);
    jstring s_pathname = (*env)->NewStringUTF(env, pathname);
    jstring s_curdir = (*env)->NewStringUTF(env, buf);
    
     ret = (*env)->CallStaticIntMethod(env, g_javaClass, g_javaRemove, s_curdir, s_pathname);
    (*env)->DeleteLocalRef(env, s_curdir);
    (*env)->DeleteLocalRef(env, s_pathname);

    int result = BYTEHOOK_CALL_PREV(remove_saf, remove_t, pathname);
    // 执行 stack 清理（不可省略）
    BYTEHOOK_POP_STACK();
    return result;
}

JNIEXPORT void JNICALL SAF_HOOK_J_MAKE_METHOD(SafFile_nativeInitSafJavaCallbacks)(JNIEnv* env, jclass clazz)
{
    LOGI("In nativeInitSafJavaCallbacks start!");
    g_javaGetFD=(*env)->GetStaticMethodID(env, clazz, "getFD", "(Ljava/lang/String;Ljava/lang/String;I)I");
    g_javaMkdir=(*env)->GetStaticMethodID(env, clazz, "mkdir", "(Ljava/lang/String;Ljava/lang/String;I)I");
    g_javaRemove = (*env)->GetStaticMethodID(env, clazz, "remove", "(Ljava/lang/String;Ljava/lang/String;)I");
    LOGI("In nativeInitSafJavaCallbacks finished!");
}



static void hacker_bytehook_hooked(bytehook_stub_t task_stub, int status_code, const char *caller_path_name, const char *sym_name, void *new_func, void *prev_func, void *arg)
{
    LOGI("hooked: status_code=%d", status_code);
    LOGI(">>>>> function hooked. stub: %" PRIxPTR", status: %d, caller_path_name: %s, sym_name: %s, new_func: %" PRIxPTR", prev_func: %" PRIxPTR", arg: %" PRIxPTR,
         (uintptr_t)task_stub, status_code, caller_path_name, sym_name, (uintptr_t)new_func, (uintptr_t)prev_func, (uintptr_t)arg);

}

JNIEXPORT int JNICALL SAF_HOOK_J_MAKE_METHOD(SafFile_nativeHookFile)(JNIEnv* env, jclass clazz, jstring hooksoStr, jstring soPath){
    g_jnienv = env;
    //bytehook_add_dlopen_callback(hacker_bytehook_pre_dlopen, hacker_bytehook_post_dlopen, NULL);

    jint result = (*env)->GetJavaVM(env, &g_vm); // 获取 JVM 对象

    if (result != JNI_OK) {
        // 处理错误
        LOGE("get javaVM err");
    } else {
        LOGI("get javaVM success");
    }

    g_javaClass = (*env) -> NewGlobalRef(env, clazz);

    LOGI("register jni hook functions");
    struct timeval start, end;
    gettimeofday(&start, NULL);

    char* c_hooksoStr = jstr2cstr(env, hooksoStr);
    LOGI("c_hooksoStr: %s", c_hooksoStr);
    //如果需要 unhook，得把返回值存下来
//    bytehook_hook_all(
//            NULL,
//            "fopen",
//            fopen_saf,
//            hacker_bytehook_hooked,
//            NULL);

    bytehook_hook_single(
            c_hooksoStr,
//            "libgit2.so",
            NULL,
            "fopen",
            fopen_saf,
            hacker_bytehook_hooked,
            NULL);
//
//    bytehook_hook_all(
//            NULL,
//            "mkdir",
//            mkdir_saf,
//            hacker_bytehook_hooked,
//            NULL);

    bytehook_hook_single(
            c_hooksoStr,
//            "libgit2.so",
            NULL,
            "mkdir",
            mkdir_saf,
            hacker_bytehook_hooked,
            NULL);
//
//    bytehook_hook_all(
//            NULL,
//            "remove",
//            remove_saf,
//            hacker_bytehook_hooked,
//            NULL);

    bytehook_hook_single(
            c_hooksoStr,
//            "libgit2.so",
            NULL,
            "remove",
            remove_saf,
            hacker_bytehook_hooked,
            NULL);

    gettimeofday(&end, NULL);
    LOGI("bytehook hook cost: %" PRIu64 " us",
            (uint64_t)(end.tv_sec * 1000000 + end.tv_usec) - (uint64_t)(start.tv_sec * 1000000 + start.tv_usec));

    free(c_hooksoStr);

    return 0;
}

//int registerSafJni(JavaVM* vm, JNIEnv* env, jclass clazz)
//{
//    LOGI("In registerSafJni start!");
//    int ret;
//    g_vm = vm;
//    g_javaClass = (*env) -> NewGlobalRef(env, clazz);
//    ret = (*env)->RegisterNatives(env, g_javaClass,
//          g_Methods, sizeof(g_Methods)/sizeof((g_Methods)[0]));
//    LOGI("In registerSafJni finished!");
//    return ret;
//}


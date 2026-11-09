#include "j_writestream.h"
#include "j_mappers.h"
#include "j_util.h"

/** int (*)(git_writestream *, const char *, size_t); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(WriteStream_jniWrite)(JNIEnv *env, jclass obj, jlong wsPtr, jbyteArray content)
{
    int out_len;
    unsigned char *c_bytes = j_unsigned_chars_from_java(env, content, &out_len);
    git_writestream *c_ws = (git_writestream *)wsPtr;
    int e = c_ws->write(c_ws, (const char *)c_bytes, out_len);
    free(c_bytes);
    return e;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(WriteStream_jniFree)(JNIEnv *env, jclass obj, jlong wsPtr)
{
    git_writestream *c_ws = (git_writestream *)wsPtr;
    c_ws->free(c_ws);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(WriteStream_jniClose)(JNIEnv *env, jclass obj, jlong wsPtr)
{
    git_writestream *c_ws = (git_writestream *)wsPtr;
    return c_ws->close(c_ws);
}

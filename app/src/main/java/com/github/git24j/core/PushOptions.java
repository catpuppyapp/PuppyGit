package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.git24j.core.Remote.*;

public class PushOptions extends CAutoReleasable {
    public static final int VERSION = 1;

    protected PushOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    public static PushOptions create(int version) {
        PushOptions out = new PushOptions(false, 0);
        Error.throwIfNeeded(jniPushOptionsNew(out._rawPtr, version));
        return out;
    }

    public static PushOptions createDefault() {
        return create(VERSION);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniPushOptionsFree(cPtr);
    }

    public int getVersion() {
        return jniPushOptionsGetVersion(getRawPointer());
    }

    public void setVersion(int version) {
        jniPushOptionsSetVersion(getRawPointer(), version);
    }

    public int getPbParallelism() {
        return jniPushOptionsGetPbParallelism(getRawPointer());
    }

    public void setPbParallelism(int pbParallelism) {
        jniPushOptionsSetPbParallelism(getRawPointer(), pbParallelism);
    }

    @CheckForNull
    public Remote.Callbacks getCallbacks() {
        long ptr = jniPushOptionsGetCallbacks(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new Remote.Callbacks(true, ptr);
    }

    @CheckForNull
    public Proxy.Options getProxyOpts() {
        long ptr = jniPushOptionsGetProxyOpts(getRawPointer());
        return ptr == 0 ? null : new Proxy.Options(true, ptr);
    }

    @Nonnull
    public List<String> getCustomHeaders() {
        List<String> out = new ArrayList<>();
        jniPushOptionsGetCustomHeaders(getRawPointer(), out);
        return out;
    }

    public void setCustomHeaders(String[] customHeaders) {
        jniPushOptionsSetCustomHeaders(getRawPointer(), customHeaders);
    }

    @Nonnull
    public List<String> getRemotePushOptions() {
        List<String> out = new ArrayList<>();
        jniPushOptionsGetRemotePushOptions(getRawPointer(), out);
        return out;
    }

    public void setRemotePushOptions(String[] remotePushOptions) {
        jniPushOptionsSetRemotePushOptions(getRawPointer(), remotePushOptions);
    }
}

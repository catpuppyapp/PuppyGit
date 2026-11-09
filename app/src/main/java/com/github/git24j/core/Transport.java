package com.github.git24j.core;

/** Data structure to communicate with a remote. */
public class Transport extends CAutoReleasable {
    protected Transport(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {}

    @FunctionalInterface
    public interface CertificateCheckCb {
        int accept(Cert cert, boolean valid, String host);
    }
}

package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.EnumSet;

public class Cert extends CAutoReleasable {
    /** create empty hostkey struct for testing */
    static native long jniHostkeyCreateEmptyForTesting();

    /** unsigned char hash_md5[16] */
    static native byte[] jniHostkeyGetHashMd5(long hostkeyPtr);

    /** unsigned char hash_sha1[20] */
    static native byte[] jniHostkeyGetHashSha1(long hostkeyPtr);

    /** unsigned char hash_sha256[32] */
    static native byte[] jniHostkeyGetHashSha256(long hostkeyPtr);

    /** git_cert parent */
    static native long jniHostkeyGetParent(long hostkeyPtr);

    /** git_cert_ssh_t type */
    static native int jniHostkeyGetType(long hostkeyPtr);

    /** git_cert parent */
    static native long jniX509GetParent(long x509Ptr);

    protected Cert(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }

    public enum T {
        /** No information about the certificate is available. This may happen when using curl. */
        NONE,
        /** The `data` argument to the callback will be a pointer to the DER-encoded data. */
        X509,
        /**
         * The `data` argument to the callback will be a pointer to a `git_cert_hostkey` structure.
         */
        HOSTKEY_LIBSSH2,
        /**
         * The `data` argument to the callback will be a pointer to a `git_strarray` with
         * `name:content` strings containing information about the certificate. This is used when
         * using curl.
         */
        STRARRAY,
    }

    public enum SshT implements IBitEnum {
        /** MD5 is available */
        MD5(1 << 0),
        /** SHA-1 is available */
        SHA1(1 << 1),
        /** SHA-256 is available */
        SHA256(1 << 2);

        private final int _bit;

        SshT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class HostKey extends CAutoReleasable {
        protected HostKey(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        static HostKey createEmpty() {
            return new HostKey(false, jniHostkeyCreateEmptyForTesting());
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        /** Note: returned Cert only valid when HostKey is not freed */
        @CheckForNull
        public Cert getParent() {
            long ptr = jniHostkeyGetParent(getRawPointer());
            return ptr == 0 ? null : new Cert(true, ptr);
        }

        public EnumSet<SshT> getType() {
            return IBitEnum.parse(jniHostkeyGetType(getRawPointer()), SshT.class);
        }

        public byte[] getHashMd5() {
            return jniHostkeyGetHashMd5(getRawPointer());
        }

        public byte[] getHashSha1() {
            return jniHostkeyGetHashSha1(getRawPointer());
        }

        public byte[] getHashSha256() {
            return jniHostkeyGetHashSha256(getRawPointer());
        }
    }

    public static class X509 extends CAutoReleasable {
        protected X509(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        @CheckForNull
        public Cert getParent() {
            long ptr = jniX509GetParent(getRawPointer());
            return ptr == 0 ? null : new Cert(true, ptr);
        }
    }
}

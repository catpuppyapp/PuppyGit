package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Credential extends CAutoReleasable {
    /** int git_credential_default_new(git_credential **out); */
    static native int jniDefaultNew(AtomicLong out);

    /** void git_credential_free(git_credential *cred); */
    static native void jniFree(long cred);

    /** const char * git_credential_get_username(git_credential *cred); */
    static native String jniGetUsername(long cred);

    /** int git_credential_has_username(git_credential *cred); */
    static native int jniHasUsername(long cred);

    /** int git_credential_ssh_key_from_agent(git_credential **out, const char *username); */
    static native int jniSshKeyFromAgent(AtomicLong out, String username);

    /**
     * int git_credential_ssh_key_memory_new(git_credential **out, const char *username, const char
     * *publickey, const char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyMemoryNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /**
     * int git_credential_ssh_key_new(git_credential **out, const char *username, const char
     * *publickey, const char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /** int git_credential_username_new(git_credential **out, const char *username); */
    static native int jniUsernameNew(AtomicLong out, String username);

    /**
     * int git_credential_userpass(git_credential **out, const char *url, const char *user_from_url,
     * unsigned int allowed_types, void *payload);
     */
    static native int jniUserpass(
            AtomicLong out, String url, String userFromUrl, int allowedTypes, long payloadPtr);

    /**
     * int git_credential_userpass_plaintext_new(git_credential **out, const char *username, const
     * char *password);
     */
    static native int jniUserpassPlaintextNew(AtomicLong out, String username, String password);

    protected Credential(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Create a new plain-text username and password credential object. The supplied credential
     * parameter will be internally duplicated.
     *
     * @return The newly created credential object.
     * @param username The username of the credential.
     * @param password The password of the credential.
     * @throws GitException git errors
     */
    @Nonnull
    public static Credential userpassPlaintextNew(
            @Nonnull String username, @Nonnull String password) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniUserpassPlaintextNew(out._rawPtr, username, password));
        return out;
    }

    /**
     * Create a "default" credential usable for Negotiate mechanisms like NTLM or Kerberos
     * authentication.
     *
     * @return The newly created credential object.
     * @throws GitException git errors
     */
    @Nonnull
    public static Credential defaultNew() {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniDefaultNew(out._rawPtr));
        return out;
    }

    /**
     * Create a credential to specify a username.
     *
     * <p>This is used with ssh authentication to query for the username if none is specified in the
     * url.
     *
     * @return The newly created credential object.
     * @param username The username to authenticate with
     * @throws GitException git errors
     */
    @Nonnull
    public static Credential usernameNew(@Nonnull String username) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniUsernameNew(out._rawPtr, username));
        return out;
    }

    @Nonnull
    public static Credential sshKeyNew(
            @Nonnull String username,
            @Nullable String publickey,
            @Nonnull String privateKey,
            @Nullable String passphrase) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniSshKeyNew(out._rawPtr, username, publickey, privateKey, passphrase));
        return out;
    }

    @Nonnull
    public static Credential sshKeyMemoryNew(
            @Nonnull String username,
            @Nullable String publickey,
            @Nonnull String privateKey,
            @Nullable String passphrase) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(
                jniSshKeyMemoryNew(out._rawPtr, username, publickey, privateKey, passphrase));
        return out;
    }

    @Nonnull
    public static Credential fromAgent(@Nonnull String username) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniSshKeyFromAgent(out._rawPtr, username));
        return out;
    }

    static int userpass(
            AtomicLong out,
            @Nonnull String url,
            @Nonnull String userFromUrl,
            EnumSet<Type> allowedTypes,
            long payloadPtr) {
        return jniUserpass(out, url, userFromUrl, IBitEnum.bitOrAll(allowedTypes), payloadPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /** @return true if credential object contains non-null username. */
    public boolean hasUsername() {
        return jniHasUsername(getRawPointer()) == 1;
    }

    /** @return associated username */
    public String getUsername() {
        return jniGetUsername(getRawPointer());
    }

    public enum Type implements IBitEnum {
        /** A vanilla user/password request */
        USERPASS_PLAINTEXT(1 << 0),

        /** An SSH key-based authentication request */
        SSH_KEY(1 << 1),

        /** An SSH key-based authentication request, with a custom signature */
        SSH_CUSTOM(1 << 2),

        /** An NTLM/Negotiate-based authentication request. */
        DEFAULT(1 << 3),

        /** An SSH interactive authentication request */
        SSH_INTERACTIVE(1 << 4),

        /**
         * Username-only authentication request
         *
         * <p>Used as a pre-authentication step if the underlying transport (eg. SSH, with no
         * username in its URL) does not know which username to use.
         */
        USERNAME(1 << 5),

        /**
         * An SSH key-based authentication request
         *
         * <p>Allows credentials to be read from memory instead of files. Note that because of
         * differences in crypto backend support, it might not be functional.
         */
        SSH_MEMORY(1 << 6),
        ;
        private final int _bit;

        Type(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface AcquireCb {
        /**
         * Acquire credential call back
         *
         * @param url The resource for which we are demanding a credential.
         * @param usernameFromUrl The username that was embedded in a "user @ host" remote url, or
         *     NULL if not included.
         * @param allowedTypes A bitmask stating which credential types are OK to return.
         * @return Credential
         * @throws GitException exception if failed to create credential
         */
        @Nonnull
        Credential accept(@Nonnull String url, @Nullable String usernameFromUrl, int allowedTypes)
                throws GitException;
    }
}

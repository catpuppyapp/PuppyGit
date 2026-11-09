package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class Cred extends CAutoReleasable {
    /**
     * int git_cred_acquire_cb(git_cred **cred, const char *url, const char *username_from_url,
     * unsigned int allowed_types, void *payload);
     */
//    static native int jniAcquireCb( AtomicLong cred, String url, String usernameFromUrl, int allowedTypes);

    /** int git_cred_default_new(git_cred **out); */
    static native int jniDefaultNew(AtomicLong out);

    /** void git_cred_free(git_cred *cred); */
    static native void jniFree(long cred);

    /** int git_cred_has_username(git_cred *cred); */
    static native int jniHasUsername(long cred);

    /** int git_cred_ssh_key_from_agent(git_cred **out, const char *username); */
    static native int jniSshKeyFromAgent(AtomicLong out, String username);

    /**
     * int git_cred_ssh_key_memory_new(git_cred **out, const char *username, const char *publickey,
     * const char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyMemoryNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /**
     * int git_cred_ssh_key_new(git_cred **out, const char *username, const char *publickey, const
     * char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /** int git_cred_username_new(git_cred **cred, const char *username); */
    static native int jniUsernameNew(AtomicLong cred, String username);

    /**
     * int git_cred_userpass(git_cred **cred, const char *url, const char *user_from_url, unsigned
     * int allowed_types, void *payload);
     */
//    static native int jniUserpass( AtomicLong cred, String url, String userFromUrl, int allowedTypes);

    /**
     * int git_cred_userpass_plaintext_new(git_cred **out, const char *username, const char
     * *password);
     */
    static native int jniUserpassPlaintextNew(AtomicLong out, String username, String password);

    protected Cred(boolean isWeak, long rawPtr) {
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
    public static Cred userpassPlaintextNew(@Nonnull String username, @Nonnull String password) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniUserpassPlaintextNew(cred._rawPtr, username, password));
        return cred;
    }

    /**
     * Create a new passphrase-protected ssh key credential object. The supplied credential
     * parameter will be internally duplicated.
     *
     * @return The newly created credential object.
     * @param username username to use to authenticate
     * @param publickey The path to the public key of the credential.
     * @param privatekey The path to the private key of the credential.
     * @param passphrase The passphrase of the credential.
     * @throws GitException git errors
     */
    @Nonnull
    public static Cred sshKeyNew(
            @Nonnull String username,
            @Nonnull String publickey,
            @Nonnull String privatekey,
            @Nonnull String passphrase) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(
                jniSshKeyNew(cred._rawPtr, username, publickey, privatekey, passphrase));
        return cred;
    }

    /**
     * Create a new ssh key credential object used for querying an ssh-agent. The supplied
     * credential parameter will be internally duplicated.
     *
     * @return The newly created credential object.
     * @param username username to use to authenticate
     * @throws GitException git errors
     */
    @Nonnull
    public static Cred sshKeyFromAgent(@Nonnull String username) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniSshKeyFromAgent(cred._rawPtr, username));
        return cred;
    }

    /**
     * Create a "default" credential usable for Negotiate mechanisms like NTLM or Kerberos
     * authentication.
     *
     * @return 0 for success or an error code for failure
     */
    @Nonnull
    public static Cred defaultNew() {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniDefaultNew(cred._rawPtr));
        return cred;
    }

    /**
     * Create a credential to specify a username.
     *
     * <p>This is used with ssh authentication to query for the username if none is specified in the
     * url.
     */
    @Nonnull
    public static Cred usernameNew(@Nonnull String username) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniUsernameNew(cred._rawPtr, username));
        return cred;
    }

    /**
     * Create a new ssh key credential object reading the keys from memory.
     *
     * @return The newly created credential object.
     * @param username username to use to authenticate.
     * @param publickey The public key of the credential.
     * @param privatekey The private key of the credential.
     * @param passphrase The passphrase of the credential.
     * @throws GitException git errors
     */
    @Nonnull
    public static Cred sshKeyMemoryNew(
            @Nonnull String username,
            @Nonnull String publickey,
            @Nonnull String privatekey,
            @Nonnull String passphrase) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(
                jniSshKeyMemoryNew(cred._rawPtr, username, publickey, privatekey, passphrase));
        return cred;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
}

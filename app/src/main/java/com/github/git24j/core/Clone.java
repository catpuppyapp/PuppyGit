package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Clone {
    static native int jniClone(AtomicLong out, String url, String localPath, long options);

    static native void jniOptionsFree(long optionsPtr);

    /** int bare */
    static native int jniOptionsGetBare(long optionsPtr);

    /** const char* checkout_branch */
    static native String jniOptionsGetCheckoutBranch(long optionsPtr);
    static native void jniOptionsSetCheckoutBranch(long optionsPtr, String branch);

    /** git_checkout_options checkout_opts */
    static native long jniOptionsGetCheckoutOpts(long optionsPtr);

    /** git_fetch_options fetch_opts */
    static native long jniOptionsGetFetchOpts(long optionsPtr);

    /** git_clone_local_t local */
    static native int jniOptionsGetLocal(long optionsPtr);

    /** unsigned int version */
    static native int jniOptionsGetVersion(long optionsPtr);

    static native int jniOptionsNew(int version, AtomicLong outOpts);

    /** int bare */
    static native void jniOptionsSetBare(long optionsPtr, int bare);

    /** git_clone_local_t local */
    static native void jniOptionsSetLocal(long optionsPtr, int local);

    /** git_remote_create_cb remote_cb */
    static native void jniOptionsSetRemoteCb(long optionsPtr, Internals.ALSSCallback remoteCb);

    /** git_repository_create_cb repository_cb */
    static native void jniOptionsSetRepositoryCb(
            long optionsPtr, Internals.ASICallback repositoryCb);

    /** unsigned int version */
    static native void jniOptionsSetVersion(long optionsPtr, int version);

    /**
     * Clone a remote repository.
     *
     * <p>By default this creates its repository and initial remote to match git's defaults. You can
     * use the options in the callback to customize how these are created.
     *
     * @param url the remote repository to clone
     * @param localPath local directory to clone to
     * @param options configuration options for the clone. If NULL, the function works as though
     *     GIT_OPTIONS_INIT were passed.
     * @return cloned repository
     * @throws GitException git errors
     */
    @Nonnull
    public static Repository cloneRepo(
            @Nonnull String url, @Nonnull String localPath, @Nullable Options options) {
        Repository outRepo = new Repository(0);
        int e = jniClone(outRepo._rawPtr, url, localPath, options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
        return outRepo;
    }

    public enum LocalT implements IBitEnum {
        /**
         * Auto-detect (default), libgit2 will bypass the git-aware transport for local paths, but
         * use a normal fetch for `file://` urls.
         */
        LOCAL_AUTO(0),
        /** Bypass the git-aware transport even for a `file://` url. */
        LOCAL(1),
        /** Do no bypass the git-aware transport */
        NO_LOCAL(2),
        /** Bypass the git-aware transport, but do not try to use hardlinks. */
        LOCAL_NO_LINKS(3);
        private final int _bit;

        LocalT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface RepositoryCreateCb {
        /**
         * The signature of a function matchin git_repository_init, with an aditional void * as
         * callback payload.
         *
         * <p>Callers of git_clone my provide a function matching this signature to override the
         * repository creation and customization process during a clone operation.
         *
         * @param path path in which to create the repository
         * @param bare whether the repository is bare. This is the value from the clone options
         * @return created repository
         * @throws GitException indicate error
         */
        @Nonnull
        Repository accept(@Nonnull String path, boolean bare) throws GitException;
    }

    @FunctionalInterface
    public interface RemoteCreateCb {
        /**
         * The signature of a function matching git_remote_create, with an additional void* as a
         * callback payload.
         *
         * <p>Callers of git_clone may provide a function matching this signature to override the
         * remote creation and customization process during a clone operation.
         *
         * @param repo the repository in which to create the remote
         * @param name the remote's name
         * @param url the remote's url
         * @return resulting remote
         * @throws GitException git errors like GIT_EINVALIDSPEC, GIT_EEXISTS
         */
        @Nonnull
        Remote accept(@Nonnull Repository repo, @Nonnull String name, @Nonnull String url)
                throws GitException;
    }

    public static class Options extends CAutoReleasable {
        public static int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Options defaultOpts() {
            return create(VERSION);
        }

        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            jniOptionsNew(version, opts._rawPtr);
            return opts;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }

        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }

        /** @return reference to checkout options that can be used to set up checkout options */
        @Nonnull
        public Checkout.Options getCheckoutOpts() {
            return new Checkout.Options(true, jniOptionsGetCheckoutOpts(getRawPointer()));
        }

        /** @return reference to fetch options that can be used to set up fetch options */
        @Nonnull
        public FetchOptions getFetchOpts() {
            return new FetchOptions(true, jniOptionsGetFetchOpts(getRawPointer()));
        }

        public boolean getBare() {
            return jniOptionsGetBare(getRawPointer()) == 1;
        }

        public void setBare(boolean bare) {
            jniOptionsSetBare(getRawPointer(), bare ? 1 : 0);
        }

        @Nonnull
        public LocalT getLocal() {
            int r = jniOptionsGetLocal(getRawPointer());
            return IBitEnum.valueOf(r, LocalT.class, LocalT.LOCAL_AUTO);
        }

        public void setLocal(LocalT local) {
            jniOptionsSetLocal(getRawPointer(), local.getBit());
        }

        @CheckForNull
        public String getCheckoutBranch() {
            return jniOptionsGetCheckoutBranch(getRawPointer());
        }

        public void setCheckoutBranch(String branch) {
            jniOptionsSetCheckoutBranch(getRawPointer(), branch);
        }

        /**
         * Set a callback used to create the new repository into which to clone. If NULL, the 'bare'
         * field will be used to determine whether to create a bare repository.
         */
        public void setRepositoryCreateCb(@Nonnull RepositoryCreateCb createCb) {
            jniOptionsSetRepositoryCb(
                    getRawPointer(),
                    ((out, str, i) -> {
                        try {
                            Repository repo = createCb.accept(str, i == 1);
                            // release owner ship to options
                            out.set(repo._rawPtr.getAndSet(0));
                        } catch (GitException e) {
                            return e.getCode().getCode();
                        }
                        return 0;
                    }));
        }

        /**
         * A callback used to create the git_remote, prior to its being used to perform the clone
         * operation. See the documentation for git_remote_create_cb for details. This parameter may
         * be NULL, indicating that git_clone should provide default behavior.
         */
        public void setRemoteCreateCb(@Nonnull RemoteCreateCb createCb) {
            jniOptionsSetRemoteCb(
                    getRawPointer(),
                    (out, repoPtr, name, url) -> {
                        try {
                            Remote remote = createCb.accept(new Repository(repoPtr), name, url);
                            // we must release ownership of "remote" here, `git_remote_create_cb`
                            // is going to free it.
                            out.set(remote._rawPtr.getAndSet(0));
                        } catch (GitException e) {
                            return e.getCode().getCode();
                        }
                        return 0;
                    });
        }
    }
}

package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import static com.github.git24j.core.Remote.*;

public class FetchOptions extends CAutoReleasable {
    public static final int VERSION = 1;

    protected FetchOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Nonnull
    public static FetchOptions of(int version) {
        FetchOptions opts = new FetchOptions(false, 0);
        Error.throwIfNeeded(Remote.jniFetchOptionsNew(opts._rawPtr, version));
        return opts;
    }

    public static FetchOptions createDefault() {
        return FetchOptions.of(VERSION);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFetchOptionsFree(cPtr);
    }

    public int getVersion() {
        return jniFetchOptionsGetVersion(getRawPointer());
    }

    public void setVersion(int version) {
        jniFetchOptionsSetVersion(getRawPointer(), version);
    }

    public int getDepth() {
        return jniFetchOptionsGetDepth(getRawPointer());
    }
    public void setDepth(int depth) {
        jniFetchOptionsSetDepth(getRawPointer(), depth);
    }

    public RedirectT getFollowRedirects() {
        int r = jniFetchOptionsGetFollowRedirects(getRawPointer());
        return IBitEnum.valueOf(r, RedirectT.class);
    }

    public void setFollowRedirects(RedirectT redirectT) {
        jniFetchOptionsSetFollowRedirects(getRawPointer(), redirectT.getBit());
    }


    /** git_remote_callbacks callbacks */
    public Remote.Callbacks getCallbacks() {
        return new Remote.Callbacks(true, jniFetchOptionsGetCallbacks(getRawPointer()));
    }

    @Nonnull
    public PruneT getPrune() {
        int r = jniFetchOptionsGetPrune(getRawPointer());
        switch (r) {
            case 1:
                return PruneT.PRUNE;
            case 2:
                return PruneT.NO_PRUNE;
            default:
                return PruneT.UNSPECIFIED;
        }
    }

    public void setPrune(PruneT prune) {
        jniFetchOptionsSetPrune(getRawPointer(), prune.ordinal());
    }

    public boolean getUpdateFetchhead() {
        return 0 != jniFetchOptionsGetUpdateFetchhead(getRawPointer());
    }

    public void setUpdateFetchhead(boolean updateFetchhead) {
        jniFetchOptionsSetUpdateFetchhead(getRawPointer(), updateFetchhead ? 1 : 0);
    }

    @Nonnull
    public Remote.AutotagOptionT getDownloadTags() {
        int r = jniFetchOptionsGetDownloadTags(getRawPointer());
        switch (r) {
            case 1:
                return Remote.AutotagOptionT.AUTO;
            case 2:
                return Remote.AutotagOptionT.NONE;
            case 3:
                return Remote.AutotagOptionT.ALL;
            default:
                return Remote.AutotagOptionT.UNSPECIFIED;
        }
    }

    public void setDownloadTags(Remote.AutotagOptionT downloadTags) {
        jniFetchOptionsSetDownloadTags(getRawPointer(), downloadTags.getBit());
    }

    @Nonnull
    public Proxy.Options getProxyOpts() {
        long ptr = jniFetchOptionsGetProxyOpts(getRawPointer());
        return new Proxy.Options(true, ptr);
    }

    /** git_strarray custom_headers */
    @Nonnull
    public List<String> getCustomHeaders() {
        ArrayList<String> out = new ArrayList<>();
        jniFetchOptionsGetCustomHeaders(getRawPointer(), out);
        return out;
    }

    public void setCustomHeaders(@Nonnull String[] customHeaders) {
        jniFetchOptionsSetCustomHeaders(getRawPointer(), customHeaders);
    }

    public enum PruneT {
        /** Use the setting from the configuration */
        UNSPECIFIED,
        /** Force pruning on */
        PRUNE,
        /** Force pruning off */
        NO_PRUNE,
    }


    /** Constants for fetch depth (shallowness of fetch).
     *  `git_fetch_depth_t` in libgit2
     * */
    public static class DepthT{
        /** The fetch is "full" (not shallow). This is the default. */
        public static final int FULL = 0;  // libgit2: `GIT_FETCH_DEPTH_FULL`

        /** The fetch should "unshallow" and fetch missing data. */
        public static final int UNSHALLOW = 2147483647;  // libgit2: `GIT_FETCH_DEPTH_UNSHALLOW`
    }
}

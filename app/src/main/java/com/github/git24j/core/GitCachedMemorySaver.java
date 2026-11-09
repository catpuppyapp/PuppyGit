package com.github.git24j.core;

// used for git_libgit2_opts() function return value, when key is GIT_OPT_GET_CACHED_MEMORY
public class GitCachedMemorySaver {
    private long currentStorageValue;
    private long maxStorage;

    public long getCurrentStorageValue() {
        return currentStorageValue;
    }

    public void setCurrentStorageValue(long currentStorageValue) {
        this.currentStorageValue = currentStorageValue;
    }

    public long getMaxStorage() {
        return maxStorage;
    }

    public void setMaxStorage(long maxStorage) {
        this.maxStorage = maxStorage;
    }
}

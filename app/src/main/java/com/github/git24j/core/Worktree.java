package com.github.git24j.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Worktree {
    /**
     * int git_worktree_add(git_worktree **out, git_repository *repo, const char *name, const char
     * *path, const git_worktree_add_options *opts);
     */
    static native int jniAdd(AtomicLong out, long repoPtr, String name, String path, long opts);

    /** int git_worktree_add_init_options(git_worktree_add_options *opts, unsigned int version); */
    static native int jniAddInitOptions(long opts, int version);

    /** void git_worktree_free(git_worktree *wt); */
    static native void jniFree(long wt);

    /** int git_worktree_is_locked(git_buf *reason, const git_worktree *wt); */
    static native int jniIsLocked(Buf reason, long wt);

    /** int git_worktree_is_prunable(git_worktree *wt, git_worktree_prune_options *opts); */
    static native int jniIsPrunable(long wt, long opts);

    /** int git_worktree_list(git_strarray *out, git_repository *repo); */
    static native int jniList(List<String> out, long repoPtr);

    /** int git_worktree_lock(git_worktree *wt, const char *reason); */
    static native int jniLock(long wt, String reason);

    /** int git_worktree_lookup(git_worktree **out, git_repository *repo, const char *name); */
    static native int jniLookup(AtomicLong out, long repoPtr, String name);

    /** const char * git_worktree_name(const git_worktree *wt); */
    static native String jniName(long wt);

    /** int git_worktree_open_from_repository(git_worktree **out, git_repository *repo); */
    static native int jniOpenFromRepository(AtomicLong out, long repoPtr);

    /** const char * git_worktree_path(const git_worktree *wt); */
    static native String jniPath(long wt);

    /** int git_worktree_prune(git_worktree *wt, git_worktree_prune_options *opts); */
    static native int jniPrune(long wt, long opts);

    /**
     * int git_worktree_prune_init_options(git_worktree_prune_options *opts, unsigned int version);
     */
    static native int jniPruneInitOptions(long opts, int version);

    /** int git_worktree_unlock(git_worktree *wt); */
    static native int jniUnlock(long wt);

    /** int git_worktree_validate(const git_worktree *wt); */
    static native int jniValidate(long wt);
}

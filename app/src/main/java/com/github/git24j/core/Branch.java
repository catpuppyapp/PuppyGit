package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Branch {
    static native int jniCreate(
            AtomicLong outRef, long repoPtr, String branchName, long targetPtr, int force);

    static native int jniCreateFromAnnotated(
            AtomicLong outRef, long repoPtr, String branchName, long annoCommitPtr, int force);

    static native int jniDelete(long refPtr);

    static native int jniIsCheckedOut(long refPtr);

    static native int jniIsHead(long refPtr);

    static native void jniIteratorFree(long branchIterPtr);

    static native int jniIteratorNew(AtomicLong outBranchIter, long repoPtr, int listFlags);

    static native int jniLookup(AtomicLong outRef, long repoPtr, String branchName, int branchType);

    static native int jniMove(AtomicLong outRef, long branchPtr, String branchName, int force);

    static native int jniName(AtomicReference<String> outStr, long refPtr);

    static native int jniNext(AtomicLong outRef, AtomicInteger outType, long branchIterPtr);

    static native int jniRemoteName(Buf outBuf, long repoPtr, String canonicalBranchName);

    static native int jniSetUpstream(long refPtr, String upstreamName);

    static native int jniUpstream(AtomicLong outRef, long branchPtr);

    static native int jniUpstreamName(Buf outBuf, long repoPtr, String refName);

    static native int jniUpstreamRemote(Buf outBuf, long repoPtr, String refName);

    /**
     * Create a new branch pointing at a target commit.
     *
     * @param repo Repository
     * @param branchName Name for the branch; this name is validated for consistency. It should also
     *     not conflict with an already existing branch name.
     * @param target Commit to which this branch should point. This object must belong to the given
     *     `repo`.
     * @param force Overwrite existing branch.
     * @return git reference that must be closed by the caller.
     * @throws GitException git error.
     * @throws IllegalStateException if repository has already been closed.
     */
    public static Reference create(
            @Nonnull Repository repo,
            @Nonnull String branchName,
            @Nonnull Commit target,
            boolean force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(
                        outRef,
                        repo.getRawPointer(),
                        branchName,
                        target.getRawPointer(),
                        force ? 1 : 0));
        return new Reference(false, outRef.get());
    }

    /**
     * Create a new branch pointing at a target commit
     *
     * <p>This behaves like `git_branch_create()` but takes an annotated commit, which lets you
     * specify which extended sha syntax string was specified by a user, allowing for more exact
     * reflog messages.
     *
     * <p>See the documentation for `git_branch_create()`.
     *
     * @param repo repository from which the branch is created
     * @param branchName name of the branch to be created
     * @param annotatedCommit annotated commit the branch is going to point to
     * @param force override existing branch
     * @return reference of the created branch
     * @throws GitException
     */
    public static Reference createFromAnnotated(
            Repository repo, String branchName, AnnotatedCommit annotatedCommit, boolean force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreateFromAnnotated(
                        outRef,
                        repo.getRawPointer(),
                        branchName,
                        annotatedCommit.getRawPointer(),
                        force ? 1 : 0));
        return new Reference(false, outRef.get());
    }

    /**
     * Delete a branch and free corresponding branch reference.
     *
     * @param branch
     * @throws GitException git error
     */
    public static void delete(Reference branch) {
        Error.throwIfNeeded(jniDelete(branch._rawPtr.getAndSet(0)));
    }

    /**
     * Move/rename an existing local branch reference.
     *
     * <p>The new branch name will be checked for validity. See `git_tag_create()` for rules about
     * valid names.
     *
     * <p>Note that if the move succeeds, the old reference object will not + be valid anymore, and
     * should be freed immediately by the user using + `git_reference_free()`.
     *
     * @param branch Current underlying reference of the branch.
     * @param branchName Target name of the branch once the move is performed; this name is
     *     validated for consistency.
     * @param force Overwrite existing branch.
     * @return New reference object for the updated name.0 on success,
     * @throws GitException git errors
     */
    public static Reference move(Reference branch, String branchName, boolean force) {
        Reference outRef = new Reference(true, 0);
        Error.throwIfNeeded(
                jniMove(outRef._rawPtr, branch.getRawPointer(), branchName, force ? 1 : 0));
        return outRef;
    }

    /**
     * Lookup a branch by its name in a repository.
     *
     * <p>The generated reference must be freed by the user. The branch name will be checked for
     * validity.
     *
     * @param repo the repository to look up the branch
     * @param branchName Name of the branch to be looked-up; this name is validated for consistency.
     * @param branchType Type of the considered branch. This should be valued with either
     *     GIT_BRANCH_LOCAL or GIT_BRANCH_REMOTE.
     * @return pointer to the looked-up branch reference or null when no matching branch exists.
     * @throws GitException GIT_EINVALIDSPEC, otherwise an error code.
     */
    public static Reference lookup(Repository repo, String branchName, BranchType branchType) {
        AtomicLong outRef = new AtomicLong();
        int e = jniLookup(outRef, repo.getRawPointer(), branchName, branchType.ordinal());
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }

    /**
     * Get the branch name
     *
     * <p>Given a reference object, this will check that it really is a branch (ie. it lives under
     * "refs/heads/" or "refs/remotes/"), and return the branch part of it.
     *
     * @param branch A reference object, ideally pointing to a branch
     * @return reference name.
     * @throws GitException git errors
     */
    public static String name(Reference branch) {
        if (branch == null) {
            return null;
        }
        AtomicReference<String> outStr = new AtomicReference<>();
        Error.throwIfNeeded(jniName(outStr, branch.getRawPointer()));
        return outStr.get();
    }

    /**
     * Get the upstream of a branch
     *
     * <p>Given a reference, this will return a new reference object corresponding to its remote
     * tracking branch. The reference must be a local branch.
     *
     * @param branch Current underlying reference of the branch.
     * @return the retrieved reference, or null when no remote tracking reference exists
     * @throws GitException
     */
    public static Reference upstream(Reference branch) {
        if (branch == null) {
            return null;
        }
        AtomicLong outRef = new AtomicLong();
        int e = jniUpstream(outRef, branch.getRawPointer());
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }

    /**
     * Set a branch's upstream branch
     *
     * <p>This will update the configuration to set the branch named `name` as the upstream of
     * `branch`. Pass a NULL name to unset the upstream information.
     *
     * @note the actual tracking reference must have been already created for the operation to
     *     succeed.
     * @param branch the branch to configure
     * @param upstreamName remote-tracking or local branch to set as upstream.
     * @return 0 on success; GIT_ENOTFOUND if there's no branch named `branch_name` or an error code
     */
    public static void setUpstream(@Nonnull Reference branch, @Nullable String upstreamName) {
        Error.throwIfNeeded(jniSetUpstream(branch.getRawPointer(), upstreamName));
    }

    /**
     * Get the upstream name of a branch
     *
     * <p>Given a local branch, this will return its remote-tracking branch information, as a full
     * reference name, ie. "feature/nice" would become "refs/remote/origin/feature/nice", depending
     * on that branch's configuration.
     *
     * @param repo the repository where the branches live.
     * @param refname reference name of the local branch.
     * @return remote branch name or null remote tracking reference exists.
     * @throws GitException git errors
     */
    public static String upstreamName(Repository repo, String refname) {
        Buf outBuf = new Buf();
        int e = jniUpstreamName(outBuf, repo.getRawPointer(), refname);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outBuf.toString();
    }

    /**
     * Determine if HEAD points to the given branch
     *
     * @param branch A reference to a local branch.
     * @return true if HEAD points at the branch, false if it isn't
     * @throws GitException git errors
     */
    public static boolean isHead(Reference branch) {
        if (branch == null) {
            return false;
        }
        int e = jniIsHead(branch.getRawPointer());
        Error.throwIfNeeded(e);
        return e == 1;
    }

    /**
     * Determine if any HEAD points to the current branch
     *
     * <p>This will iterate over all known linked repositories (usually in the form of worktrees)
     * and report whether any HEAD is pointing at the current branch.
     *
     * @param branch A reference to a local branch.
     * @return true if branch is checked out, 0 if it isn't,
     * @throws GitException git error
     */
    public static boolean isCheckedOut(Reference branch) {
        int e = jniIsCheckedOut(branch.getRawPointer());
        Error.throwIfNeeded(e);
        return e == 1;
    }

    /**
     * Find the remote name of a remote-tracking branch
     *
     * <p>This will return the name of the remote whose fetch refspec is matching the given branch.
     * E.g. given a branch "refs/remotes/test/master", it will extract the "test" part. If refspecs
     * from multiple remotes match, the function will return GIT_EAMBIGUOUS.
     *
     * @param repo The repository where the branch lives.
     * @param canonicalBranchName complete name of the remote tracking branch.
     * @return remote name or null when no matching remote was found,
     * @throws GitException git errors
     */
    public static String remoteName(Repository repo, String canonicalBranchName) {
        if (repo == null) {
            return null;
        }
        Buf outBuf = new Buf();
        int e = jniRemoteName(outBuf, repo.getRawPointer(), canonicalBranchName);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outBuf.toString();
    }

    /**
     * Retrieve the upstream remote of a local branch
     *
     * <p>This will return the currently configured "branch.*.remote" for a given branch. This
     * branch must be local.
     *
     * @param repo the repository in which to look
     * @param refName the full name of the branch
     * @return name of the upstream remote
     * @throws GitException git errors
     */
    public static String upstreamRemote(Repository repo, String refName) {
        Buf outBuf = new Buf();
        int e = jniUpstreamRemote(outBuf, repo.getRawPointer(), refName);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        return outBuf.toString();
    }

    public enum BranchType implements IBitEnum {
        INVALID(0),
        LOCAL(1),
        REMOTE(2),
        ALL(3);

        private final int _bit;

        BranchType(int bit) {
            _bit = bit;
        }

        public static BranchType valueOf(int iVal) {
            return IBitEnum.valueOf(iVal, BranchType.class, INVALID);
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class Iterator {
        private final AtomicLong _ptr = new AtomicLong();

        Iterator(long rawPointer) {
            _ptr.set(rawPointer);
        }

        /**
         * Create an iterator which loops over the requested branches.
         *
         * @param repo Repository where to find the branches.
         * @param flag Filtering flags for the branch listing. Valid values are GIT_BRANCH_LOCAL,
         *     GIT_BRANCH_REMOTE or GIT_BRANCH_ALL.
         * @return the iterator
         * @throws GitException git errors
         */
        public static Iterator create(Repository repo, BranchType flag) {
            AtomicLong outRef = new AtomicLong();
            Error.throwIfNeeded(jniIteratorNew(outRef, repo.getRawPointer(), flag.ordinal()));
            return new Iterator(outRef.get());
        }

        @Override
        protected void finalize() throws Throwable {
            if (_ptr.get() != 0) {
                jniIteratorFree(_ptr.get());
            }
            super.finalize();
        }

        /**
         * Retrieve the next branch from the iterator
         *
         * @return the reference and the type, null if there are no more branches
         * @throws GitException git errors.
         */
        public Map.Entry<Reference, BranchType> next() {
            AtomicLong outRef = new AtomicLong();
            AtomicInteger outType = new AtomicInteger();
            int e = jniNext(outRef, outType, _ptr.get());
            if (e == GitException.ErrorCode.ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            Reference ref = new Reference(true, outRef.get());
            BranchType type = BranchType.valueOf(outType.get());
            return new HashMap.SimpleImmutableEntry<>(ref, type);
        }
    }
}

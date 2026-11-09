package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.git24j.core.GitException.ErrorCode.ITEROVER;
import static com.github.git24j.core.Internals.BArrCallback;

public class Revwalk extends CAutoReleasable {
    /**
     * int git_revwalk_add_hide_cb(git_revwalk *walk, git_revwalk_hide_cb hide_cb, void *payload);
     */
    static native int jniAddHideCb(long walk, BArrCallback hideCb, AtomicLong outPayload);

    /** void git_revwalk_free(git_revwalk *walk); */
    static native void jniFree(long walk);

    static native void jniFreeHideCb(long payloadPtr);

    /** int git_revwalk_hide(git_revwalk *walk, const git_oid *commit_id); */
    static native int jniHide(long walk, Oid commitId);

    /** int git_revwalk_hide_glob(git_revwalk *walk, const char *glob); */
    static native int jniHideGlob(long walk, String glob);

    /** int git_revwalk_hide_head(git_revwalk *walk); */
    static native int jniHideHead(long walk);

    /** int git_revwalk_hide_ref(git_revwalk *walk, const char *refname); */
    static native int jniHideRef(long walk, String refname);

    /** int git_revwalk_new(git_revwalk **out, git_repository *repo); */
    static native int jniNew(AtomicLong out, long repoPtr);

    /** int git_revwalk_next(git_oid *out, git_revwalk *walk); */
    static native int jniNext(Oid out, long walk);

    /** int git_revwalk_push(git_revwalk *walk, const git_oid *id); */
    static native int jniPush(long walk, Oid id);

    /** int git_revwalk_push_glob(git_revwalk *walk, const char *glob); */
    static native int jniPushGlob(long walk, String glob);

    /** int git_revwalk_push_head(git_revwalk *walk); */
    static native int jniPushHead(long walk);

    /** int git_revwalk_push_range(git_revwalk *walk, const char *range); */
    static native int jniPushRange(long walk, String range);

    /** int git_revwalk_push_ref(git_revwalk *walk, const char *refname); */
    static native int jniPushRef(long walk, String refname);

    /** git_repository * git_revwalk_repository(git_revwalk *walk); */
    static native long jniRepository(long walk);

    /** void git_revwalk_reset(git_revwalk *walker); */
    static native void jniReset(long walker);

    /** void git_revwalk_simplify_first_parent(git_revwalk *walk); */
    static native void jniSimplifyFirstParent(long walk);

    /** void git_revwalk_sorting(git_revwalk *walk, unsigned int sort_mode); */
    static native void jniSorting(long walk, int sortMode);

    protected Revwalk(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Allocate a new revision walker to iterate through a repo.
     *
     * <p>This revision walker uses a custom memory pool and an internal commit cache, so it is
     * relatively expensive to allocate.
     *
     * <p>For maximum performance, this revision walker should be reused for different walks.
     *
     * <p>This revision walker is *not* thread safe: it may only be used to walk a repository on a
     * single thread; however, it is possible to have several revision walkers in several different
     * threads walking the same repository.
     *
     * @param repo the repo to walk through
     * @return the new revision walker
     * @throws GitException git errors
     */
    @Nonnull
    public static Revwalk create(@Nonnull Repository repo) {
        Revwalk out = new Revwalk(true, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr, repo.getRawPointer()));
        return out;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Mark a commit (and its ancestors) uninteresting for the output.
     *
     * <p>The given id must belong to a committish on the walked repository.
     *
     * <p>The resolved commit and all its parents will be hidden from the output on the revision
     * walk.
     *
     * @param commitId the oid of commit that will be ignored during the traversal
     * @throws GitException git errors
     */
    public void hide(@Nonnull Oid commitId) {
        Error.throwIfNeeded(jniHide(getRawPointer(), commitId));
    }

    /**
     * Hide matching references.
     *
     * <p>The OIDs pointed to by the references that match the given glob pattern and their
     * ancestors will be hidden from the output on the revision walk.
     *
     * <p>A leading 'refs/' is implied if not present as well as a trailing '/\*' if the glob lacks
     * '?', '\*' or '['.
     *
     * <p>Any references matching this glob which do not point to a committish will be ignored.
     *
     * @param glob the glob pattern references should match
     * @throws GitException git errors
     */
    public void hideGlob(@Nonnull String glob) {
        Error.throwIfNeeded(jniHideGlob(getRawPointer(), glob));
    }

    /**
     * Hide the repository's HEAD
     *
     * @throws GitException git errors
     */
    public void hideHead() {
        Error.throwIfNeeded(jniHideHead(getRawPointer()));
    }

    /**
     * Push the OID pointed to by a reference
     *
     * <p>The reference must point to a committish.
     *
     * @param refname the reference to push
     * @throws GitException git errors
     */
    public void hideRef(@Nonnull String refname) {
        Error.throwIfNeeded(jniHideRef(getRawPointer(), refname));
    }

    /**
     * Get the next commit from the revision walk.
     *
     * <p>The initial call to this method is *not* blocking when iterating through a repo with a
     * time-sorting mode.
     *
     * <p>Iterating with Topological or inverted modes makes the initial call blocking to preprocess
     * the commit list, but this block should be mostly unnoticeable on most repositories
     * (topological preprocessing times at 0.3s on the git.git repo).
     *
     * <p>The revision walker is reset when the walk is over.
     *
     * @return next commit, null if there are no commits left to iterate
     * @throws GitException git errors
     */
    @CheckForNull
    public Oid next() {
        Oid oid = new Oid();
        int r = jniNext(oid, getRawPointer());
        if (ITEROVER.getCode() == r) {
            return null;
        }
        Error.throwIfNeeded(r);
        return oid;
    }

    /**
     * Add a new root for the traversal
     *
     * <p>The pushed commit will be marked as one of the roots from which to start the walk. This
     * commit may not be walked if it or a child is hidden.
     *
     * <p>At least one commit must be pushed onto the walker before a walk can be started.
     *
     * <p>The given id must belong to a committish on the walked repository.
     *
     * @param id the oid of the commit to start from.
     * @throws GitException git errors
     */
    public void push(@Nonnull Oid id) {
        Error.throwIfNeeded(jniPush(getRawPointer(), id));
    }

    /**
     * Push matching references
     *
     * <p>The OIDs pointed to by the references that match the given glob pattern will be pushed to
     * the revision walker.
     *
     * <p>A leading 'refs/' is implied if not present as well as a trailing '/\*' if the glob lacks
     * '?', '\*' or '['.
     *
     * <p>Any references matching this glob which do not point to a committish will be ignored.
     *
     * @param glob the glob pattern references should match
     * @throws GitException git errors
     */
    public void pushGlob(@Nonnull String glob) {
        Error.throwIfNeeded(jniPushGlob(getRawPointer(), glob));
    }

    /**
     * Push the repository's HEAD
     *
     * @throws GitException git errors
     */
    public void pushHead() {
        Error.throwIfNeeded(jniPushHead(getRawPointer()));
    }

    /**
     * Push and hide the respective endpoints of the given range.
     *
     * <p>The range should be of the form <commit>..<commit> where each <commit> is in the form
     * accepted by 'git_revparse_single'. The left-hand commit will be hidden and the right-hand
     * commit pushed.
     *
     * @param range the range
     * @throws GitException git errors
     */
    public void pushRange(@Nonnull String range) {
        Error.throwIfNeeded(jniPushRange(getRawPointer(), range));
    }

    /**
     * Push the OID pointed to by a reference
     *
     * <p>The reference must point to a committish.
     *
     * @param refname the reference to push
     * @throws GitException git errors
     */
    public void pushRef(@Nonnull String refname) {
        Error.throwIfNeeded(jniPushRef(getRawPointer(), refname));
    }

    /**
     * Return the repository on which this walker is operating.
     *
     * @return the repository being walked
     */
    @CheckForNull
    public Repository repository() {
        long ptr = jniRepository(getRawPointer());
        if (ptr <= 0) {
            return null;
        }
        return new Repository(ptr);
    }

    /**
     * Reset the revision walker for reuse.
     *
     * <p>This will clear all the pushed and hidden commits, and leave the walker in a blank state
     * (just like at creation) ready to receive new commit pushes and start a new walk.
     *
     * <p>The revision walk is automatically reset when a walk is over.
     */
    public void reset() {
        jniReset(getRawPointer());
    }

    /**
     * Simplify the history by first-parent
     *
     * <p>No parents other than the first for each commit will be enqueued.
     */
    public void simplifyFirstParent() {
        jniSimplifyFirstParent(getRawPointer());
    }

    /**
     * Change the sorting mode when iterating through the repository's contents.
     *
     * <p>Changing the sorting mode resets the walker.
     *
     * @param sortMode combination of GIT_SORT_XXX flags, null defaults to SortT.NONE
     */
    public void sorting(@Nullable EnumSet<SortT> sortMode) {
        jniSorting(getRawPointer(), IBitEnum.bitOrAll(sortMode));
    }

    /**
     * Add or remove existing callback.
     *
     * @param callback
     */
    public void addHideCb(@Nullable HideCb callback) {
        HideCbAdapter adapter = callback == null ? null : new HideCbAdapter(false, 0, callback);
        Error.throwIfNeeded(
                jniAddHideCb(getRawPointer(), adapter, callback == null ? null : adapter._rawPtr));
    }

    /**
     * This is a callback function that user can provide to hide a commit and its parents. If the
     * callback function returns non-zero value, then this commit and its parents will be hidden.
     */
    @FunctionalInterface
    public interface HideCb {
        int accept(Oid oid);
    }

    public static class HideCbAdapter extends CAutoReleasable implements BArrCallback {
        private final HideCb _hideCb;

        protected HideCbAdapter(boolean isWeak, long rawPtr, HideCb hideCb) {
            super(isWeak, rawPtr);
            _hideCb = hideCb;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFreeHideCb(cPtr);
        }

        @Override
        public int accept(byte[] rawid) {
            return _hideCb.accept(Oid.of(rawid));
        }
    }
}

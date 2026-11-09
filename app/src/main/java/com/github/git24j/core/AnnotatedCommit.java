package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An annotated commit contains information about how it was looked up, which may be useful for
 * functions like merge or rebase to provide context to the operation. For example, conflict files
 * will include the name of the source or target branches being merged. It is therefore preferable
 * to use the most specific function (eg `git_annotated_commit_from_ref`) instead of this one when
 * that data is known.
 */
public class AnnotatedCommit extends CAutoReleasable {
    static native void jniFree(long acPtr);

    static native int jniFromFetchHead(
            AtomicLong outAc, long repoPtr, String branchName, String remoteUrl, Oid oid);

    static native int jniFromRef(AtomicLong outAc, long repoPtr, long refPtr);

    static native int jniFromRevspec(AtomicLong outAc, long repoPtr, String revspec);

    static native byte[] jniId(long acPtr);

    static native int jniLookup(AtomicLong outAc, long repoPtr, Oid oid);

    static native String jniRef(long acPtr);

    protected AnnotatedCommit(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Initialize {@link AnnotatedCommit} from a {@link Reference} of a repository.
     *
     * @param repo git repository
     * @param ref git reference
     * @return AnnotatedCommit
     * @throws GitException git error
     */
    public static AnnotatedCommit fromRef(Repository repo, Reference ref) {
        // FIXME: api doc suggests `fromRef` returns a strong ref, but poractice suggests otherwise
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(jniFromRef(commit._rawPtr, repo.getRawPointer(), ref.getRawPointer()));
        return commit;
    }

    /**
     * Initialize {@link AnnotatedCommit} from FETCH_HEAD data
     *
     * @param repo git repository
     * @param branchName branch name, nonnull
     * @param remoteUrl remote url, nonnull
     * @param oid oid, must not null
     * @return {@link AnnotatedCommit}
     * @throws GitException git error
     */
    @Nonnull
    public static AnnotatedCommit fromFetchHead(
            @Nonnull Repository repo,
            @Nonnull String branchName,
            @Nonnull String remoteUrl,
            @Nonnull Oid oid) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(
                jniFromFetchHead(commit._rawPtr, repo.getRawPointer(), branchName, remoteUrl, oid));
        return commit;
    }

    /**
     * Initialize {@link AnnotatedCommit} from Oid
     *
     * @param repo
     * @param oid
     * @return
     */
    @Nonnull
    public static AnnotatedCommit lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(jniLookup(commit._rawPtr, repo.getRawPointer(), oid));
        return commit;
    }

    /**
     * Creates a `git_annotated_comit` from a revision string.
     *
     * <p>See `man gitrevisions`, or
     * http://git-scm.com/docs/git-rev-parse.html#_specifying_revisions for information on the
     * syntax accepted.
     *
     * @param repo repository that contains the given commit
     * @param revspec the extended sha syntax string to use to lookup the commit
     * @return annotated commit
     * @throws GitException git errors
     */
    @Nonnull
    public static AnnotatedCommit fromRevspec(@Nonnull Repository repo, @Nonnull String revspec) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(jniFromRevspec(commit._rawPtr, repo.getRawPointer(), revspec));
        return commit;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /** @return the commit ID that the given `git_annotated_commit` refers to. */
    @Nonnull
    public Oid id() {
        return Oid.of(jniId(getRawPointer()));
    }

    @Nonnull
    public String ref() {
        return jniRef(getRawPointer());
    }
}

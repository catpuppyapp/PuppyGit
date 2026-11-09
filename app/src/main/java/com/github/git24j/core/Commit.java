package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Commit extends GitObject {

    static native int jniAmend(
            Oid outOid,
            long commitToAmend,
            String updateRef,
            long author,
            long committer,
            String messageEncoding,
            String message,
            long treePtr);

    static native long jniAuthor(long commitPtr);

    /**
     * int git_commit_author_with_mailmap(git_signature **out, const git_commit *commit, const
     * git_mailmap *mailmap);
     */
    static native int jniAuthorWithMailmap(AtomicLong out, long commit, long mailmap);

    static native String jniBody(long commitPtr);

    static native long jniCommitter(long commitPtr);

    /**
     * int git_commit_committer_with_mailmap(git_signature **out, const git_commit *commit, const
     * git_mailmap *mailmap);
     */
    static native int jniCommitterWithMailmap(AtomicLong out, long commit, long mailmap);

    static native int jniCreate(
            Oid outOid,
            long repoPtr,
            String updateRef,
            long author,
            long commiter,
            String msgEncoding,
            String message,
            long treePtr,
            long[] parents);

    static native int jniCreateBuffer(
            Buf outBuf,
            long repoPtr,
            long author,
            long committer,
            String messageEncoding,
            String message,
            long treePtr,
            int parentCnt,
            long[] parents);

    static native int jniCreateWithSignature(
            Oid oid, long repoPtr, String commitContent, String signature, String signatureField);

    static native int jniHeaderField(Buf outBuf, long commitPtr, String field);

    static native String jniMessage(long commitPtr);

    static native String jniMessageEncoding(long commitPtr);

    static native String jniMessageRaw(long commitPtr);

    static native int jniNthGenAncestor(AtomicLong outPtr, long commitPtr, int n);

    static native int jniParent(AtomicLong outPtr, long commitPtr, int n);

    static native int jniParentCount(long commitPtr);

    static native byte[] jniParentId(long commitPtr, int n);

    static native String jniRawHeader(long commitPtr);

    static native String jniSummary(long commitPtr);

    static native long jniTime(long commitPtr);

    static native int jniTimeOffset(long commitPtr);

    static native int jniTree(AtomicLong outTreePtr, long commitPtr);

    static native byte[] jniTreeId(long commitPtr);

    Commit(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }

    /**
     * Lookup a commit object from a repository.
     *
     * @param repo the repo to use when locating the commit.
     * @param oid identity of the commit to locate. If the object is an annotated tag it will be
     *     peeled back to the commit.
     * @return Found commit
     * @throws GitException git error
     * @throws IllegalStateException required objects are not open or have been closed.
     */
    public static Commit lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        return (Commit) GitObject.lookup(repo, oid, Type.COMMIT);
    }

    /**
     * Lookup a commit object from a repository, given a prefix of its identifier (short id).
     *
     * <p>The returned object should be released with `git_commit_free` when no longer needed.
     *
     * @param repo the repo to use when locating the commit.
     * @param shortOid of the commit to locate. If the object is an annotated tag it will be peeled
     *     back to the commit.
     * @return found commit
     * @throws GitException git errors
     */
    public static Commit lookupPrefix(@Nonnull Repository repo, @Nonnull String shortOid) {
        return (Commit) GitObject.lookupPrefix(repo, shortOid, Type.COMMIT);
    }

    /**
     * Create new commit in the repository. See also {@code git-commit-tree}.
     *
     * <p>A git commit encapsulates `tree`, `parents`, `author`, `committer` and `message`. To see
     * all fields of a commit:
     *
     * <pre>{@code
     * $ git cat-file -p 7dcb276
     * > tree f3d69a07e30b377a722c84d654415e36290c3f5f
     * > parent f80e0b10f83e512d1fae0142d000cceba3aca721
     * > parent e5b28427ba064002e0e343e783ea3095018ce72c
     * > author Shijing Lu <shijing.lu@gmail.com> 1569091521 -0400
     * > committer Shijing Lu <shijing.lu@gmail.com> 1569091521 -0400
     * >
     * > Merge branch 'feature/dev'
     * }</pre>
     *
     * Therefore, to create a commit, all necessary must be provided.
     *
     * @param repo Repository where to store the commit
     * @param updateRef If not NULL, name of the reference that will be updated to point to this
     *     commit. If the reference is not direct, it will be resolved to a direct reference. Use
     *     "HEAD" to update the HEAD of the current branch and make it point to this commit. If the
     *     reference doesn't exist yet, it will be created. If it does exist, the first parent must
     *     be the tip of this branch.
     * @param author Signature with author and author time of commit
     * @param committer Signature with committer and * commit time of commit
     * @param messageEncoding The encoding for the message in the commit, represented with a
     *     standard encoding name. E.g. "UTF-8". If NULL, no encoding header is written and UTF-8 is
     *     assumed.
     * @param message Full message for this commit
     * @param tree An instance of a `git_tree` object that will be used as the tree for the commit.
     *     This tree object must also be owned by the given `repo`.
     * @param parents List commits that will be used as the parents for this commit. This array may
     *     be null creating root commit. All the given commits must be owned by the `repo`.
     * @return oid of the created commit
     * @throws GitException git errors The created commit will be written to the Object Database and
     *     the given reference will be updated to point to it
     */
    public static Oid create(
            @Nonnull Repository repo,
            @Nullable String updateRef,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nullable String messageEncoding,
            @Nonnull String message,
            @Nonnull Tree tree,
            @Nonnull List<Commit> parents) {
        Oid outOid = new Oid();
        long[] parentsArray =
                parents.stream().map(Commit::getRawPointer).mapToLong(Long::longValue).toArray();
        int e =
                jniCreate(
                        outOid,
                        repo.getRawPointer(),
                        updateRef,
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        messageEncoding,
                        message,
                        tree.getRawPointer(),
                        parentsArray);
        Error.throwIfNeeded(e);
        return outOid;
    }

    /**
     * Amend an existing commit by replacing only non-NULL values.
     *
     * <p>This creates a new commit that is exactly the same as the old commit, except that any
     * non-NULL values will be updated. The new commit has the same parents as the old commit.
     *
     * <p>The `updateRef` value works as in the regular `Commit::create()`, updating the ref to
     * point to the newly rewritten commit. If you want to amend a commit that is not currently the
     * tip of the branch and then rewrite the following commits to reach a ref, pass this as NULL
     * and update the rest of the commit chain and ref separately.
     *
     * <p>Unlike `Commit::create()`, the `author`, `committer`, `message`, `messageEncoding`, and
     * `tree` parameters can be NULL in which case this will use the values from the original
     * `commitToAmend`.
     *
     * <p>All parameters have the same meanings as in `Commit::create()`.
     *
     * @see Commit::create
     * @param commitToAmend non-null commit to be amended
     * @param updateRef nullable
     * @param author nullable
     * @param committer nullable
     * @param messageEncoding nullable
     * @param message nullable
     * @param tree nullable
     * @return oid after amending
     * @throws GitException git errors
     */
    public static Oid amend(
            Commit commitToAmend,
            @Nullable String updateRef,
            @Nullable Signature author,
            @Nullable Signature committer,
            @Nullable String messageEncoding,
            @Nullable String message,
            @Nullable Tree tree) {
        Oid outOid = new Oid();
        int e =
                jniAmend(
                        outOid,
                        commitToAmend.getRawPointer(),
                        updateRef,
                        author == null ? 0 : author.getRawPointer(),
                        committer == null ? 0 : committer.getRawPointer(),
                        messageEncoding,
                        message,
                        tree == null ? 0 : tree.getRawPointer());
        Error.throwIfNeeded(e);
        return outOid;
    }

    /**
     * Create a commit and write it into a buffer
     *
     * <p>Create a commit as with `Commit::create()` but instead of writing it to the objectdb,
     * write the contents of the object into a buffer.
     *
     * @param repo Repository where the referenced tree and parents live
     * @param author Signature with author and author time of commit
     * @param committer Signature with committer and * commit time of commit
     * @param messageEncoding The encoding for the message in the commit, represented with a
     *     standard encoding name. E.g. "UTF-8". If NULL, no encoding header is written and UTF-8 is
     *     assumed.
     * @param message Full message for this commit
     * @param tree An instance of a `git_tree` object that will be used as the tree for the commit.
     *     This tree object must also be owned by the given `repo`.
     * @param parents Array of `parent_count` pointers to `git_commit` objects that will be used as
     *     the parents for this commit. All the given commits must be owned by the `repo`.
     * @return the buffer into which to write the commit object content
     * @throws GitException git errors
     */
    public static Buf createBuffer(
            @Nonnull Repository repo,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nullable String messageEncoding,
            @Nonnull String message,
            @Nonnull Tree tree,
            @Nonnull List<Commit> parents) {
        Buf outBuf = new Buf();
        long[] parentsArray =
                parents.stream().map(Commit::getRawPointer).mapToLong(Long::longValue).toArray();
        int e =
                jniCreateBuffer(
                        outBuf,
                        repo.getRawPointer(),
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        messageEncoding,
                        message,
                        tree.getRawPointer(),
                        parents.size(),
                        parentsArray);
        Error.throwIfNeeded(e);
        return outBuf;
    }

    /**
     * Create a commit object from the given buffer and signature
     *
     * <p>Given the unsigned commit object's contents, its signature and the header field in which
     * to store the signature, attach the signature to the commit and write it into the given
     * repository.
     *
     * @param commitContent the content of the unsigned commit object
     * @param signature the signature to add to the commit. Leave `NULL` to create a commit without
     *     adding a signature field.
     * @param signatureField which header field should contain this signature. Leave `NULL` for the
     *     default of "gpgsig"
     * @return the resulting commit id
     * @throws GitException git errors
     */
    public static Oid createWithSignature(
            Repository repo, String commitContent, String signature, String signatureField) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(
                jniCreateWithSignature(
                        outOid, repo.getRawPointer(), commitContent, signature, signatureField));
        return outOid;
    }

    /**
     * Get the encoding for the message of a commit, as a string representing a standard encoding
     * name.
     *
     * <p>The encoding may be NULL if the `encoding` header in the commit is missing; in that case
     * UTF-8 is assumed.
     *
     * @return NULL, or the encoding
     */
    public String messageEncoding() {
        return jniMessageEncoding(getRawPointer());
    }

    /**
     * Get the full message of a commit.
     *
     * <p>The returned message will be slightly prettified by removing any potential leading
     * newlines.
     *
     * @return the message of a commit
     */
    public String message() {
        return jniMessage(getRawPointer());
    }

    /**
     * Get the full raw message of a commit.
     *
     * @return the raw message of a commit
     */
    public String messageRaw() {
        return jniMessageRaw(getRawPointer());
    }

    /**
     * Get the short "summary" of the git commit message.
     *
     * <p>The returned message is the summary of the commit, comprising the first paragraph of the
     * message with whitespace trimmed and squashed.
     *
     * @return the summary of a commit or NULL on error
     */
    public String summary() {
        return jniSummary(getRawPointer());
    }

    /**
     * Get the long "body" of the git commit message.
     *
     * <p>The returned message is the body of the commit, comprising everything but the first
     * paragraph of the message. Leading and trailing whitespaces are trimmed.
     *
     * @return the body of a commit or NULL when no the message only consists of a summary
     */
    public String body() {
        return jniBody(getRawPointer());
    }

    /**
     * Get the commit time (i.e. committer time) of a commit.
     *
     * @return the time of a commit
     */
    public Instant time() {
        return Instant.ofEpochSecond(jniTime(getRawPointer()));
    }

    /**
     * Get the commit timezone offset (i.e. committer's preferred timezone) of a commit.
     *
     * @return positive or negative timezone offset, in minutes from UTC
     */
    public int timeOffset() {
        return jniTimeOffset(getRawPointer());
    }

    /**
     * Get the committer of a commit.
     *
     * @return the committer of a commit
     */
    @Nonnull
    public Signature committer() {
        long ptr = jniCommitter(getRawPointer());
        return new Signature(true, ptr);
    }

    /**
     * Get the author of a commit.
     *
     * @return the author of a commit
     */
    @Nonnull
    public Signature author() {
        long ptr = jniAuthor(getRawPointer());
        return new Signature(true, ptr);
    }

    /**
     * Get the committer of a commit, using the mailmap to map names and email addresses to
     * canonical real names and email addresses.
     *
     * @param mailmap the mailmap to resolve with. (may be null)
     * @return signature that contains the committer identity
     * @throws GitException git errors
     */
    @Nullable
    public Signature committerWithMailmap(@Nullable Mailmap mailmap) {
        Signature outSig = new Signature(false, 0);
        Error.throwIfNeeded(
                jniCommitterWithMailmap(
                        outSig._rawPtr,
                        getRawPointer(),
                        mailmap == null ? 0 : mailmap.getRawPointer()));
        return outSig.isNull() ? null : outSig;
    }

    /**
     * Get the author of a commit, using the mailmap to map names and email addresses to canonical
     * real names and email addresses.
     *
     * @param mailmap the mailmap to resolve with. (may be NULL)
     * @return signature that contains the author information
     * @throws GitException git errors
     */
    @Nullable
    public Signature authorWithMailmap(@Nullable Mailmap mailmap) {
        Signature outSig = new Signature(false, 0);
        Error.throwIfNeeded(
                jniAuthorWithMailmap(
                        outSig._rawPtr,
                        getRawPointer(),
                        mailmap == null ? 0 : mailmap.getRawPointer()));
        return outSig.isNull() ? null : outSig;
    }

    /**
     * Get the full raw text of the commit header.
     *
     * @return the header text of the commit
     */
    public String rawHeader() {
        return jniRawHeader(getRawPointer());
    }

    /**
     * Get the tree pointed to by a commit. Equiv to {@code git rev-parse "$COMMIT^{tree}"} or
     * {@code git cat-file $COMMIT | grep tree}
     *
     * @return tree object
     * @throws GitException git errors
     */
    public Tree tree() {
        AtomicLong outTreePtr = new AtomicLong();
        Error.throwIfNeeded(jniTree(outTreePtr, getRawPointer()));
        return new Tree(false, outTreePtr.get());
    }

    /**
     * Get the id of the tree pointed to by a commit. This differs from `tree()` in that no attempts
     * are made to fetch an object from the ODB.
     *
     * @return the id of tree pointed to by commit.
     */
    @CheckForNull
    public Oid treeId() {
        return Oid.ofNullable(jniTreeId(getRawPointer()));
    }

    /**
     * Get the number of parents of this commit
     *
     * @return integer of count of parents
     */
    public int parentCount() {
        return jniParentCount(getRawPointer());
    }

    /**
     * Get the specified parent of the commit.
     *
     * @param n the position of the parent (from 0 to `parentcount`)
     * @return parent commit
     * @throws GitException git errors
     */
    public Commit parent(int n) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniParent(outPtr, getRawPointer(), n));
        return new Commit(false, outPtr.get());
    }

    /**
     *
     * <li>{@code git cat-file -p master | grep parent}
     * <li>{@code git rev-parse master^1 }
     * <li>{@code git log --pretty=%P -n 1 master}
     *
     *     <p>Get the oid of a specified parent for a commit. This is different from
     *     `git_commit_parent`, which will attempt to load the parent commit from the ODB.
     *
     * @param n the position of the parent (from 0 to `parentcount`)
     * @return the id of the parent, NULL on error.
     */
    @CheckForNull
    public Oid parentId(int n) {
        return Oid.ofNullable(jniParentId(getRawPointer(), n));
    }

    /**
     * Get the commit object that is the <n>th generation ancestor of the named commit object,
     * following only the first parents. The returned commit has to be freed by the caller.
     *
     * <p>Passing `0` as the generation number returns another instance of the base commit itself.
     *
     * @param n the requested generation
     * @return found commit, return null if no matching ancestor exists
     * @throws GitException git errors
     */
    public Commit nthGenAncestor(int n) {
        AtomicLong outPtr = new AtomicLong();
        int e = jniNthGenAncestor(outPtr, getRawPointer(), n);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Commit(false, outPtr.get());
    }

    /**
     * Get an arbitrary header field
     *
     * @param field the header field to return
     * @return git header field, return null if the field does not exist
     * @throws GitException git errors
     */
    @Nullable
    public String headerField(String field) {
        Buf buf = new Buf();
        int e = jniHeaderField(buf, getRawPointer(), field);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return buf.getString().orElse(null);
    }

    @Override
    public Commit dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return new Commit(false, out.get());
    }

    @FunctionalInterface
    public interface SigningCb {
        int accept(String signature, String signatureField, String commitContent);
    }
}

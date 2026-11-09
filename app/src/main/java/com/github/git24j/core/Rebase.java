package com.github.git24j.core;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Rebase extends CAutoReleasable {

        /** void *payload */
    static native void jniOptionsSetPayload(long optionsPtr, long payload);

    /** int git_rebase_abort(git_rebase *rebase); */
    static native int jniAbort(long rebase);

    /**
     * int git_rebase_commit(git_oid *id, git_rebase *rebase, const git_signature *author, const
     * git_signature *committer, const char *message_encoding, const char *message);
     */
    static native int jniCommit(
            Oid id,
            long rebase,
            long author,
            long committer,
            String messageEncoding,
            String message);

    /** int git_rebase_finish(git_rebase *rebase, const git_signature *signature); */
    static native int jniFinish(long rebase, long signature);

    /** void git_rebase_free(git_rebase *rebase); */
    static native void jniFree(long rebase);

    /**
     *
     *
     * <pre>
     * int git_rebase_init(
     *     git_rebase **out,
     *     git_repository *repo,
     *     const git_annotated_commit *branch,
     *     const git_annotated_commit *upstream,
     *     const git_annotated_commit *onto,
     *     const git_rebase_options *opts);
     * </pre>
     */
    static native int jniInit(
            AtomicLong out, long repoPtr, long branch, long upstream, long onto, long opts);

    /** int git_rebase_init_options(git_rebase_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /** int git_rebase_inmemory_index(git_index **index, git_rebase *rebase); */
    static native int jniInmemoryIndex(AtomicLong index, long rebase);

    /** int git_rebase_next(git_rebase_operation **operation, git_rebase *rebase); */
    static native int jniNext(AtomicLong operation, long rebase);

    /** const git_oid * git_rebase_onto_id(git_rebase *rebase); */
    static native byte[] jniOntoId(long rebase);

    /** const char * git_rebase_onto_name(git_rebase *rebase); */
    static native String jniOntoName(long rebase);

    /**
     * int git_rebase_open(git_rebase **out, git_repository *repo, const git_rebase_options *opts);
     */
    static native int jniOpen(AtomicLong out, long repoPtr, long opts);

    /** git_rebase_operation * git_rebase_operation_byindex(git_rebase *rebase, size_t idx); */
    static native long jniOperationByindex(long rebase, int idx);

    /** size_t git_rebase_operation_current(git_rebase *rebase); */
    static native int jniOperationCurrent(long rebase);

    /** size_t git_rebase_operation_entrycount(git_rebase *rebase); */
    static native int jniOperationEntrycount(long rebase);

    /** const char *exec */
    static native String jniOperationGetExec(long operationPtr);

    /** const git_oid id */
    static native byte[] jniOperationGetId(long operationPtr);

    /** int type */
    static native int jniOperationGetType(long operationPtr);

    static native void jniOptionsFree(long optsPtr);

    /** git_checkout_options checkout_options */
    static native long jniOptionsGetCheckoutOptions(long optionsPtr);

    /** int inmemory */
    static native int jniOptionsGetInmemory(long optionsPtr);

    /** git_merge_options merge_options */
    static native long jniOptionsGetMergeOptions(long optionsPtr);

    /** int quiet */
    static native int jniOptionsGetQuiet(long optionsPtr);

    /** const char *rewrite_notes_ref */
    static native String jniOptionsGetRewriteNotesRef(long optionsPtr);

    static native int jniOptionsGetVersion(long optionsPtr);

    /** int git_rebase_options_init(git_rebase_options *opts, unsigned int version); */
    static native int jniOptionsInit(long opts, int version);

    static native int jniOptionsNew(AtomicLong outOpts, int version);

    /** int inmemory */
    static native void jniOptionsSetInmemory(long optionsPtr, int inmemory);

    /** int quiet */
    static native void jniOptionsSetQuiet(long optionsPtr, int quiet);

    /** const char *rewrite_notes_ref */
    static native void jniOptionsSetRewriteNotesRef(long optionsPtr, String rewriteNotesRef);

    /** git_commit_signing_cb signing_cb */
    static native void jniOptionsSetSigningCb(long optionsPtr, Internals.SSSCallback signingCb);

    /** unsigned int version */
    static native void jniOptionsSetVersion(long optionsPtr, int version);

    /** const git_oid * git_rebase_orig_head_id(git_rebase *rebase); */
    static native byte[] jniOrigHeadId(long rebase);

    /** const char * git_rebase_orig_head_name(git_rebase *rebase); */
    static native String jniOrigHeadName(long rebase);

    protected Rebase(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Initializes a rebase operation to rebase the changes in `branch` relative to `upstream` onto
     * another branch. To begin the rebase process, call `git_rebase_next`. When you have finished
     * with this object, call `git_rebase_free`.
     *
     * @param repo The repository to perform the rebase
     * @param branch The terminal commit to rebase, or NULL to rebase the current branch
     * @param upstream The commit to begin rebasing from, or NULL to rebase all reachable commits
     * @param onto The branch to rebase onto, or NULL to rebase onto the given upstream
     * @param opts Options to specify how rebase is performed, or NULL
     * @return rebase object
     * @throws GitException git errors
     */
    @Nonnull
    public static Rebase init(
            @Nonnull Repository repo,
            @Nullable AnnotatedCommit branch,
            @Nullable AnnotatedCommit upstream,
            @Nullable AnnotatedCommit onto,
            @Nullable Options opts) {
        Rebase rebase = new Rebase(false, 0);
        Error.throwIfNeeded(
                jniInit(
                        rebase._rawPtr,
                        repo.getRawPointer(),
                        branch == null ? 0 : branch.getRawPointer(),
                        upstream == null ? 0 : upstream.getRawPointer(),
                        onto == null ? 0 : onto.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return rebase;
    }

    /**
     * Opens an existing rebase that was previously started by either an invocation of
     * `git_rebase_init` or by another client.
     *
     * @param repo The repository that has a rebase in-progress
     * @param opts Options to specify how rebase is performed
     * @return rebase object
     * @throws GitException git errors
     */
    @Nonnull
    public static Rebase open(@Nonnull Repository repo, @Nullable Options opts) {
        Rebase rebase = new Rebase(false, 0);
        Error.throwIfNeeded(
                jniOpen(
                        rebase._rawPtr,
                        repo.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return rebase;
    }

    /** Gets the onto id for merge rebases. */
    @CheckForNull
    public Oid ontoId() {
        byte[] raw = jniOntoId(getRawPointer());
        if (raw == null) {
            return null;
        }
        return Oid.of(raw);
    }

    /** Gets the onto id for merge rebases. */
    @CheckForNull
    public String ontoName() {
        return jniOntoName(getRawPointer());
    }

    @CheckForNull
    public Oid origHeadId() {
        byte[] raw = jniOrigHeadId(getRawPointer());
        return raw == null ? null : Oid.of(raw);
    }

    @CheckForNull
    public String origHeadName() {
        return jniOrigHeadName(getRawPointer());
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Gets the count of rebase operations that are to be applied.
     *
     * @return The number of rebase operations in total
     */
    public int operationEntrycount() {
        return jniOperationEntrycount(getRawPointer());
    }

    /**
     * Gets the index of the rebase operation that is currently being applied. If the first
     * operation has not yet been applied (because you have called `init` but not yet `next`) then
     * this returns `GIT_REBASE_NO_OPERATION`.
     *
     * @return The index of the rebase operation currently being applied.
     */
    public int operationCurrent() {
        return jniOperationCurrent(getRawPointer());
    }

    /**
     * Gets the rebase operation specified by the given index.
     *
     * @param idx The index of the rebase operation to retrieve
     * @return The rebase operation or null if `idx` was out of bounds
     */
    @Nullable
    public Operation operationByIndex(int idx) {
        long ptr = jniOperationByindex(getRawPointer(), idx);
        if (ptr == 0) {
            return null;
        }
        return new Operation(ptr);
    }

    /**
     * Performs the next rebase operation and returns the information about it. If the operation is
     * one that applies a patch (which is any operation except GIT_REBASE_OPERATION_EXEC) then the
     * patch will be applied and the index and working directory will be updated with the changes.
     * If there are conflicts, you will need to address those before committing the changes.
     *
     * @return Pointer to store the rebase operation that is to be performed next
     * @throws GitException git errors
     */
    @Nonnull
    public Operation next() {
        Operation out = new Operation(0);
        Error.throwIfNeeded(jniNext(out._rawPtr, getRawPointer()));
        return out;
    }

    /**
     * Gets the index produced by the last operation, which is the result of `git_rebase_next` and
     * which will be committed by the next invocation of `git_rebase_commit`. This is useful for
     * resolving conflicts in an in-memory rebase before committing them. You must call
     * `git_index_free` when you are finished with this.
     *
     * <p>This is only applicable for in-memory rebases; for rebases within a working directory, the
     * changes were applied to the repository's index.
     */
    @Nonnull
    public Index inmemoryIndex() {
        Index outIdx = new Index(false, 0);
        Error.throwIfNeeded(jniInmemoryIndex(outIdx._rawPtr, getRawPointer()));
        return outIdx;
    }

    /**
     * Commits the current patch. You must have resolved any conflicts that were introduced during
     * the patch application from the `git_rebase_next` invocation.
     *
     * @param author The author of the updated commit, or NULL to keep the author from the original
     *     commit
     * @param committer The committer of the rebase
     * @param messageEncoding The encoding for the message in the commit, represented with a
     *     standard encoding name. If message is NULL, this should also be NULL, and the encoding
     *     from the original commit will be maintained. If message is specified, this may be NULL to
     *     indicate that "UTF-8" is to be used.
     * @param message The message for this commit, or NULL to use the message from the original
     *     commit.
     * @return the OID of the newly created commit
     * @throws GitException GIT_EUNMERGED if there are unmerged changes in the index, GIT_EAPPLIED
     *     if the current commit has already been applied to the upstream and there is nothing to
     *     commit, -1 on failure.
     */
    public Oid commit(
            @Nullable Signature author,
            @Nonnull Signature committer,
            @Nullable Charset messageEncoding,
            @Nullable String message) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniCommit(
                        oid,
                        getRawPointer(),
                        author == null ? 0 : author.getRawPointer(),
                        committer.getRawPointer(),
                        messageEncoding == null ? null : messageEncoding.name(),
                        message));
        return oid;
    }

    /**
     * Aborts a rebase that is currently in progress, resetting the repository and working directory
     * to their state before rebase began.
     *
     * @throws GitException GIT_ENOTFOUND if a rebase is not in progress, -1 on other errors.
     */
    public void abort() {
        Error.throwIfNeeded(jniAbort(getRawPointer()));
    }

    /**
     * Finishes a rebase that is currently in progress once all patches have been applied.
     *
     * @param signature The identity that is finishing the rebase (optional)
     * @throws GitException git errors
     */
    public void finish(@Nullable Signature signature) {
        Error.throwIfNeeded(
                jniFinish(getRawPointer(), signature == null ? 0 : signature.getRawPointer()));
    }
    /** Type of rebase operation in-progress after calling `git_rebase_next`. */
    public enum OperationT implements IBitEnum {
        /**
         * The given commit is to be cherry-picked. The client should commit the changes and
         * continue if there are no conflicts.
         */
        PICK(0),

        /**
         * The given commit is to be cherry-picked, but the client should prompt the user to provide
         * an updated commit message.
         */
        REWORD(1),

        /**
         * The given commit is to be cherry-picked, but the client should stop to allow the user to
         * edit the changes before committing them.
         */
        EDIT(2),

        /**
         * The given commit is to be squashed into the previous commit. The commit message will be
         * merged with the previous message.
         */
        SQUASH(3),

        /**
         * The given commit is to be squashed into the previous commit. The commit message from this
         * commit will be discarded.
         */
        FIXUP(4),

        /**
         * No commit will be cherry-picked. The client should run the given command and (if
         * successful) continue.
         */
        EXEC(5);
        private final int _bit;

        OperationT(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Rebase options
     *
     * <p>Use to tell the rebase machinery how to operate.
     */
    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }

        public static Options createDefault() {
            return create(VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        /** unsigned int version */
        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }

        /** unsigned int version */
        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }

        /** int quiet */
        public int getQuiet() {
            return jniOptionsGetQuiet(getRawPointer());
        }

        /** int quiet */
        public void setQuiet(int quiet) {
            jniOptionsSetQuiet(getRawPointer(), quiet);
        }

        /** int inmemory */
        public int getInmemory() {
            return jniOptionsGetInmemory(getRawPointer());
        }

        /** int inmemory */
        public void setInmemory(int inmemory) {
            jniOptionsSetInmemory(getRawPointer(), inmemory);
        }

        /** const char *rewrite_notes_ref */
        public String getRewriteNotesRef() {
            return jniOptionsGetRewriteNotesRef(getRawPointer());
        }

        /** const char *rewrite_notes_ref */
        public void setRewriteNotesRef(String rewriteNotesRef) {
            jniOptionsSetRewriteNotesRef(getRawPointer(), rewriteNotesRef);
        }

        /** git_merge_options merge_options */
        @Nonnull
        public Merge.Options getMergeOptions() {
            long ptr = jniOptionsGetMergeOptions(getRawPointer());
            return new Merge.Options(true, ptr);
        }

        /** git_checkout_options checkout_options */
        public Checkout.Options getCheckoutOptions() {
            return new Checkout.Options(true, jniOptionsGetCheckoutOptions(getRawPointer()));
        }

        /** git_commit_signing_cb signing_cb */
        public void setSigningCb(Commit.SigningCb signingCb) {
            jniOptionsSetSigningCb(getRawPointer(), signingCb::accept);
        }

        // TODO: moved to Options
        public void setPayload(long payload) {
            jniOptionsSetPayload(getRawPointer(), payload);
        }
    }

    public static class Operation extends CAutoReleasable {
        protected Operation(long rawPtr) {
            super(true, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            // skipped, operation are managed by native c
        }

        @CheckForNull
        public OperationT getType() {
            int r = jniOperationGetType(getRawPointer());
            return IBitEnum.valueOf(r, OperationT.class);
        }

        @CheckForNull
        public Oid getId() {
            byte[] raw = jniOperationGetId(this.getRawPointer());
            return raw == null ? null : Oid.of(raw);
        }

        @CheckForNull
        public String getExec() {
            return jniOperationGetExec(getRawPointer());
        }
    }
}

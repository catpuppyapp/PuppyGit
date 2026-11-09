package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Reflog extends CAutoReleasable {
    /**
     * int git_reflog_append(git_reflog *reflog, const git_oid *id, const git_signature *committer,
     * const char *msg);
     */
    static native int jniAppend(long reflog, Oid id, long committer, String msg);

    /** int git_reflog_delete(git_repository *repo, const char *name); */
    static native int jniDelete(long repoPtr, String name);

    /** int git_reflog_drop(git_reflog *reflog, size_t idx, int rewrite_previous_entry); */
    static native int jniDrop(long reflog, int idx, int rewritePreviousEntry);

    /** const git_reflog_entry * git_reflog_entry_byindex(const git_reflog *reflog, size_t idx); */
    static native long jniEntryByindex(long reflog, int idx);

    /** const git_signature * git_reflog_entry_committer(const git_reflog_entry *entry); */
    static native long jniEntryCommitter(long entry);

    /** const git_oid * git_reflog_entry_id_new(const git_reflog_entry *entry); */
    static native byte[] jniEntryIdNew(long entry);

    /** const git_oid * git_reflog_entry_id_old(const git_reflog_entry *entry); */
    static native byte[] jniEntryIdOld(long entry);

    /** const char * git_reflog_entry_message(const git_reflog_entry *entry); */
    static native String jniEntryMessage(long entry);

    /** size_t git_reflog_entrycount(git_reflog *reflog); */
    static native int jniEntrycount(long reflog);

    /** void git_reflog_free(git_reflog *reflog); */
    static native void jniFree(long reflog);

    /** int git_reflog_read(git_reflog **out, git_repository *repo, const char *name); */
    static native int jniRead(AtomicLong out, long repoPtr, String name);

    /** int git_reflog_rename(git_repository *repo, const char *old_name, const char *name); */
    static native int jniRename(long repoPtr, String oldName, String name);

    /**
     * int git_transaction_set_reflog(git_transaction *tx, const char *refname, const git_reflog
     * *reflog);
     */
//    static native int jniSetReflog(long tx, String refname, long reflog);

    /** int git_reflog_write(git_reflog *reflog); */
    static native int jniWrite(long reflog);

    protected Reflog(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    public static void rename(
            @Nonnull Repository repository, @Nonnull String oldName, @Nonnull String name) {
        Error.throwIfNeeded(jniRename(repository.getRawPointer(), oldName, name));
    }

    public void append(@Nonnull Oid oid, @Nonnull Signature committer, @Nullable String msg) {
        Error.throwIfNeeded(jniAppend(getRawPointer(), oid, committer.getRawPointer(), msg));
    }

    public void delete(@Nonnull Repository repository, @Nonnull String name) {
        Error.throwIfNeeded(jniDelete(repository.getRawPointer(), name));
    }

    public void drop(int idx, boolean rewritePreviousEntry) {
        Error.throwIfNeeded(jniDrop(getRawPointer(), idx, rewritePreviousEntry ? 1 : 0));
    }

    @Nonnull
    public Entry entryByIndex(int idx) {
        return new Entry(true, jniEntryByindex(getRawPointer(), idx));
    }

    public int entryCount() {
        return jniEntrycount(getRawPointer());
    }

    public static Reflog read(@Nonnull Repository repository, @Nonnull String name) {
        Reflog reflog = new Reflog(false, 0);
        int e = jniRead(reflog._rawPtr, repository.getRawPointer(), name);
        Error.throwIfNeeded(e);
        return reflog;
    }

//    public void transactionSetReflog(@Nonnull Transaction tx, @Nonnull String refname) {
//        Error.throwIfNeeded(jniSetReflog(tx.getRawPointer(), refname, getRawPointer()));
//    }

    public void write() {
        Error.throwIfNeeded(jniWrite(getRawPointer()));
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            // entry is always weak
        }

        @Nonnull
        public Signature committer() {
            return new Signature(true, jniEntryCommitter(getRawPointer()));
        }

        @Nonnull
        public Oid idNew() {
            return Oid.of(jniEntryIdNew(getRawPointer()));
        }

        @Nonnull
        public Oid idOld() {
            return Oid.of(jniEntryIdOld(getRawPointer()));
        }

        @Nullable
        public String message() {
            return jniEntryMessage(getRawPointer());
        }
    }
}

package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FilterList extends CAutoReleasable {
    /** int git_filterList_apply_to_blob(git_buf *out, git_filter_list *filters, git_blob *blob); */
    static native int jniApplyToBlob(Buf out, long filters, long blob);

    /** int git_filterList_apply_to_data(git_buf *out, git_filter_list *filters, git_buf *in); */
    static native int jniApplyToData(Buf out, long filters, String in);

    /**
     * int git_filterList_apply_to_file(git_buf *out, git_filter_list *filters, git_repository
     * *repo, const char *path);
     */
    static native int jniApplyToFile(Buf out, long filters, long repoPtr, String path);

    /** int git_filterList_contains(git_filter_list *filters, const char *name); */
    static native int jniContains(long filters, String name);

    /** void git_filterList_free(git_filter_list *filters); */
    static native void jniFree(long filters);

    /**
     * int git_filterList_load(git_filter_list **filters, git_repository *repo, git_blob *blob,
     * const char *path, int mode, int flags);
     */
    static native int jniLoad(
            AtomicLong filters, long repoPtr, long blob, String path, int mode, int flags);

    /**
     * int git_filterList_stream_blob(git_filter_list *filters, git_blob *blob, git_writestream
     * *target);
     */
    static native int jniStreamBlob(long filters, long blob, long target);

    /**
     * int git_filterList_stream_data(git_filter_list *filters, git_buf *data, git_writestream
     * *target);
     */
    static native int jniStreamData(long filters, String data, long target);

    /**
     * int git_filterList_stream_file(git_filter_list *filters, git_repository *repo, const char
     * *path, git_writestream *target);
     */
    static native int jniStreamFile(long filters, long repoPtr, String path, long target);

    protected FilterList(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Load the filter list for a given path or null if no filters are requested for the given file.
     *
     * @param repo Repository object that contains `path`
     * @param blob The blob to which the filter will be applied (if known)
     * @param path Relative path of the file to be filtered
     * @param mode Filtering direction (WT->ODB or ODB->WT)
     * @param flags Combination of `git_filter_flag_t` flags
     * @return loaded filters or null
     * @throws GitException git2 exceptions
     */
    @CheckForNull
    public static FilterList load(
            @Nonnull Repository repo,
            @Nullable Blob blob,
            @Nonnull String path,
            ModeT mode,
            @Nonnull EnumSet<FlagT> flags) {
        FilterList out = new FilterList(false, 0);
        int e =
                jniLoad(
                        out._rawPtr,
                        repo.getRawPointer(),
                        blob == null ? 0 : blob.getRawPointer(),
                        path,
                        mode.getBit(),
                        IBitEnum.bitOrAll(flags));
        Error.throwIfNeeded(e);
        return out;
    }

    /**
     * Query the filter list to see if a given filter (by name) will run. The built-in filters
     * "crlf" and "ident" can be queried, otherwise this is the name of the filter specified by the
     * filter attribute.
     *
     * <p>This will return 0 if the given filter is not in the list, or 1 if the filter will be
     * applied.
     *
     * @param filters A loaded git_filter_list (or NULL)
     * @param name The name of the filter to query
     * @return true if the filter is in the list, false otherwise
     */
    public static boolean contains(@Nullable FilterList filters, @Nonnull String name) {
        return jniContains(filters == null ? 0 : filters.getRawPointer(), name) != 0;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Apply filter list to a data buffer.
     *
     * @param in Buffer containing the data to filter
     * @return filtered string or empty string("") if nothing nothing left after filtering.
     * @throws GitException git2 exceptions
     */
    @Nonnull
    public String applyToData(@Nonnull String in) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniApplyToData(out, getRawPointer(), in));
        return out.getString().orElse("");
    }

    /**
     * Apply a filter list to the contents of a file on disk
     *
     * @param repo the repository in which to perform the filtering
     * @param relativePath the path of the file to filter, a relative path will be taken as relative
     *     to the workdir
     * @return filtered string or empty string("") if nothing nothing left after filtering.
     * @throws GitException git2 exceptions
     */
    @Nonnull
    public String applyToFile(@Nonnull Repository repo, @Nonnull String relativePath) {
        Buf out = new Buf();
        int e = jniApplyToFile(out, getRawPointer(), repo.getRawPointer(), relativePath);
        Error.throwIfNeeded(e);
        return out.getString().orElse("");
    }

    /**
     * Apply a filter list to the contents of a blob
     *
     * @param blob the blob to filter
     * @return filtered string or empty ("") if nothing left after filtering
     * @throws GitException git exceptions
     */
    @Nonnull
    public String applyToBlob(@Nonnull Blob blob) {
        Buf out = new Buf();
        int e = jniApplyToBlob(out, getRawPointer(), blob.getRawPointer());
        Error.throwIfNeeded(e);
        return out.getString().orElse("");
    }

    /**
     * Apply a filter list to an arbitrary buffer as a stream
     *
     * @param data the buffer to filter
     * @param target the stream into which the data will be written
     * @throws GitException git2 exceptions
     */
    public void streamData(@Nonnull String data, @Nonnull WriteStream target) {
        int e = jniStreamData(getRawPointer(), data, target.getRawPointer());
        Error.throwIfNeeded(e);
    }

    /**
     * Apply a filter list to a file as a stream
     *
     * @param repo the repository in which to perform the filtering
     * @param relativePath the path of the file to filter, a relative path will be taken as relative
     *     to the workdir
     * @param target the stream into which the data will be written
     * @throws GitException git2 exception
     */
    public void streamFile(
            @Nonnull Repository repo, @Nonnull String relativePath, @Nonnull WriteStream target) {
        int e =
                jniStreamFile(
                        getRawPointer(),
                        repo.getRawPointer(),
                        relativePath,
                        target.getRawPointer());
        Error.throwIfNeeded(e);
    }

    /**
     * Apply a filter list to a blob as a stream
     *
     * @param blob the blob to filter
     * @param target the stream into which the data will be written
     */
    public void streamBlob(@Nonnull Blob blob, @Nonnull WriteStream target) {
        int e = jniStreamBlob(getRawPointer(), blob.getRawPointer(), target.getRawPointer());
        Error.throwIfNeeded(e);
    }

    public enum ModeT implements IBitEnum {
        TO_WORKTREE(0),
        SMUDGE(0),
        TO_ODB(1),
        CLEAN(1),
        ;
        private final int _bit;

        ModeT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum FlagT implements IBitEnum {
        DEFAULT(0),

        /** Don't error for `safecrlf` violations, allow them to continue. */
        ALLOW_UNSAFE(1 << 0),

        /** Don't load `/etc/gitattributes` (or the system equivalent) */
        NO_SYSTEM_ATTRIBUTES(1 << 1),

        /** Load attributes from `.gitattributes` in the root of HEAD */
        ATTRIBUTES_FROM_HEAD(1 << 2),
        ;
        private final int _bit;

        FlagT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }
}

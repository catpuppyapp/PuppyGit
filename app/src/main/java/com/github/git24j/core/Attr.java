package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class Attr {
    /** int git_attr_add_macro(git_repository *repo, const char *name, const char *values); */
    static native int jniAddMacro(long repoPtr, String name, String values);

    /** int git_attr_cache_flush(git_repository *repo); */
    static native int jniCacheFlush(long repoPtr);

    /**
     * int git_attr_foreach(git_repository *repo, int flags, const char *path, long callback, void
     * *payload);
     */
    static native int jniForeach(long repoPtr, int flags, String path, ForeachCb foreachCb);

    /**
     * int git_attr_get(const char **value_out, git_repository *repo, int flags, const char *path,
     * const char *name);
     */
    static native int jniGet(
            AtomicReference<String> valueOut, long repoPtr, int flags, String path, String name);

    /**
     * int git_attr_get_many(const char **values_out, git_repository *repo, int flags, const char
     * *path, size_t num_attr, const char **names);
     */
    static native int jniGetMany(
            List<String> valuesOut, long repoPtr, int flags, String path, String[] names);

    /** git_attr_value_t git_attr_value(const char *attr); */
    static native int jniValue(String attr);

    /**
     * Add a macro definition.
     *
     * <p>Macros will automatically be loaded from the top level `.gitattributes` file of the
     * repository (plus the build-in "binary" macro). This function allows you to add others. For
     * example, to add the default macro, you would call:
     *
     * <pre>
     *     git_attr_add_macro(repo, "binary", "-diff -crlf");
     *  </pre>
     */
    public static void addMacro(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull String value) {
        Error.throwIfNeeded(jniAddMacro(repo.getRawPointer(), name, value));
    }

    public static void cacheFlush(@Nonnull Repository repo) {
        Error.throwIfNeeded(jniCacheFlush(repo.getRawPointer()));
    }

    /**
     * Loop over all the git attributes for a path.
     *
     * @param repo The repository containing the path.
     * @param flags A combination of GIT_ATTR_CHECK... flags.
     * @param path Path inside the repo to check attributes. This does not have to exist, but if it
     *     does not, then it will be treated as a plain file (i.e. not a directory).
     * @param callback Function to invoke on each attribute name and value. See git_attr_foreach_cb.
     * @return callback return code, 0 implies success
     * @throws GitException git2 exceptions
     */
    public static int foreach(
            @Nonnull Repository repo,
            EnumSet<CheckFlags> flags,
            @Nonnull String path,
            @Nonnull ForeachCb callback) {
        int e = jniForeach(repo.getRawPointer(), IBitEnum.bitOrAll(flags), path, callback);
        Error.throwIfNeeded(e);
        return e;
    }

    @Nonnull
    public static ValueT value(@Nonnull String attr) {
        return IBitEnum.valueOf(jniValue(attr), ValueT.class, ValueT.UNSPECIFIED);
    }

    /**
     * Look up the value of one git attribute for path.
     *
     * @return Output of the value of the attribute. Use the GIT_ATTR_... macros to test for TRUE,
     *     FALSE, UNSPECIFIED, etc. or just use the string value for attributes set to a value. You
     *     should NOT modify or free this value.
     * @param repo The repository containing the path.
     * @param flags A combination of GIT_ATTR_CHECK... flags.
     * @param path The path to check for attributes. Relative paths are interpreted relative to the
     *     repo root. The file does not have to exist, but if it does not, then it will be treated
     *     as a plain file (not a directory).
     * @param name The name of the attribute to look up.
     * @throws GitException git2 exception
     */
    public static String getAttr(
            @Nonnull Repository repo,
            @Nonnull EnumSet<CheckFlags> flags,
            @Nonnull String path,
            @Nonnull String name) {
        AtomicReference<String> out = new AtomicReference<>();
        int e = jniGet(out, repo.getRawPointer(), IBitEnum.bitOrAll(flags), path, name);
        Error.throwIfNeeded(e);
        return out.get();
    }

    @Nonnull
    public static List<String> getMany(
            @Nonnull Repository repo,
            @Nonnull EnumSet<CheckFlags> flags,
            @Nonnull String path,
            @Nonnull List<String> names) {
        List<String> out = new ArrayList<>();
        int e =
                jniGetMany(
                        out,
                        repo.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        path,
                        names.toArray(new String[0]));
        Error.throwIfNeeded(e);
        return out;
    }

    public enum ValueT implements IBitEnum {
        /** < The attribute has been left unspecified */
        UNSPECIFIED(0),
        /** < The attribute has been set */
        TRUE(1),
        /** < The attribute has been unset */
        FALSE(2),
        /** < This attribute has a value */
        STRING(3),
        ;
        private final int _bit;

        ValueT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum CheckFlags implements IBitEnum {
        CHECK_FILE_THEN_INDEX(0),
        CHECK_INDEX_THEN_FILE(1),
        CHECK_INDEX_ONLY(2),
        CHECK_NO_SYSTEM(1 << 2),
        CHECK_INCLUDE_HEAD(1 << 3);
        private final int _bit;

        CheckFlags(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface ForeachCb {
        int accept(String name, String value);
    }
}

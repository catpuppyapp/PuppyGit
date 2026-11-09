package com.github.git24j.core;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/** @author shijing Setup functions */
public class Init {

    private static final String CUSTOM_LIB_PATH_PROP = "com.github.git24j.library_path";
    private static final String JAVA_LIB_PATH_PROP = "java.library.path";
    private static final String DEFAULT_LIB_PATH = "target";
    private static AtomicBoolean _loaded = new AtomicBoolean(false);

    /**
     * Search following paths(in order) for the native library, load if found, throw exceptions if
     * not.
     *
     * <ul>
     *   <li>paths defined in {@code com.github.git24j.library_path}
     *   <li>paths defined in {@code java.library.path}
     *   <li>hard coded path in ${project.dir}/target/${lib}/
     * </ul>
     *
     * @param lib library to load
     * @throws FileNotFoundException could not load the shared library
     */
    static synchronized void loadLibrary(NativeLib lib, @Nullable Path searchPath)
            throws FileNotFoundException {
        Path libPath;
        if (searchPath != null) {
            libPath = searchPath.resolve(lib.mappedLibraryName());
            if (Files.exists(libPath)) {
                System.load(libPath.toAbsolutePath().toString());
                return;
            }
        }
        String customPath = System.getProperty(CUSTOM_LIB_PATH_PROP, null);
        if (customPath != null) {
            System.setProperty(
                    JAVA_LIB_PATH_PROP,
                    customPath + ":" + System.getProperty(JAVA_LIB_PATH_PROP, ""));
        }
        try {
            System.loadLibrary(lib.shortName());
        } catch (UnsatisfiedLinkError ignore) {
            // ignore and try next candidate location
        }
        libPath = Paths.get(DEFAULT_LIB_PATH, lib.shortName(), lib.mappedLibraryName());
        if (!Files.exists(libPath)) {
            throw new FileNotFoundException("Could not load library: " + lib.mappedLibraryName());
        }
        System.load(libPath.toAbsolutePath().toString());
    }

    /**
     * Load necessary libraries from specific folders or from default paths which are:
     *
     * <ul>
     *   <li>paths defined in {@code com.github.git24j.library_path}
     *   <li>paths defined in {@code java.library.path}
     *   <li>hard coded path in ${project.dir}/target/${lib}/
     * </ul>
     *
     * NOTE: The first two properties must be specified * through jvm option before JVM
     * initialization (aka cannot be reset at runtime).
     *
     * @throws GitException if loading fails
     * @param git2Path path to search for libgit2 shared lib, pass NULL if search from
     */
    public static synchronized void loadLibraries(
            @Nullable Path git2Path, @Nullable Path git24jPath) {
        if (!_loaded.get()) {
            try {
                loadLibrary(NativeLib.GIT2, git2Path);
                loadLibrary(NativeLib.GIT24J, git24jPath);
            } catch (FileNotFoundException e) {
                throw new GitException(
                        GitException.ErrorClass.ZLIB, "Could not load native libraries");
            }
            _loaded.set(true);
        }
    }

    private enum NativeLib {
        /** libgit2 */
        GIT2("git2"),
        /** libgit24j */
        GIT24J("git24j");
        private final String _name;

        NativeLib(String name) {
            _name = name;
        }
        /** platform-specific name of the lib, e.g. libgit2.so (linux) and libgit2.dll (windows). */
        String mappedLibraryName() {
            return System.mapLibraryName(_name);
        }
        /** short name like git2 and git24j. */
        String shortName() {
            return _name;
        }
    }
}

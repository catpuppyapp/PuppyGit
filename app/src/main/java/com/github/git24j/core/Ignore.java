package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class Ignore {

    /** int git_ignore_add_rule(git_repository *repo, const char *rules); */
    static native int jniAddRule(long repoPtr, String rules);

    /** int git_ignore_clear_internal_rules(git_repository *repo); */
    static native int jniClearInternalRules(long repoPtr);

    /** int git_ignore_path_is_ignored(int *ignored, git_repository *repo, const char *path); */
    static native int jniPathIsIgnored(AtomicInteger ignored, long repoPtr, String path);

    /**
     * Add ignore rules for a repository.
     *
     * <p>Excludesfile rules (i.e. .gitignore rules) are generally read from .gitignore files in the
     * repository tree or from a shared system file only if a "core.excludesfile" config value is
     * set. The library also keeps a set of per-repository internal ignores that can be configured
     * in-memory and will not persist. This function allows you to add to that internal rules list.
     *
     * <p>Example usage:
     *
     * <p>error = git_ignore_add_rule(myrepo, "*.c\ndir/\nFile with space\n");
     *
     * <p>This would add three rules to the ignores.
     *
     * @param repo The repository to add ignore rules to.
     * @param rules Text of rules, a la the contents of a .gitignore file. It is okay to have
     *     multiple rules in the text; if so, each rule should be terminated with a newline.
     * @throws GitException git2 exception
     */
    public static void addRule(@Nonnull Repository repo, @Nonnull String rules) {
        Error.throwIfNeeded(jniAddRule(repo.getRawPointer(), rules));
    }

    /**
     * Test if the ignore rules apply to a given path.
     *
     * <p>This function checks the ignore rules to see if they would apply to the given file. This
     * indicates if the file would be ignored regardless of whether the file is already in the index
     * or committed to the repository.
     *
     * <p>One way to think of this is if you were to do "git check-ignore --no-index" on the given
     * file, would it be shown or not?
     *
     * @param repo a repository object
     * @param path the file to check ignores for, relative to the repo's workdir.
     * @return if the file is not ignored
     * @throws GitException if ignore rules could not be processed for the file.
     */
    public static boolean pathIsIgnored(@Nonnull Repository repo, @Nonnull String path) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniPathIsIgnored(out, repo.getRawPointer(), path));
        return out.get() != 0;
    }

    /**
     * Clear ignore rules that were explicitly added.
     *
     * <p>Resets to the default internal ignore rules. This will not turn off rules in .gitignore
     * files that actually exist in the filesystem.
     *
     * <p>The default internal ignores ignore ".", ".." and ".git" entries.
     *
     * @param repo The repository to remove ignore rules from.
     * @throws GitException git2 exceptions
     */
    public static void clearInternalRules(@Nonnull Repository repo) {
        Error.throwIfNeeded(jniClearInternalRules(repo.getRawPointer()));
    }
}

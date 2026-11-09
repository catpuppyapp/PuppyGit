package com.catpuppyapp.puppygit.syntaxhighlight.base

//PL is "Program Language"

enum class PLScope(val scope: String) {

    // init value for state
    AUTO("AUTO_DETECTED"),


    // 无语法高亮
    // 可不解析或用 EmptyLanguage()
    NONE(""),

    // order by a-z
    BAT("source.batchfile"),
    C("source.c"),
    CLOJURE("source.clojure"),
    COFFEE_SCRIPT("source.coffee"),
    CPP("source.cpp"),
    CSHARP("source.cs"),
    CSS("source.css"),
    DART("source.dart"),
    DIFF("source.diff"),
    DOCKER_FILE("source.dockerfile"),
    FSHARP("source.fsharp"),
    GO("source.go"),
    GROOVY("source.groovy"),
    GIT_IGNORE("source.ignore"),
    GIT_REBASE("text.git-rebase"),
    HTML("text.html.basic"),
    INI("source.ini"),
    JAVA("source.java"),
    JAVASCRIPT("source.js"),
    JSX("source.js.jsx"),
    JSON("source.json"),
    JULIA("source.julia"),
    KOTLIN("source.kotlin"),
    LATEX("text.tex.latex"),
    LESS("source.css.less"),
    LUA("source.lua"),
    MAKE_FILE("source.makefile"),
    MARKDOWN("text.html.markdown"),
    MARKDOWN_MATH("text.html.markdown.math"),
    PERL("source.perl"),
    PHP("source.php"),
    POWER_SHELL("source.powershell"),
    PROGUARD("source.proguard"),
    PYTHON("source.python"),
    RAKU("source.perl.6"),
    RUBY("source.ruby"),
    RUST("source.rust"),
    SCSS("source.css.scss"),
    SHELL("source.shell"),
    SQL("source.sql"),
    SWIFT("source.swift"),
    TOML("source.toml"),
    TS("source.ts"),
    TSX("source.tsx"),
    VB("source.asp.vb.net"),
    VUE("source.vue"),
    XML("text.xml"),
    XSL("text.xml.xsl"),
    YAML("source.yaml"),
    ZIG("source.zig"),

    ;


    companion object {

        val SCOPES = entries.map { it.scope }
        val SCOPES_NO_AUTO = entries.filter { it != AUTO }

        fun guessScopeType(fileName: String) : PLScope {
            val fileName = fileName.lowercase()

            //note: must use lower case extensions to match
            //note: must use lower case extensions to match
            //note: must use lower case extensions to match
            //note: must use lower case extensions to match
            //note: must use lower case extensions to match
            //note: must use lower case extensions to match

            if(fileName.endsWith(".txt") || fileName.endsWith(".text")) {
                return NONE
            }

            if(fileName.endsWith(".gitignore")) {
                return GIT_IGNORE
            }

            if(fileName.endsWith(".ts")) {
                return TS
            }

            // iml is jetbrains config file extension, it use xml format
            if(fileName.endsWith(".xml") || fileName.endsWith(".iml")) {
                return XML
            }

            if(fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
                return YAML
            }

            if(fileName.endsWith(".md") || fileName.endsWith(".mdown") || fileName.endsWith(".markdown")) {
                return MARKDOWN
            }

            if(fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                return HTML
            }

            if(fileName.endsWith(".json")) {
                return JSON
            }

            if(fileName.endsWith(".java")) {
                return JAVA
            }

            if(fileName.endsWith(".js") || fileName.endsWith(".javascript")) {
                return JAVASCRIPT
            }

            if(fileName.endsWith(".jsx")) {
                return JSX
            }

            if(fileName.endsWith(".tsx")) {
                return TSX
            }

            if(fileName.endsWith(".ini") || fileName == "config" || fileName.endsWith(".gitconfig") || fileName.endsWith(".gitmodules")) {
                return INI
            }

            if(fileName.endsWith(".vue")) {
                return VUE
            }

            if(fileName.endsWith(".lua")) {
                return LUA
            }

            if(fileName.endsWith(".kt") || fileName.endsWith(".kts") || fileName.endsWith(".kotlin")) {
                return KOTLIN
            }

            if(fileName.endsWith(".go")) {
                return GO
            }

            if(fileName.endsWith(".c")) {
                return C
            }

            // c and cpp both use same header extension .h, but cpp is super set of c, so match to cpp better
            // c和c++头文件后缀名一样，但c++是c的超集，所以匹配到c++兼容性更好
            if(fileName.endsWith(".h")
                || fileName.endsWith(".cpp")
                || fileName.endsWith(".hpp")
                || fileName.endsWith(".cc")
                || fileName.endsWith(".cxx")
                || fileName.endsWith(".c++")
            ) {
                return CPP
            }


            if(fileName.endsWith(".diff") || fileName.endsWith(".patch")) {
                return DIFF
            }


            if(fileName.endsWith(".bat") || fileName.endsWith(".cmd")) {
                return BAT
            }

            if(fileName.endsWith(".cs")) {
                return CSHARP
            }

            if(fileName.endsWith(".dart")) {
                return DART
            }

            if(fileName.endsWith(".php")) {
                return PHP
            }

            if(fileName.endsWith(".py") || fileName.endsWith(".python")) {
                return PYTHON
            }

            if(fileName.endsWith(".rs") || fileName.endsWith(".rust")) {
                return RUST
            }

            if(fileName.endsWith(".gradle") || fileName.endsWith(".groovy")) {
                return GROOVY
            }

            if(fileName == "dockerfile" || fileName.endsWith(".dockerfile")) {
                return DOCKER_FILE
            }

            if(fileName.endsWith(".sh")) {
                return SHELL
            }

            if(fileName.endsWith(".zig") || fileName.endsWith(".zon")) {
                return ZIG
            }

            if(fileName.endsWith(".toml")) {
                return TOML
            }

            if(fileName == "makefile" || fileName.endsWith(".makefile")) {
                return MAKE_FILE
            }

            if(fileName.endsWith(".css")) {
                return CSS
            }

            if(fileName.endsWith(".less")) {
                return LESS
            }


            if(fileName.endsWith(".ps1")) {
                return POWER_SHELL
            }

            if(fileName.endsWith(".vb")) {
                return VB
            }

            if(fileName.endsWith(".rb") || fileName.endsWith(".ruby")

                // Vagrantfile is ruby based
                || fileName == "vagrantfile" || fileName.endsWith(".vagrantfile")
            ) {
                return RUBY
            }


            if(fileName.endsWith(".pl") || fileName.endsWith(".perl")
                || fileName.endsWith(".pm")|| fileName.endsWith(".psgi")
            ) {
                return PERL
            }


            if(fileName.endsWith(".pl6") || fileName.endsWith(".p6")
                || fileName.endsWith(".perl6") || fileName.endsWith(".pm6") || fileName.endsWith(".nqp")
                || fileName.endsWith(".raku") || fileName.endsWith(".rakudoc") || fileName.endsWith(".rakumod")
            ) {
                return RAKU
            }

            if(fileName.endsWith(".scss") || fileName.endsWith(".sass")) {
                return SCSS
            }

            if(fileName.endsWith(".clj") || fileName.endsWith(".cljs")) {
                return CLOJURE
            }

            if(fileName.endsWith(".coffee")) {
                return COFFEE_SCRIPT
            }

            if(fileName.endsWith(".sql")) {
                return SQL
            }

            if(fileName.endsWith(".pro") || fileName.endsWith(".proguard") || fileName.endsWith(".r8")) {
                return PROGUARD
            }

            if(fileName.endsWith(".swift")) {
                return SWIFT
            }

            if(fileName.endsWith(".xsl")) {
                return XSL
            }

            if(fileName.endsWith(".tex") || fileName.endsWith(".ltx") || fileName.endsWith(".latex")) {
                return LATEX
            }

            if(fileName.endsWith(".jl") || fileName.endsWith(".julia")) {
                return JULIA
            }

            if(fileName.endsWith(".fs") || fileName.endsWith(".fsi")
                || fileName.endsWith(".fsx") || fileName.endsWith(".fsscript")
                || fileName.endsWith(".fsharp")
            ) {
                return FSHARP
            }

            // .mmd extensions from?: https://github.com/Mathpix/mathpix-markdown-it
            // 这.mmd不知道是不是出自：https://github.com/Mathpix/mathpix-markdown-it
            if(fileName.endsWith(".mmd") || fileName.endsWith(".mdm") || fileName.endsWith(".mdmath")) {
                return MARKDOWN_MATH
            }

            if(fileName.endsWith(".rebase") || fileName.endsWith(".git-rebase") || fileName.endsWith(".gitrebase")) {
                return GIT_REBASE
            }

            return NONE
        }

        private fun guessScope(fileName: String) = guessScopeType(fileName).scope

        fun scopeInvalid(
            plScope: String?,
            autoAsInvalid: Boolean = true,
            noneAsInvalid: Boolean = true
        ) :Boolean {
            return plScope == null
                    || (autoAsInvalid && plScope == AUTO.scope)
                    || (noneAsInvalid && plScope == NONE.scope)
                    || plScope.isBlank()
                    || !SCOPES.contains(plScope)
        }

        fun scopeTypeInvalid(
            plScope: PLScope?,
            autoAsInvalid: Boolean = true,
            noneAsInvalid: Boolean = true
        ) = scopeInvalid(plScope?.scope, autoAsInvalid = autoAsInvalid, noneAsInvalid = noneAsInvalid)

        fun isSupportPreview(plScope: PLScope?) = plScope == MARKDOWN


    }
}

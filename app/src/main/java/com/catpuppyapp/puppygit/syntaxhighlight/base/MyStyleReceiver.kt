package com.catpuppyapp.puppygit.syntaxhighlight.base

import com.catpuppyapp.puppygit.utils.MyLog
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.analysis.StyleUpdateRange
import io.github.rosemoe.sora.lang.brackets.BracketsProvider
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer
import io.github.rosemoe.sora.lang.styling.Styles

private const val TAG = "MyStyleReceiver"

abstract class MyStyleReceiver(
    val subClassTag:String,
    val expectedSourceManager: AnalyzeManager?,
): StyleReceiver {
    abstract fun handleStyles(styles: Styles)

    override fun setStyles(sourceManager: AnalyzeManager, styles: Styles?) {
        setStyles(sourceManager, styles, null)
    }

    override fun setStyles(sourceManager: AnalyzeManager, styles: Styles?, action: Runnable?) {
        if(styles == null) {
            return
        }

        if(sourceManager != expectedSourceManager) {
            MyLog.w(TAG, "$subClassTag: sourceManager doesn't match: sourceManager=$sourceManager, expectedSourceManager=$expectedSourceManager")
            return
        }

        handleStyles(styles)
    }


    //received styles is full spans, not a part
    override fun updateStyles(
        sourceManager: AnalyzeManager,
        styles: Styles,
        range: StyleUpdateRange
    ) {
        setStyles(sourceManager, styles)
    }

    override fun setDiagnostics(sourceManager: AnalyzeManager, diagnostics: DiagnosticsContainer?) {

    }

    override fun updateBracketProvider(sourceManager: AnalyzeManager, provider: BracketsProvider?) {

    }
}

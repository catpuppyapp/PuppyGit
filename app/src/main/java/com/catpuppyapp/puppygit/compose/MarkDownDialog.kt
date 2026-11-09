package com.catpuppyapp.puppygit.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FontDownloadOff
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLFont
import com.catpuppyapp.puppygit.syntaxhighlight.markdown.MarkDownSyntaxHighlighter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarkDownDialog(
    title: String = stringResource(R.string.msg),
    text: String,
    previewModeOn: MutableState<Boolean>,
    useSystemFonts: MutableState<Boolean>,
    basePathNoEndSlash:String,
    close: () -> Unit,
    copy: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val highlightedMarkdownText = remember { mutableStateOf<AnnotatedString?>(null) }
    val annotatedText = remember { AnnotatedString(text) }


    val switchBetweenRawAndRenderedContent = { previewModeOn.value = !previewModeOn.value }

    // markdown 是android view的，有bug，切换字体可能不会重新应用新字体，所以用这个变量触发重载
    // 初次加载不会出问题，切换是否使用系统字体后才会，所以这个loading初始值为false
    val loadingForLetMarkDownContainerRefreshAfterSwitchFont = remember { mutableStateOf(false) }
    val switchFonts = {
        useSystemFonts.value = !useSystemFonts.value

        // for markdown container re-render to apply new fonts
        loadingForLetMarkDownContainerRefreshAfterSwitchFont.value = true
        scope.launch {
            delay(50)
            loadingForLetMarkDownContainerRefreshAfterSwitchFont.value = false
        }

        Unit
    }
    val rawOrHighlightedText = { highlightedMarkdownText.value ?: annotatedText}

    val font = remember(useSystemFonts.value) { if(useSystemFonts.value) null else PLFont.codeFont }


    ConfirmDialog3(
        title = title,
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                // use code font for both, if want to view text with system font, can use details dialog instead
                if(previewModeOn.value) {
                    if(loadingForLetMarkDownContainerRefreshAfterSwitchFont.value) {
                        // just temporary display this when switch fonts
                        // 临时在切换字体时占下位置，这个markdown容器切换字体有bug，字体可能不会立刻变，必须先触发移除组件再重新渲染
                        // 这里只是临时过渡，不需要加selection container
                        Text(
                            text = text,
                            fontFamily = font
                        )
                    }else {
                        //这个容器自带选择拷贝，不用套selection container
                        MarkDownContainer(
                            content = text,
                            style = LocalTextStyle.current.copy(fontFamily = font),
                            basePathNoEndSlash = basePathNoEndSlash,
                            // return false to let default link handler take it
                            onLinkClicked = { false },
                        )
                    }
                }else {
                    MySelectionContainer {
                        Text(
                            text = rawOrHighlightedText(),
                            fontFamily = font
                        )
                    }
                }
            }
        },

        customCancel = {
            ScrollableRow {
                IconButton(
                    onClick = switchFonts
                ) {
                    Icon(
                        imageVector = if(useSystemFonts.value) Icons.Filled.FontDownloadOff else Icons.Filled.FontDownload,
                        contentDescription = "switch use system fonts or not"
                    )
                }

                IconButton(
                    onClick = switchBetweenRawAndRenderedContent
                ) {
                    Icon(
                        imageVector = if(previewModeOn.value) Icons.AutoMirrored.Filled.Notes else Icons.Filled.RemoveRedEye,
                        contentDescription = "switch preview mode and text mode"
                    )
                }

                TextButton(
                    onClick = close
                ) {
                    Text(
                        text = stringResource(R.string.close),
                    )
                }
            }

        },
        customOk = {
            TextButton(
                onClick = {
                    copy()
                    close()
                }
            ) {
                Text(
                    text = stringResource(R.string.copy),
                )
            }
        },
        onCancel = close,
    )

    LaunchedEffect(previewModeOn.value) {
        if(!previewModeOn.value && highlightedMarkdownText.value == null) {
            // will do an one time analyze, then deliver styles and release highlighter
            MarkDownSyntaxHighlighter(text) {
                highlightedMarkdownText.value = it
            }.analyze()
        }
    }
}

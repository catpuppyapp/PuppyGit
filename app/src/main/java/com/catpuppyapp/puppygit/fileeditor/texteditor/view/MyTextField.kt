package com.catpuppyapp.puppygit.fileeditor.texteditor.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.MyTextFieldState
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLFont
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.MyLog
import java.util.concurrent.atomic.AtomicBoolean


private const val TAG = "MyTextField"


@Composable
internal fun MyTextField(
    scrollIfInvisible:()->Unit,
    readOnly:Boolean,
    focusThisLine:Boolean,
    textFieldState: MyTextFieldState,
    enabled: Boolean,
    onUpdateText: (TextFieldValue) -> Unit,
    onContainNewLine: (TextFieldValue) -> Unit,
    onFocus: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
//    needShowCursorHandle:MutableState<Boolean>,
    fontSize:Int,
    fontColor:Color,
) {
    val inDarkTheme = Theme.inDarkTheme
    val textStyle = LocalTextStyle.current

    val currentTextField = textFieldState.value.let { remember(it) { mutableStateOf(it) } }
    val focusRequester = remember(focusThisLine) { if(focusThisLine) FocusRequester() else null }
    val alreadyCalledContainsNewLine = remember(focusThisLine) { AtomicBoolean(false) }


    // NOTE: if the `value` is not equals to `BasicTextField` held value, then the ime state will reset
    BasicTextField(
        value = currentTextField.value,
        readOnly = readOnly,
        enabled = enabled,
        onValueChange = ovc@{ newState ->
            val indexOfLineBreak = newState.text.indexOf('\n')
            if (indexOfLineBreak != -1) {
                // make sure only call contains new line once, else maybe cause paste content twice in sometimes
                if(alreadyCalledContainsNewLine.compareAndSet(false, true)) {
                    onContainNewLine(newState)
                }

            } else {
                alreadyCalledContainsNewLine.set(false)

                val lastState = currentTextField.value

                val newState = keepStylesIfPossible(
                    newState,
                    lastState,
                    textChangedCallback = scrollIfInvisible
                )

                currentTextField.value = newState

                onUpdateText(newState)
            }
        },
        //字体样式:字体颜色、字体大小、背景颜色等
        textStyle = textStyle.copy(
            fontSize = fontSize.sp,
            color = fontColor,
//            background = bgColor,
            fontFamily = PLFont.editorCodeFont(),
        ),
        //光标颜色
        cursorBrush = SolidColor(if(inDarkTheme) Color.LightGray else Color.Black),

        modifier = modifier
            .fillMaxWidth()
//            .wrapContentHeight()
            .padding(start = 2.dp)
//            .focusTarget()  //如果加这个，按一次返回会先解除focus，然后才会退出，操作有些繁琐，我感觉不加比较好
            .then(
                if(focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                }else {
                    Modifier
                }
            )
            .onFocusChanged {
                if (it.isFocused) {
                    onFocus(
                        keepStylesIfPossible(
                            newState = currentTextField.value,
                            lastState = textFieldState.value,
                            // here shouldn't call `scrollIfInvisible`,
                            // because here only focus,
                            // doesn't change text, image,
                            // you focused line 100, scroll to check line 1,
                            // then you input "a" or pressed arrow left,
                            // in that case, the text changed, should scroll if target possible,
                            // but at here, nothing changed, so should not call `scrollIfInvisible
                            textChangedCallback = {}
                        )
                    )
                }
            }
    )



    if(focusThisLine) {
        LaunchedEffect(Unit) {
            runCatching {
                focusRequester?.requestFocus()
            }
        }
    }

}

private fun keepStylesIfPossible(
    newState: TextFieldValue,
    lastState: TextFieldValue,
    textChangedCallback: () -> Unit,
) : TextFieldValue {
    try {
        val textChanged = lastState.text.length != newState.text.length || lastState.text != newState.text

        // scroll if invisible
        // when input some chars but target line invisible, will scroll to that line
        if(textChanged) {
            textChangedCallback()
        }else {
            return newState.copy(annotatedString = lastState.annotatedString)
        }


        // text must changed when reached here

        // try keep styles if possible, will use more cpu and memory, but can reduce text flashing
        val newTextLen = newState.text.length
        val validSpans = mutableListOf<AnnotatedString.Range<SpanStyle>>()
        for(it in lastState.annotatedString.spanStyles) {
            // this shouldn't happen
            if(it.start < 0) {
                break
            }

            // this maybe happen
            // the `it.end` is exclusive, so can be equals to length, but can't greater than
            if(it.end > newTextLen) {
                validSpans.add(it.copy(start = it.start, end = newTextLen))

                // already out of range, so break
                break
            }

            validSpans.add(it)
        }

        if(validSpans.isEmpty()) {
            return newState
        }

        // make sure spans covered whole text
        val lastSpanIndex = validSpans.last().end
        if(lastSpanIndex < newTextLen) {
            validSpans.add(AnnotatedString.Range(MyStyleKt.emptySpanStyle, lastSpanIndex, newTextLen))
        }

        val newAnnotatedString = newState.annotatedString
        return newState.copy(
            annotatedString = AnnotatedString(
                text = newAnnotatedString.text,
                spanStyles = validSpans,
                paragraphStyles = newAnnotatedString.paragraphStyles
            )
        )
    }catch (e: Exception) {
        MyLog.e(TAG, "#keepStylesIfPossible err: ${e.localizedMessage}")
        e.printStackTrace()

        return newState
    }
}

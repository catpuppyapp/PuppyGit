package com.catpuppyapp.puppygit.fileeditor.texteditor.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.fileeditor.texteditor.state.TextFieldState
import com.catpuppyapp.puppygit.syntaxhighlight.base.PLFont
import com.catpuppyapp.puppygit.syntaxhighlight.codeeditor.MyCodeEditor
import com.catpuppyapp.puppygit.ui.theme.Theme
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.MyLog

typealias ComposeTextFieldState = androidx.compose.foundation.text.input.TextFieldState
typealias ComposeTextFieldStateSaver = androidx.compose.foundation.text.input.TextFieldState.Saver

private const val TAG = "MyTextField"


@Composable
internal fun MyTextField(
    codeEditor: MyCodeEditor?,
    scrollIfInvisible:()->Unit,
    readOnly:Boolean,
    focusThisLine:Boolean,
    textFieldState: TextFieldState,
    enabled: Boolean,
    onUpdateText: (TextFieldValue, textChanged: Boolean?) -> Unit,
    onContainNewLine: (TextFieldValue) -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier,
    needShowCursorHandle:MutableState<Boolean>,
    fontSize:Int,
    fontColor:Color,
) {
    val focusRequester = remember { FocusRequester() }
    val textStyle = LocalTextStyle.current
    val inDarkTheme = Theme.inDarkTheme

    //写法1
//    val currentTextField = remember { mutableStateOf(textFieldState) }  //重组不会重新执行
//    currentTextField.value = textFieldState  //重组会重新执行，这样通过value就能获取到最新值了（当然，第一次执行这个也会执行，所以和上面的mutableState()加起来等于执行了两次赋值，但state值相同不会触发更新，所以重复执行无所谓

    //写法2，这种写法等同于写法1，之所以使用currentTextFiled的地方能获取到重组后最新的值是因为本质上by是state.value的语法糖，所以使用by后的值等于 获取 state.value ，所以value一旦更新，调用state.value就能获取到最新值
//    val textState = textFieldState.value.let { rememberSaveable(it, saver = ComposeTextFieldStateSaver) { ComposeTextFieldState(it.text, it.selection) } }
    val textState = textFieldState.value.let { remember { mutableStateOf(ComposeTextFieldState(it.text, it.selection)) } }



    fun onValueChange(newState: TextFieldValue) {
        val lastTextField = textFieldState.value


        //存在选中文本时，显示光标拖手和背景颜色（handle）
        needShowCursorHandle.value = newState.selection.start != newState.selection.end

        // no change
        if (lastTextField == newState) {
            return
        }

        // used for some checks
        val textChanged = lastTextField.text.length != newState.text.length || lastTextField.text != newState.text

        // scroll if invisible
        // when input some chars but target line invisible, will scroll to that line
        if(textChanged) {
            scrollIfInvisible()
        }

        if (newState.text.contains('\n')) {
            onContainNewLine(newState)
        } else {
            // only update state when no linebreak,
            //   if has line break, still update, will got unexpected line break,
            //   for avoid it, just wait the text field list updated it, nothing need to do here

            val newState = if(textChanged) {
                newState
            } else {
                // copy to avoid lost highlighting styles when text no changes,
                // if styles still lost, try use `textFieldState.value` as `lastTextField`
                lastTextField.copy(selection = newState.selection)
            }

//            currentTextField.value = newState

            onUpdateText(newState, textChanged)
        }
    }



    // if the `value` is not equals to `BasicTextField` held value, then the ime state will reset
    BasicTextField(
        state = textState.value,
        inputTransformation = InputTransformation {
            val newText = toString()
            if(newText == textFieldState.value.text) {
                onValueChange(textFieldState.value.copy(selection = selection))
            }else {
                onValueChange(TextFieldValue(newText, selection))
            }
        },
        outputTransformation = OutputTransformation {
            val newText = toString()
            // format output
            if(newText.isNotEmpty()) {
                val rangeOfNewText = TextRange(0, newText.length)
                textFieldState.value.annotatedString.spanStyles.forEach {
                    // even change state, still keep styles before new style generated
                    if(rangeOfNewText.contains(TextRange(it.start, it.end - 1))) {
                        addStyle(it.item, it.start, it.end)
                    }
                }
            }
        },
//        value = currentTextField.value,
        readOnly = readOnly,
        enabled = enabled,

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
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    onFocus()
                }
            }
    )



    if(focusThisLine) {
        LaunchedEffect(Unit) {
            runCatching {
                focusRequester.requestFocus()
            }
        }
    }

}




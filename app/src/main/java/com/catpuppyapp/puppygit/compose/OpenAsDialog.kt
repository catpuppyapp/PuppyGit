package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.FsUtils
import com.catpuppyapp.puppygit.utils.Msg
import com.catpuppyapp.puppygit.utils.forEachBetter
import com.catpuppyapp.puppygit.utils.mime.MimeType
import com.catpuppyapp.puppygit.utils.mime.guessFromFileName
import com.catpuppyapp.puppygit.utils.mime.intentType
import java.io.File


/**
 * fileName: 主要是用来检测mime类型的
 * filePath: 用来生成uri
 * showOpenInEditor: 是否显示在内部编辑器打开的选项
 * openInEditor: 执行在内部editor打开的函数
 * openSuccessCallback: 打开成功的回调，不管用内部editor打开还是用外部程序打开，只要打开成功就会调这个回调
 * close: 关闭弹窗
 */
@Composable
fun OpenAsDialog(
    readOnly: MutableState<Boolean>,
    fileName:String,
    filePath:String,
    showOpenInEditor:Boolean=false,
    openInEditor:(expectReadOnly:Boolean)->Unit={},
    openSuccessCallback:()->Unit={},
    close:()->Unit
) {

    val activityContext = LocalContext.current

    val mimeTypeList = remember(fileName) {
        FsUtils.FileMimeTypes.typeList.toMutableList().let {
            //添加一个根据文件名后缀打开的方式，不过可能不准
            //我的app里只允许对文件使用 open as，所以这里的filePath必然是文件（除非有bug），所以这里调用guessFromPath()无需判断路径是否是文件夹，也不需要写若是文件夹则在末尾追加分隔符的逻辑
            it.add(
                FsUtils.FileMimeTypes.MimeTypeAndDescText(
                    MimeType.guessFromFileName(fileName).intentType
                ) { it.getString(R.string.file_open_as_by_extension) }
            )

            it
        }
    }





    val itemHeight = 50.dp
    val mimeTextWeight = FontWeight.Bold
    val mimeTypeFontSize = 12.sp

    PlainDialogWithPadding(
        onClose = close,
        scrollable = true,
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        //文本
        //图像
        //音频
        //视频
        //其他
        //任意
        //根据后缀名检测
        if(showOpenInEditor) {
            Row(modifier = Modifier
                .height(itemHeight)
                .fillMaxWidth()
                .clickable {
                    val expectReadOnly = readOnly.value  //期望的readonly模式，若文件路径不属于app内置禁止编辑的目录，则使用此值作为readonly的初始值
                    openInEditor(expectReadOnly)

                    openSuccessCallback()
                    close()
                }
                ,

                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.open_in_editor), fontWeight = mimeTextWeight)
            }

//                    Spacer(modifier = Modifier.height(10.dp))
            //加这个分割线看着想标题，让人感觉不可点击，不好，所以去掉了
//                    MyHorizontalDivider(color = color)

        }

        Spacer(modifier = Modifier.height(10.dp))

        mimeTypeList.forEachBetter { (mimeType, text) ->
            Row(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .clickable {
                        val openSuccess = FsUtils.openFile(
                            activityContext,
                            File(filePath),
                            mimeType,
                            readOnly.value
                        )

                        if (openSuccess) {
                            openSuccessCallback()
                        } else {
                            Msg.requireShow(activityContext.getString(R.string.open_failed))
                        }

                        close()
                    },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text(activityContext), fontWeight = mimeTextWeight)

                    Text(mimeType, fontSize = mimeTypeFontSize)
                }

            }

            Spacer(modifier = Modifier.height(20.dp))

        }

        Spacer(modifier = Modifier.height(10.dp))

        MyCheckBox(stringResource(R.string.read_only), readOnly)

        Spacer(modifier = Modifier.height(20.dp))

    }
}

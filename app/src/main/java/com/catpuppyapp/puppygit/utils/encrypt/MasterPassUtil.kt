package com.catpuppyapp.puppygit.utils.encrypt

import android.content.Context
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.SettingsUtil
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.HashUtil
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.pref.PrefMan
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64

object MasterPassUtil {
    private const val TAG = "MasterPassUtil"

    /**
     * 若修改主密码，必须先通过encode和decode检测再保存，以免保存后才发现编码解码有问题导致不能用
     */
    fun goodToSave(masterPass: String):Boolean {
        return decode(encode(masterPass)) == masterPass
    }

    private fun encode(masterPass:String):String {
        if(masterPass.isEmpty()) {
            return masterPass
        }

        return masterPass.encodeBase64()
    }

    private fun decode(masterPassEncoded:String):String {
        // base64不包含空格，所以用isBlank()判断也行，但是，正常不会存上blank，若存在，必然有问题，所以这里还是用isEmpty()判断，这样如果值全是空格，decode时就会抛异常
        if(masterPassEncoded.isEmpty()) {
            return masterPassEncoded
        }

        return masterPassEncoded.decodeBase64String()
    }

    /**
     * 保存新的主密码，若为空字符串(isEmpty, not Blank!)则清空。
     * 注：保存前应先调用 goodToSave() 检查下编码是否会出错。
     *
     * @return 返回包含最新密码hash的设置项，由于设置项更新有一定延迟，所以若不在这里返回，可能无法及时更新页面的settings对象
     */
    fun save(appContext: Context = AppModel.realAppContext, newMasterPass: String): AppSettings {
        //存编码后的值
        PrefMan.set(appContext, PrefMan.Key.masterPass, encode(newMasterPass))

        //存hash
        // update master password hash
        val newSettingsWithNewPass = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
            it.masterPasswordHash = if(newMasterPass.isEmpty()) { // 空字符串代表清空了密码，不必算hash
                newMasterPass
            }else {  // 非空字符串，计算hash
                HashUtil.hash(newMasterPass)
            }
        }!!

        // update in-memory master password
        AppModel.masterPassword.value = newMasterPass

        return newSettingsWithNewPass
    }

    fun get(appContext: Context):String {
        try {
            val masterPassEncoded = PrefMan.get(appContext, PrefMan.Key.masterPass, "")
            return decode(masterPassEncoded)
        }catch (e:Exception) {
            //解码失败才会抛异常，所以应该不会打印用户的原始密码（因为解码失败了，并没获取到原始密码），不过没严格测试
            MyLog.e(TAG, "get() failed: ${e.stackTraceToString()}")
            return ""
        }
    }

    /**
     * 忘记密码时用来清除主密码，后果是用旧密码加密的凭据密码不再可用，直到更新后用新密码重新加密
     */
    fun clear(appContext: Context = AppModel.realAppContext) {
        save(appContext, "")
    }

}

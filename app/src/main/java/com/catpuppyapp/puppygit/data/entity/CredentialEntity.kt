package com.catpuppyapp.puppygit.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.catpuppyapp.puppygit.constants.Cons
import com.catpuppyapp.puppygit.data.entity.common.BaseFields
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.encrypt.PassEncryptHelper
import com.catpuppyapp.puppygit.utils.getShortUUID

@Entity(tableName = "credential")
data class CredentialEntity(
    @PrimaryKey
    var id: String = getShortUUID(),
    var name:String = "",  //字段需唯一
//    var testMigra:String="",

    // repo push or pull etc time
    var value: String = "",  // username or private key
    var pass: String = "",  // password or passphrase_for_ssh

    // credential bind a type such a bad design, credential should define by it's proposal, not by itself, example: when used for https link, its a http type, when used for ssh, its a ssh type
    //计划取消凭据类型，未来将根据使用凭据的git url来判断是创建http凭据还是ssh凭据
    @Deprecated("planning to deprecate type of credential, in future credential will no type defined in itself, it will defined by it's proposal, e.g. if you use same credential to http and ssh respectively, it's value and pass will treat as username+password and privateKey+passphrase respectively, and which type of credential will be create by libgit2, it's will depending by type of git url")
    var type: Int= Cons.dbCredentialTypeHttp,  //"ssh" or "http"

    //新凭据默认使用最新版本的加密器
    var encryptVer:Int = PassEncryptHelper.passEncryptCurrentVer,

    @Embedded
    var baseFields: BaseFields = BaseFields(),

){

//    fun getTypeStr(searchable:Boolean):String {
//        return if(searchable){
//            if(type == Cons.dbCredentialTypeHttp) Cons.httpStr else Cons.sshStr
//        } else {
//            val activityContext = AppModel.activityContext
//            activityContext.getString(if(type == Cons.dbCredentialTypeHttp) R.string.http_https else R.string.ssh)
//        }
//    }

    fun maybeIsValid() = value.isNotEmpty() || pass.isNotEmpty();

}

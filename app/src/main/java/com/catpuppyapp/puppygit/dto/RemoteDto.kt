package com.catpuppyapp.puppygit.dto

import androidx.room.Ignore
import com.catpuppyapp.puppygit.constants.Cons

class RemoteDto {
    var remoteId=""
    var remoteName=""
    var remoteUrl=""

    /**
     * NOTE:
     * the `credentialId` and `pushCredentialId` maybe is match by domain, in that case, the `actuallyCredentialIdWhenCredentialIdIsMatchByDomain` and `actuallyPushCredentialIdWhenCredentialIdIsMatchByDomain`
     *  will save the actually using id, and the crenentialName/Pass/Type 's value is related about the actually credential/pushCredential id
     * if credentialId is match by domain, this will replace to the value of `actuallyCredentialIdWhenCredentialIdIsMatchByDomain` related credential
     */
    var credentialId:String?=""  // this id maybe match by domain,
    var credentialName:String?=""
    var credentialVal:String?=""
    var credentialPass:String?=""  // the pass is encrypted, will decrypt when using
    var credentialType:Int = Cons.dbCredentialTypeHttp
    var pushUrl=""
    var pushCredentialId:String?=""   // this id maybe match by domain,
    var pushCredentialName:String?=""
    var pushCredentialVal:String?=""
    var pushCredentialPass:String?=""  // the pass is encrypted, will decrypt when using
    var pushCredentialType:Int = Cons.dbCredentialTypeHttp

    var repoId=""
    var repoName=""

    @Ignore
    var actuallyCredentialIdWhenCredentialIdIsMatchByDomain:String=""
    @Ignore
    var actuallyPushCredentialIdWhenCredentialIdIsMatchByDomain:String=""

    //Ignore的作用是从数据库查dto的时候忽略此字段
    @Ignore
    var branchMode:Int= Cons.dbRemote_Fetch_BranchMode_All
    @Ignore
    var branchListForFetch:List<String> = listOf()


    /**
     * when push url empty, will use fetch url, that case should set this value to true*
     */
    @Ignore
    var pushUrlTrackFetchUrl:Boolean = false


    fun getLinkedFetchCredentialName():String {
        return getLinkedFetchOrPushCredentialName(true)
    }

    fun getLinkedPushCredentialName():String {
        return getLinkedFetchOrPushCredentialName(false)
    }

    private fun getLinkedFetchOrPushCredentialName(isFetch:Boolean):String {
        return getCredentialName(isFetch, credentialId, credentialName, pushCredentialId, pushCredentialName)
    }
}

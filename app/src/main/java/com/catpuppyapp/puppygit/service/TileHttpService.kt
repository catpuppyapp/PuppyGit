package com.catpuppyapp.puppygit.service

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.quicksettings.Tile
import androidx.core.content.ContextCompat
import com.catpuppyapp.puppygit.constants.IntentCons
import com.catpuppyapp.puppygit.base.BaseTileService
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.receiverFlags

private const val TAG = "TileHttpService"


// 注意: TileService没重写 onCreate 也没执行 AppMode 的init系列函数，但不会出问题，因为onClick被触发时，会启动HttpService，其内部有执行初始化
// note: tile service haven't overwrite onCreate and execute `AppMode.init_` serial functions,
//   but is ok, cause HttpService will do init when Tile service onClick triggered
@TargetApi(Build.VERSION_CODES.N)  // tile support by android 24 and above
class TileHttpService: BaseTileService() {
    companion object {
        //加个包名，避免冲突
        val ACTION_UPDATE = IntentCons.Action.UPDATE_TILE
        const val INTENT_EXTRA_KEY_NEW_STATE = IntentCons.ExtrasKey.newState

        fun sendUpdateTileRequest(appContext: Context, newState:Boolean) {
            val intent = Intent(ACTION_UPDATE)
            intent.putExtra(INTENT_EXTRA_KEY_NEW_STATE, newState)
            // 发送广播通知 TileService 更新状态
            appContext.sendBroadcast(intent)
        }
    }

    
    private val updateTileReceiver = object : BroadcastReceiver() {
        //这个只有一个action，而且靠receiver过滤掉了其他intent，所以不用判断action，来消息就更新，如果之后添加更多不同类型的请求，换成extra传参
        override fun onReceive(context: Context?, intent: Intent?) {
            val newState = intent?.extras?.getBoolean(INTENT_EXTRA_KEY_NEW_STATE)
            MyLog.d(TAG, "#updateTileReceiver.updateTileReceiver(): received action: ${intent?.action}, newState=$newState")
            //如果携带了参数，更新状态，否则无视
            if(newState != null) {
                updateState(newState)
            }
        }
    }

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()

        updateState(HttpService.isRunning())
    }

    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
        try {
            ContextCompat.registerReceiver(applicationContext, updateTileReceiver, IntentFilter(ACTION_UPDATE), receiverFlags())
        }catch (e:Exception) {
            MyLog.e(TAG, "#onStartListening: ${e.stackTraceToString()}")
        }
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
        try {
            //有时候会报错，必须try catch
            unregisterReceiver(updateTileReceiver)
        }catch (e:Exception) {
            MyLog.e(TAG, "#onStopListening: ${e.stackTraceToString()}")
        }
    }

    // Called when the user taps on your tile in an active or inactive state.
    override fun onClick() {
        super.onClick()
        if(qsTile.state == Tile.STATE_ACTIVE) {
            HttpService.stop(applicationContext)
        }else {
            HttpService.start(applicationContext)
        }
    }
//
//    // Called when the user removes your tile.
//    override fun onTileRemoved() {
//        super.onTileRemoved()
//    }


    private fun updateState(newState:Boolean) {
        qsTile?.state = if (newState) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }

        qsTile?.updateTile()
    }

}

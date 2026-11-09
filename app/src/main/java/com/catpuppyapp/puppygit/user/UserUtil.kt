package com.catpuppyapp.puppygit.user

import androidx.compose.runtime.MutableState
import com.catpuppyapp.puppygit.utils.MyLog
import com.catpuppyapp.puppygit.utils.doJobThenOffLoading
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

object UserUtil {
    private val TAG = "UserUtil"
    private var user = User()

    val updateUserLock = Mutex()
    private const val channelBufferSize = 50
    private val updateUserChannel = Channel<User>(capacity = channelBufferSize)
    private val updateUserJobStarted = AtomicBoolean(false)

    fun init() {
        //TODO 查询用户授权状态，是否买了pro版之类的信息，异步的，在这查不了
//            singleInstance = UserInfo(isPro = false)
        if(!updateUserJobStarted.get()){
            updateUserJobStarted.compareAndSet(false, true)
            startUpdateUserJob()
        }
    }

    //这个函数的意义在于，如果外部调用update修改了即将更新的user对象的isProState=mutableStateOf(xxx)，那最终调用这个函数更新时依然保持原来的state不变，确保使用state的变量能获取到最新的状态。
    //反之，如果调用者本来就是用 userWillUpdate.xxxState.value =newValue 的形式更新user对象，则此函数就变得没意义了
    private fun startUpdateUserJob() {
        val logTag = "updateUserJob"
        doJobThenOffLoading {
            var errCountLimit = 3

            while (errCountLimit > 0) {
                try {
                    updateUserLock.withLock {

                        //接收新的用户信息
                        var newUser = updateUserChannel.receive()

                        //尝试取出最多n个候选对象，这样做的目的是当同一时间有多个待写入任务时，直接取最后一个请求者，避免中间的写入，减少io，但也不能一直不写，所以设置一个限制，超过这个限制，则写入一次，然后重新接收
                        var count = 0
                        //尝试获取队列后面的条目。
                        //循环实际终止条件为：候选条目大于限制大小 或 无候选条目
                        //如果用我这种做法，在receive和tryReceive之间要么间隔长，要么队列有缓冲，否则意义不大
                        while (count++ < channelBufferSize) {
                            val result = updateUserChannel.tryReceive()
                            if (result.isSuccess) {
                                newUser = result.getOrElse { newUser }
                            } else {  //无候选设置项，终止（所谓的“无候选”，直白说就是接收到设置项后，队列里没后续条目也没人再发新的设置项过来）
                                break
                            }
                        }

                        //更新类变量。注意：不要给isProState赋值
                        user.isProState.value = newUser.isProState.value

                    }

                } catch (e: Exception) {
                    errCountLimit--
                    MyLog.e(TAG, "$logTag: update user err: ${e.stackTraceToString()}")
                }
            }

            updateUserChannel.close()
        }
    }

    fun getUserSnapshot(u:User= user):User {
        return u.copy()
    }

    //也可这样写：modifyUser:User.()->Unit ，限定函数作用域为传给modifyUser的入参user，函数里用this指代当前user实例，相当于给传入的函数Bind了user实例作为this
    //needUpdateUserByLock若为true，百分百不出错，但如果调用者正确更新状态，则没必要执行updateUserByLock
    fun updateUser(requireReturnChangedUser:Boolean = false, needUpdateUserByLock:Boolean=true, modifyUser:(User)->Unit):User? {
        //其实就算用channel也没办法保证完全有序，如果modifyUser流程时间长，但先发起，那后面如果来个流程短的一样会抢先，在有序保存方面只是比用互斥锁稍微好一点点而已
        val userForUpdate = getUserSnapshot()
        modifyUser(userForUpdate)

        //如果调用者更新的本来就是state的值，就不需要再更新UserUtil的user了，但如果调用者给state变量重新赋值，例如xxxState=mutableStateOf(xxx)，那就得更新下user，不然页面获取不到最新状态
        if(needUpdateUserByLock) {
            doJobThenOffLoading {
                updateUserChannel.send(userForUpdate)
            }
        }

        return if(requireReturnChangedUser) getUserSnapshot(userForUpdate) else null
    }


    /**
     * isPro的使用方法：
     * 1 在MainCompose里创建rememberSaveable的isPro状态变量，然后调用UserInfo.updateIsProState()把状态赋值UserInfo实例
     * 2 在需要获取isPro的地方调用UserInfo.isPro()；在需要修改的地方调用 setIsPro()。
     * 3 有待补充
     */


    //以后可能会修改是否pro的逻辑，所以用方法，这样以后好改
    fun isPro(): Boolean {
//            return false
//        if (dev_ProModeOn) {
//            return true
//        }

        // pro版，直接永久返回true就行了
        return true

//        return user.isProState.value
    }


    /**
        此方法应只在MainCompose里调用一次
     *  期望的入参是由rememberXXXX系列方法生成的user的各种state变量
     */
    fun updateUserStateToRememberXXXForPage(
        newIsProState: MutableState<Boolean>
    ) {
        user.isProState = newIsProState
    }

    //应只在MainCompose里执行一次此函数，然后所有地方都用同一个rememberSaveable变量
//        @Composable
//        fun rememberUserIsPro(userInfo: UserInfo = singleInstance):MutableState<Boolean> {
//            return rememberSaveable {
//                mutableStateOf(false)
//            }
//        }


}

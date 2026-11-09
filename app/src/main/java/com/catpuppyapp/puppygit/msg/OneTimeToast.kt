package com.catpuppyapp.puppygit.msg

import androidx.room.concurrent.AtomicBoolean
import com.catpuppyapp.puppygit.utils.Msg

// if need more other class, create an interface
class OneTimeToast {
    private val showed = AtomicBoolean(false)

    fun show(msg:String) {
        if(showed.compareAndSet(false, true)) {
            Msg.requireShowLongDuration(msg)
        }
    }

    fun reset() {
        showed.set(false)
    }
}

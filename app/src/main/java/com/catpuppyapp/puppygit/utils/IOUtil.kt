package com.catpuppyapp.puppygit.utils

import java.io.IOException
import java.io.InputStream
import java.util.Objects

object IOUtil {
    const val ioBufSize = 8192; // 8192 bytes, io byte array size


    fun createByteBuffer():ByteArray {
        return ByteArray(ioBufSize)
    }

    //直接拷的安卓库里的readNBytes()，虽然java11貌似就有这个api了，
    // 但安卓sdk 33（对应安卓系统版本13）才添加这个api，所以得手动实现一下以确保兼容性，
    // 另外jgit在安卓13以下不能用也和这个api有关，不过应该也存在其他不兼容的api，
    // 这这样是因为安卓的java实现并不完全支持所有的原生java实现，尤其是java新版本添加的api，安卓可能要过几年才会支持
    /**
     * 这个函数和 inputStream.read(b, off, len) 的区别在于：在没读到eof的情况下，如果没读够指定大小，这个函数会一直读，读到满足为止。
     */
    @Throws(IOException::class)
    fun readNBytes(inputStream: InputStream, b: ByteArray, off: Int, len: Int): Int {
        Objects.checkFromIndexSize(off, len, b.size)

        var n = 0
        while (n < len) {
            val count: Int = inputStream.read(b, off + n, len - n)
            if (count < 0) break
            n += count
        }
        return n
    }

    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream, b: ByteArray): Int {
        return readNBytes(inputStream, b, 0, b.size)
    }

    /**
     * @param len 注意是len不是end index，换算公式为：
     * len = endIndex - startIndex + 1 = endIndex - (startIndex - 1)：
     * endIndex = len + (startIndex-1) = len + startIndex - 1
     * 例如：
     * 要比较 索引0 到 索引8，则len应为 8-0+1=9；
     * 当startIndex为2，len为3时，endIndex应为 3+2-1 = 4，这时目标元素索引即 2、3、4，正好3个索引，与len相等
     */
    fun bytesAreEquals(left:ByteArray, right:ByteArray, startIndex:Int, len: Int) :Boolean {
        //或者直接添加个记数，从0开始，等于len就break，但那样需要多加个变量
        //若len和startIndex有误，endIndex则会无效，这时是调用者的问题，与函数本身无关
//        val endIndex = startIndex+len-1  // endIndex = len + (startIndex-1) = len + startIndex -1

        for (idx in startIndex..getEndIndex(startIndex, len)) {
            if(left[idx] != right[idx]) {
                return false
            }
        }

        return true
    }

    fun bytesAreNotEquals(left:ByteArray, right:ByteArray, startIndex:Int, len: Int) :Boolean {
        return !bytesAreEquals(left, right, startIndex, len)
    }

    fun getEndIndex(startIndex:Int, len:Int):Int {
        return startIndex + len - 1
    }

    fun getLen(startIndex:Int, endIndex:Int):Int {
        return endIndex - startIndex + 1
    }
}

package cn.leo.pagingktx.utils

import kotlin.jvm.Throws

/**
 * @author : leo
 * @date : 2019/4/18
 */
object HexUtil {
    private val DIGITS_LOWER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val DIGITS_UPPER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    /**
     * 16进制转byte数组
     * @param data 16进制字符串
     * @return byte数组
     */
    @Throws(Exception::class)
    fun hex2Bytes(data: String): ByteArray {
        val len = data.length

        if (len and 0x01 != 0) {
            throw Exception("Odd number of characters.")
        }

        val out = ByteArray(len shr 1)

        // two characters form the hex value.
        var i = 0
        var j = 0
        while (j < len) {
            var f = toDigit(data[j], j) shl 4
            j++
            f = f or toDigit(data[j], j)
            j++
            out[i] = (f and 0xFF).toByte()
            i++
        }
        return out
    }

    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @param toLowerCase 是否小写
     * @return 转化结果
     */
    @JvmOverloads
    fun bytes2Hex(data: ByteArray, toLowerCase: Boolean = true): String {
        return bytes2Hex(
            data,
            if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER
        )
    }


    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @param toDigits DIGITS_LOWER或DIGITS_UPPER
     * @return 转化结果
     */
    private fun bytes2Hex(data: ByteArray, toDigits: CharArray): String {
        val l = data.size
        val out = CharArray(l shl 1)
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (i < l) {
            out[j++] = toDigits[(0xF0 and data[i].toInt()).ushr(4)]
            out[j++] = toDigits[0x0F and data[i].toInt()]
            i++
        }
        return String(out)
    }

    /**
     * 16转化为数字
     * @param ch 16进制
     * @param index 索引
     * @return 转化结果
     */
    @Throws(Exception::class)
    private fun toDigit(ch: Char, index: Int): Int {
        val digit = Character.digit(ch, 16)
        if (digit == -1) {
            throw Exception(
                "Illegal hexadecimal character " + ch
                        + " at index " + index
            )
        }
        return digit
    }
}

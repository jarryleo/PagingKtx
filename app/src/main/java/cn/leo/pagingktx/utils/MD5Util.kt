package cn.leo.retrofitktx.utils


import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author : leo
 * @date : 2019/4/18
 */
object MD5Util {
    /**
     * 获得字符串的md5值
     *
     * @param str 待加密的字符串
     * @return md5加密后的字符串
     */
    fun getMD5String(str: String): String {
        var bytes: ByteArray? = null
        try {
            val md5 = MessageDigest.getInstance("MD5")
            bytes = md5.digest(str.toByteArray(charset("UTF-8")))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return HexUtil.bytes2Hex(bytes!!)

    }


    /**
     * 获得字符串的md5大写值
     *
     * @param str 待加密的字符串
     * @return md5加密后的大写字符串
     */
    fun getMD5UpperString(str: String): String {
        return getMD5String(str).toUpperCase()
    }

    /**
     * 获得文件的md5值
     *
     * @param file 文件对象
     * @return 文件的md5
     */
    fun getFileMD5String(file: File): String {
        var ret = ""
        var `in`: FileInputStream? = null
        var ch: FileChannel? = null
        try {
            `in` = FileInputStream(file)
            ch = `in`.channel
            val byteBuffer = ch!!.map(
                FileChannel.MapMode.READ_ONLY, 0,
                file.length()
            )
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(byteBuffer)
            ret = HexUtil.bytes2Hex(md5.digest())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (ch != null) {
                try {
                    ch.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return ret
    }

    /**
     * 获得文件md5值大写字符串
     *
     * @param file 文件对象
     * @return 文件md5大写字符串
     */
    fun getFileMD5UpperString(file: File): String {
        return getFileMD5String(file).toUpperCase()
    }

    /**
     * 校验文件的md5值
     *
     * @param file 目标文件
     * @param md5  基准md5
     * @return 校验结果
     */
    fun checkFileMD5(file: File, md5: String): Boolean {
        return getFileMD5String(file).equals(md5, ignoreCase = true)
    }

    /**
     * 校验字符串的md5值
     *
     * @param str 目标字符串
     * @param md5 基准md5
     * @return 校验结果
     */
    fun checkMD5(str: String, md5: String): Boolean {
        return getMD5String(str).equals(md5, ignoreCase = true)
    }

    /**
     * 获得加盐md5，算法过程是原字符串md5后连接加盐字符串后再进行md5
     *
     * @param str  待加密的字符串
     * @param salt 盐
     * @return 加盐md5
     */
    fun getMD5AndSalt(str: String, salt: String): String {
        return getMD5String(getMD5String(str) + salt)
    }
    /*private static String bytesToHex(byte[] bytes) {
        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                result.append("0");
            }
            result.append(hex);
        }
        return result.toString();
    }*/
}

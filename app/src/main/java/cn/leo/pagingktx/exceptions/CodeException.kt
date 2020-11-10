package cn.leo.retrofitktx.exceptions

import androidx.annotation.IntDef

/**
 * @author : leo
 * @date : 2019-04-26
 */
object CodeException {
    /*网络错误*/
    const val NETWORK_ERROR = 0x1
    /*http_错误*/
    const val HTTP_ERROR = 0x2
    /*json错误*/
    const val JSON_ERROR = 0x3
    /*未知错误*/
    const val UNKNOWN_ERROR = 0x4
    /*运行时异常-包含自定义异常*/
    const val RUNTIME_ERROR = 0x5
    /*无法解析该域名*/
    const val UNKOWN_HOST_ERROR = 0x6


    @IntDef(NETWORK_ERROR, HTTP_ERROR, RUNTIME_ERROR, UNKNOWN_ERROR, JSON_ERROR, UNKOWN_HOST_ERROR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CodeEp
}

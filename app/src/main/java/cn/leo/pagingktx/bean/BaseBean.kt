package cn.leo.retrofitktx.bean

/**
 * @author : leo
 * @date : 2019-08-30
 */
open class BaseBean<T>(
    var errcode: Int = 0,
    var errmsg: String,
    val data: T
)
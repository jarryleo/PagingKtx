package cn.leo.retrofitktx.exceptions


/**
 * @author : ling luo
 * @date : 2019-04-26
 */
class ApiException : Exception {
    /*错误码*/
    @get:CodeException.CodeEp
    var code: Int = 0
    /*显示的信息*/
    var msg: String? = null

    constructor(e: Throwable) : super(e) {
        msg = e.message
    }

    constructor(cause: Throwable, @CodeException.CodeEp code: Int, showMsg: String)
            : super(showMsg, cause) {
        this.code = code
        msg = showMsg
    }
}

package cn.leo.retrofitktx.exceptions

/**
 * @author : ling luo
 * @date : 2019-04-26
 * 业务异常
 */
class BusinessException : Exception {
    var code: Int = 0
    var msg: String = ""

    constructor(code: Int) : super() {
        this.code = code
    }

    constructor(msg: String) : super(msg) {
        this.msg = msg
    }

    constructor(code: Int, msg: String)
            : super(msg) {
        this.code = code
        this.msg = msg
    }
}

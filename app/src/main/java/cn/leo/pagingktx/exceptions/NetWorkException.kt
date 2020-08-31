package cn.leo.retrofitktx.exceptions

/**
 * @author : ling luo
 * @date : 2019-04-26
 */
class NetWorkException(showMsg: String) : Exception(showMsg) {
    /*错误码*/
    @get:CodeException.CodeEp
    var code = CodeException.NETWORK_ERROR
    /*显示的信息*/
    var displayMessage: String? = null


    init {
        code = CodeException.NETWORK_ERROR
        displayMessage = showMsg
    }
}

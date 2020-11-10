package cn.leo.retrofitktx.exceptions

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author : leo
 * @date : 2019-04-26
 */
object FactoryException {

    /**
     * 解析异常
     *
     * @param e
     * @return
     */
    fun analysisException(e: Throwable): ApiException {
        val apiException = ApiException(e)
        if (e is NetWorkException) {
            /*网络异常*/
            apiException.code = e.code
            apiException.msg = e.displayMessage
        } else if (e is HttpException) {
            /*网络异常*/
            apiException.code = CodeException.HTTP_ERROR
            apiException.msg = e.message()
        } else if (e is HttpTimeException) {
            /*自定义运行时异常*/
            apiException.code = CodeException.RUNTIME_ERROR
            apiException.msg = e.message
        } else if (e is ConnectException || e is SocketTimeoutException) {
            /*链接异常*/
            apiException.code = CodeException.HTTP_ERROR
            apiException.msg = e.message
        } else if (e is JsonParseException || e is JSONException || e is ParseException) {
            /*json解析异常*/
            apiException.code = CodeException.JSON_ERROR
            apiException.msg = e.message
        } else if (e is BusinessException) {
            /*业务异常（服务器返回的请求失败信息）*/
            apiException.code = e.code
            apiException.msg = e.msg
        } else if (e is UnknownHostException) {
            /*无法解析该域名异常*/
            apiException.code = CodeException.UNKOWN_HOST_ERROR
            apiException.msg = e.message
        } else {
            /*未知异常*/
            apiException.code = CodeException.UNKNOWN_ERROR
            apiException.msg = e.message
        }
        return apiException
    }
}

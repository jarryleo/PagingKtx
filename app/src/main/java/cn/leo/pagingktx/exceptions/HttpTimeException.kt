package cn.leo.retrofitktx.exceptions

/**
 * @author : ling luo
 * @date : 2019-04-26
 */
class HttpTimeException(detailMessage: String) : RuntimeException(detailMessage) {


    constructor(resultCode: Int) : this(getApiExceptionMessage(resultCode))

    companion object {
        /*未知错误*/
        val UNKOWN_ERROR = 0x1002
        /*本地无缓存错误*/
        val NO_CHACHE_ERROR = 0x1003
        /*缓存过时错误*/
        val CHACHE_TIMEOUT_ERROR = 0x1004

        /**
         * 转换错误数据
         *
         * @param code
         * @return
         */
        private fun getApiExceptionMessage(code: Int): String {
            return when (code) {
                UNKOWN_ERROR -> "错误：网络错误"
                NO_CHACHE_ERROR -> "错误：无缓存数据"
                CHACHE_TIMEOUT_ERROR -> "错误：缓存数据过期"
                else -> "错误：未知错误"
            }
        }
    }
}

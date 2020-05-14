package cn.leo.retrofit_ktx.http

import cn.leo.retrofit_ktx.view_model.KLiveData

/**
 * @author : ling luo
 * @date : 2019-09-17
 */

open class KInterceptor {
    var priority: Int = 0
    /**
     * @return 返回true 拦截
     */
    open fun <T> intercept(extra: Any? = null, data: T, liveData: KLiveData<T>): Boolean {
        return false
    }
}
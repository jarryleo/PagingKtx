package cn.leo.retrofit_ktx.http

import java.util.*
import kotlin.collections.ArrayList


/**
 * @author : ling luo
 * @date : 2019-09-17
 */
object KInterceptorManager {
    val interceptors: ArrayList<KInterceptor> = ArrayList()

    /**
     * 加载全局拦截器
     * @param priority 优先级，越大越优先
     */
    fun addInterceptor(interceptor: KInterceptor, priority: Int = 0) {
        interceptor.priority = priority
        interceptors.add(interceptor)
        interceptors.sortWith(Comparator { o1, o2 ->
            o2.priority - o1.priority
        })
    }
}
package cn.leo.pagingktx.model

import cn.leo.pagingktx.net.Apis
import cn.leo.pagingktx.net.LoggerInterceptor
import cn.leo.pagingktx.net.Urls
import cn.leo.retrofit_ktx.http.OkHttp3Creator
import cn.leo.retrofit_ktx.http.create
import cn.leo.retrofit_ktx.view_model.KNetViewModel

/**
 * @author : ling luo
 * @date : 2020/5/12
 */
open class BaseViewModel : KNetViewModel<Apis>() {
    override fun getBaseUrl() = Urls.baseUrlZhiHu

    override fun createApi(): Apis {
        return Apis::class.java.create {
            baseUrl = getBaseUrl()
            httpClient = OkHttp3Creator.build {
                //网络请求日志打印拦截器
                interceptors.add(LoggerInterceptor())
            }
        }
    }
}
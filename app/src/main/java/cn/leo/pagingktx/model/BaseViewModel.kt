package cn.leo.pagingktx.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.leo.pagingktx.App
import cn.leo.pagingktx.net.Apis
import cn.leo.pagingktx.net.Urls
import cn.leo.retrofit_ktx.http.OkHttp3Creator
import cn.leo.retrofit_ktx.http.ServiceCreator
import cn.leo.retrofitktx.interceptor.CacheInterceptor
import cn.leo.pagingktx.net.interceptor.LoggerInterceptor

/**
 * @author : leo
 * @date : 2020/4/29
 */
open class BaseViewModel : ViewModel() {
    val loadingLiveData = MutableLiveData<Boolean>()

    companion object {
        val api by lazy {
            ServiceCreator.create(Apis::class.java) {
                baseUrl = Urls.baseUrlZhiHu
                httpClient = OkHttp3Creator.build {
                    //缓存文件夹
                    cacheDir = App.context?.cacheDir
                    //网络请求日志打印拦截器
                    addInterceptor(LoggerInterceptor())
                    //接口缓存拦截器
                    addInterceptor(CacheInterceptor())
                }
            }
        }
    }
}
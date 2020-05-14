package cn.leo.retrofit_ktx.view_model

import androidx.lifecycle.viewModelScope
import cn.leo.retrofit_ktx.http.KInterceptorManager
import cn.leo.retrofit_ktx.http.KJob
import kotlinx.coroutines.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author : ling luo
 * @date : 2019-12-12
 */
class ViewModelCoroutineHelper : ReadOnlyProperty<KViewModel, ViewModelCoroutineHelper> {

    private lateinit var model: KViewModel

    override fun getValue(
        thisRef: KViewModel,
        property: KProperty<*>
    ): ViewModelCoroutineHelper {
        model = thisRef
        return this
    }

    /**
     * 协程执行网络请求，并把结果给上层的LiveData
     */
    fun <R> executeRequest(
        deferred: Deferred<R>,
        liveData: KLiveData<R>,
        extra: Any? = null
    ): Job = model.viewModelScope.launch(
        model.viewModelScope.coroutineContext + Dispatchers.IO
    ) {
        try {
            liveData.showLoading()
            val result = deferred.await()
            KInterceptorManager.interceptors.forEach {
                if (it.intercept(extra, result, liveData)) {
                    return@launch
                }
            }
            liveData.success(result, extra)
        } catch (e: CancellationException) {
            //取消请求
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
            liveData.failed(e, extra)
        }
    }

    /**
     * 异步方法 回调在主线程
     */
    fun <R> async(block: suspend CoroutineScope.() -> R): KJob<R> {
        val liveData = model.getLiveData<R>()
        //协程包装(异步)
        val deferred = model.viewModelScope.async(
            model.viewModelScope.coroutineContext + Dispatchers.IO
        ) { block() }
        //执行结果
        val job = model.viewModelScope.launch {
            try {
                liveData.showLoading()
                val value = deferred.await()
                if (value != null) {
                    liveData.success(value)
                } else {
                    liveData.failed(NullPointerException("null"))
                }
            } catch (e: CancellationException) {
                //取消请求
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                liveData.failed(e)
            }
        }
        return KJob(job)
    }
}
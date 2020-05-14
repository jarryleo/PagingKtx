package cn.leo.retrofit_ktx.view_model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.leo.retrofit_ktx.http.KJob
import kotlinx.coroutines.*
import kotlin.reflect.KFunction

/**
 * @author : ling luo
 * @date : 2019-07-03
 *
 * ViewModel 生命周期比 Activity 长，不应该持有任何view引用或者包含view的引用，
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class KViewModel : ViewModel() {

    /**
     * liveData帮助类
     */
    internal val mLiveDataHelper by lazy { KLiveDataHelper() }

    /**
     * 协程帮助类
     */
    internal val mCoroutineHelper by ViewModelCoroutineHelper()

    /**
     * 释放资源
     */
    override fun onCleared() {
        mLiveDataHelper.clear()
    }

    /**
     * 协程执行网络请求，并把结果给上层的LiveData
     */
    fun <R> executeRequest(
        deferred: Deferred<R>,
        liveData: KLiveData<R>,
        extra: Any? = null
    ): Job = mCoroutineHelper.executeRequest(deferred, liveData, extra)

    /**
     * 异步方法 回调在主线程
     */
    fun <R> async(block: suspend CoroutineScope.() -> R): KJob<R> = mCoroutineHelper.async(block)

    /**
     * 获取liveData
     */
    fun <R> getLiveData(key: String? = null): KLiveData<R> {
        //获取当前方法名称
        val methodName = key ?: Thread.currentThread()
            .stackTrace
            .find {
                it.className == this::class.java.name
            }?.methodName ?: "no-name"
        return mLiveDataHelper.getLiveData(methodName)
    }

    /**
     * 订阅方法回调(本地方法和网络请求)
     * @param kFunction 参数写法 model::test
     */
    fun <R> observe(
        lifecycleOwner: LifecycleOwner,
        kFunction: KFunction<KJob<R>>,
        result: (Result<R>).() -> Unit = {}
    ) = getLiveData<R>(kFunction.name).observe(lifecycleOwner, result)

    /**
     * 订阅方法，正确结果直接传递
     * @param viewModelSupport 写法 model::setTitle + ::setActionBarTitle
     * 前面为订阅方法，后面为回调方法
     * 前面的方法必须是本model的方法，后面的方法参数必须是前面方法的返回值
     */
    fun <R> observe(
        lifecycleOwner: LifecycleOwner,
        viewModelSupport: ViewModelSupport<R>
    ) {
        observe(lifecycleOwner, viewModelSupport.modelFuc) {
            success { viewModelSupport.obFunc(it) }
        }
    }

    /**
     * 无生命周期的监听，谨慎使用，防止泄露
     * @param kFunction 参数写法 Api::test
     */
    fun <R> observeForever(
        kFunction: KFunction<KJob<R>>,
        result: (Result<R>).() -> Unit = {}
    ) = getLiveData<R>(kFunction.name).observeForever(result)


    /**
     * 订阅一次
     * @param kFunction 参数写法 Api::test
     */
    fun <R> observeOnce(
        kFunction: KFunction<KJob<R>>,
        result: (Result<R>).() -> Unit = {}
    ) = viewModelScope.launch(context = Dispatchers.Main) {
        getLiveData<R>(kFunction.name).observeOnce(result)
    }
}
package cn.leo.retrofit_ktx.view_model

import cn.leo.retrofit_ktx.http.KJob
import kotlinx.coroutines.Deferred
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author : ling luo
 * @date : 2019-11-15
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class ViewModelApiHelper<T : Any> :
    ReadOnlyProperty<KNetViewModel<*>, ViewModelApiHelper<T>> {

    /**
     * viewModel
     */
    private lateinit var model: KNetViewModel<*>


    //请求代理相关
    private var mApiProxy: T? = null
    private var mApiHandler: InvocationHandler? = null
    //请求携带的额外数据
    @Volatile
    private var mExtra: Any? = null

    override fun getValue(thisRef: KNetViewModel<*>, property: KProperty<*>): ViewModelApiHelper<T> {
        model = thisRef
        return this
    }

    /**
     * 代理请求接口
     */
    fun <R : Any> apis(extra: Any?): T {
        mExtra = extra
        mApiHandler = mApiHandler ?: InvocationHandler { _, method, args ->
            val finalExtra = mExtra
            val mJob = method.invoke(model.api, *args ?: arrayOf()) as KJob<R>
            val deferred = mJob.job as Deferred<R>
            KJob<R>(
                model.executeRequest(
                    deferred,
                    model.getLiveData(method.name),
                    finalExtra
                )
            )
        }
        mApiProxy = mApiProxy ?: Proxy.newProxyInstance(
            javaClass.classLoader,
            arrayOf(*model.api.javaClass.interfaces),
            mApiHandler!!
        ) as T
        return mApiProxy!!
    }

    /**
     * 释放资源
     */
    fun clear() {
        mApiProxy = null
        mApiHandler = null
        mExtra = null
    }


}
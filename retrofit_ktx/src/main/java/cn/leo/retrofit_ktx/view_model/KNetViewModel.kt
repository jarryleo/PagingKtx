package cn.leo.retrofit_ktx.view_model

import cn.leo.retrofit_ktx.http.create
import cn.leo.retrofit_ktx.utils.getSuperClassGenericType

/**
 * @author : ling luo
 * @date : 2019-07-03
 *
 * ViewModel 生命周期比 Activity 长，不应该持有任何view引用或者包含view的引用，
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class KNetViewModel<T : Any> : KViewModel() {

    /**
     * 网络请求帮助类
     */
    private val mApiHelper by ViewModelApiHelper<T>()

    /**
     * 非代理网络请求(订阅方式请勿直接请求)
     */
    val api by lazy { createApi() }

    /**
     * 代理网络请求
     */
    val apis get() = mApiHelper.apis<Any>(null)

    /**
     * 代理传参网络请求
     * @param extra 在订阅里原样返回
     */
    fun apis(extra: Any? = null) = mApiHelper.apis<Any>(extra)

    /**
     * 获取接口基础地址
     */
    abstract fun getBaseUrl(): String

    /**
     * 创建请求api
     * 可以重写，配置retrofit 和 okHttp3
     */
    open fun createApi(): T {
        return javaClass
            .getSuperClassGenericType<T>() //获取泛型类型
            .create {
                //创建retrofit，生成请求service
                baseUrl = getBaseUrl()
            }
    }


    /**
     * 释放资源
     */
    override fun onCleared() {
        mApiHelper.clear()
    }
}
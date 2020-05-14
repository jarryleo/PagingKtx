package cn.leo.retrofit_ktx.view_model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * @author : ling luo
 * @date : 2019-07-03
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
open class KLiveData<T> : MediatorLiveData<Result<T>>() {

    private fun getObserver(
        result: (Result<T>).() -> Unit
    ): KObserver<Result<T>> {
        return KObserver {
            result(it)
            when (it) {
                is Result.Loading<T> -> it.loading(it.isShow)
                is Result.Success<T> -> it.success(it.data)
                is Result.Failed<T> -> it.failed(it.exception)
            }
        }
    }

    class KObserver<R>(var block: (R) -> Unit) : Observer<R> {
        override fun onChanged(t: R) {
            block(t)
        }
    }

    /**
     * 主线程执行成功方法
     */
    open fun success(value: T, extra: Any? = null) {
        super.postValue(Result.Success(value).apply { this.extra = extra })
    }

    /**
     * 原线程执行成功方法
     */
    fun setSuccess(value: T) = super.setValue(Result.Success(value))

    /**
     * 通知loading状态
     */
    fun showLoading() = super.postValue(Result.Loading(true))

    /**
     * 线程转换的失败方法
     */
    open fun failed(e: Exception, extra: Any? = null) {
        val failed = Result.Failed<T>(e)
        failed.extra = extra
        super.postValue(failed)
    }

    /**
     * 订阅
     */
    fun observe(
        lifecycleOwner: LifecycleOwner,
        result: (Result<T>).() -> Unit = {}
    ) = super.observe(lifecycleOwner, getObserver(result))

    fun observeForever(
        result: (Result<T>).() -> Unit = {}
    ) = super.observeForever(getObserver(result))


    private var onceObserver: KObserver<Result<T>>? = null
    fun observeOnce(
        result: (Result<T>).() -> Unit = {}
    ) {
        if (onceObserver != null) {
            onceObserver?.block = {
                result(it)
                when (it) {
                    is Result.Loading<T> -> it.loading(it.isShow)
                    is Result.Success<T> -> it.success(it.data)
                    is Result.Failed<T> -> it.failed(it.exception)
                }
            }
        } else {
            onceObserver = getObserver(result)
            super.observeForever(onceObserver!!)
        }
    }

    /**
     * LiveData 类型转换，类似与RxJava的map
     */
    fun <R> map(mapFunction: (input: T) -> R): KLiveData<R> {
        val newLiveData = KLiveData<R>()
        newLiveData.addSource(this) {
            when (it) {
                is Result.Failed -> {
                    val failed =
                        Result.Failed<R>(it.exception)
                    failed.extra = it.extra
                    newLiveData.postValue(failed)
                }
                is Result.Success -> {
                    val success =
                        Result.Success(
                            mapFunction(it.data)
                        )
                    success.extra = it.extra
                    newLiveData.postValue(success)
                }
            }
        }
        return newLiveData
    }
}
package cn.leo.retrofitktx.ext

import cn.leo.retrofit_ktx.utils.KResult
import cn.leo.retrofit_ktx.utils.withIO
import cn.leo.retrofitktx.bean.BaseBean
import cn.leo.retrofitktx.exceptions.BusinessException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll

/**
 * @author : ling luo
 * @date : 2020/6/23
 */

suspend inline fun <T> Deferred<BaseBean<T>>.getResult(
    crossinline loadingCallback: (Boolean) -> Unit = {}
): KResult<T> {
    return withIO {
        try {
            loadingCallback(true)
            val data = this@getResult.await()
            if (data.errcode == 0) {
                KResult.success(data.data)
            } else {
                KResult.failure<T>(BusinessException(data.errcode, data.errmsg))
            }
        } catch (e: Exception) {
            KResult.failure<T>(e)
        } finally {
            loadingCallback(false)
        }
    }
}

suspend inline fun <T> Collection<Deferred<T>>.getResult(
    crossinline loadingCallback: (Boolean) -> Unit = {}
): KResult<List<T>> {
    return withIO {
        try {
            loadingCallback(true)
            val data = this@getResult.awaitAll()
            KResult.success(data)
        } catch (e: Exception) {
            KResult.failure<List<T>>(e)
        } finally {
            loadingCallback(false)
        }
    }
}
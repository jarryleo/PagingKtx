package cn.leo.retrofit_ktx.view_model

import cn.leo.retrofit_ktx.utils.KResult
import cn.leo.retrofit_ktx.utils.withIO
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll

/**
 * @author : ling luo
 * @date : 2020/6/24
 */
suspend inline fun <T> Deferred<T>.result() =
    withIO {
        try {
            val data = this@result.await()
            KResult.success(data)
        } catch (e: Exception) {
            KResult.failure<T>(e)
        }
    }

suspend inline fun <T> Collection<Deferred<T>>.result() =
    withIO {
        try {
            val data = this@result.awaitAll()
            KResult.success(data)
        } catch (e: Exception) {
            KResult.failure<List<T>>(e)
        }
    }

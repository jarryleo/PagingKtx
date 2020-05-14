package cn.leo.retrofit_ktx.view_model

import java.util.concurrent.ConcurrentHashMap

/**
 * @author : ling luo
 * @date : 2019-12-12
 * liveData 创建和缓存类
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class KLiveDataHelper {

    private val mLiveDataCache = ConcurrentHashMap<String, KLiveData<*>>()

    /**
     * 获取LiveData
     * @param key 方法名做key
     */
    fun <R> getLiveData(key: String): KLiveData<R> {
        return if (mLiveDataCache.containsKey(key)) {
            mLiveDataCache[key] as KLiveData<R>
        } else {
            val liveData = KLiveData<R>()
            mLiveDataCache[key] = liveData
            liveData
        }
    }

    /**
     * 释放资源
     */
    fun clear() {
        mLiveDataCache.clear()
    }

}
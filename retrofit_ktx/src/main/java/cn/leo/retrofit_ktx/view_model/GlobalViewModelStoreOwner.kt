package cn.leo.retrofit_ktx.view_model

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * @author : ling luo
 * @date : 2019-12-10
 * 全局 ViewModelStoreOwner 测试类，谨慎使用
 */
class GlobalViewModelStoreOwner : ViewModelStoreOwner {
    companion object {
        private val globalViewModelStore by lazy { ViewModelStore() }
        val globalModelStoreOwner by lazy { GlobalViewModelStoreOwner() }
    }

    override fun getViewModelStore(): ViewModelStore {
        return globalViewModelStore
    }

    fun clear() {
        globalViewModelStore.clear()
    }
}
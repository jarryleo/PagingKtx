package cn.leo.pagingktx

import android.app.Application
import android.content.Context

/**
 * @author : ling luo
 * @date : 2020/5/11
 */
class App : Application() {
    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}
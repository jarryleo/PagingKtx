package cn.leo.pagingktx

import android.app.Application
import android.content.Context
import cn.leo.pagingktx.view.NoMoreDataFooter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

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
        initSmartRefreshLayout()
    }

    private fun initSmartRefreshLayout() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            //NoMoreDataFooter(context)
            ClassicsFooter(context)
        }
    }
}
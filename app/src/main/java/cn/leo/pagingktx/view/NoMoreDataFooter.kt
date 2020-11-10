package cn.leo.pagingktx.view

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import cn.leo.pagingktx.utils.dp
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper

/**
 * @author : leo
 * @date : 2020/5/15
 */
class NoMoreDataFooter(context: Context) : RefreshFooterWrapper(
    TextView(context).apply {
        gravity = Gravity.CENTER
        text = "加载更多。。。"
        height = 60.dp
    }) {
    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        val textView = view as? TextView
        if (noMoreData) {
            textView?.text = "人家也是有底线的"
        } else {
            textView?.text = "加载更多。。。"
        }
        return true
    }
}

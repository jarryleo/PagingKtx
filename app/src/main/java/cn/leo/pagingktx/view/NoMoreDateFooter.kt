package cn.leo.pagingktx.view

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import cn.leo.pagingktx.utils.dp
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper

/**
 * @author : ling luo
 * @date : 2020/5/15
 */
class NoMoreDateFooter(context: Context) : RefreshFooterWrapper(
    TextView(context).apply {
        gravity = Gravity.CENTER
        text = "人家也是有底线的"
        height = 60.dp
    }
)

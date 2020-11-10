package cn.leo.pagingktx.view

import android.view.View
import androidx.annotation.LayoutRes

/**
 * @author : leo
 * @date : 2020/5/15
 */

fun View.showView(@LayoutRes layoutId: Int) {
    StatusPager.builder(this).build()
        .showCustom(layoutId)
}
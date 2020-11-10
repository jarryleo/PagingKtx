package cn.leo.pagingktx.adapter

import android.widget.ImageView
import cn.leo.paging_ktx.PagingDataAdapterKtx
import cn.leo.paging_ktx.SimpleHolder
import cn.leo.pagingktx.R
import cn.leo.pagingktx.bean.NewsBean
import cn.leo.pagingktx.image.loadImage
import cn.leo.pagingktx.utils.dp

/**
 * @author : ling luo
 * @date : 2020/11/10
 * @description : 知乎日报 holder
 */
class NewsHolder : SimpleHolder<NewsBean.StoriesBean>() {

    override fun getLayoutRes(): Int {
        return R.layout.item_news
    }

    override fun bindData(
        helper: PagingDataAdapterKtx.ItemHelper,
        data: NewsBean.StoriesBean?,
        payloads: MutableList<Any>?
    ) {
        if (data == null) return
        helper.setText(R.id.tv_title, data.title)
            .findViewById<ImageView>(R.id.iv_cover)
            .loadImage(data.images?.get(0) ?: "", corners = 6.dp)
    }


}
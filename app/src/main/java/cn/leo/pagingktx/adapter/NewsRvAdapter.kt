package cn.leo.pagingktx.adapter

import android.widget.ImageView
import cn.leo.paging_ktx.PagingDataAdapterKtx
import cn.leo.pagingktx.R
import cn.leo.pagingktx.bean.NewsBean
import cn.leo.pagingktx.image.loadImage
import cn.leo.pagingktx.utils.dp

/**
 * @author : leo
 * @date : 2020/5/11
 */
class NewsRvAdapter : PagingDataAdapterKtx<NewsBean.StoriesBean>(
    itemCallback(
        areItemsTheSame = { old, new ->
            old.id == new.id
        },
        areContentsTheSame = { old, new ->
            old.title == new.title && old.type == new.type
        }
    )
) {

    override fun getItemLayout(position: Int): Int {
        return R.layout.item_news
    }

    override fun bindData(
        helper: ItemHelper,
        data: NewsBean.StoriesBean?,
        payloads: MutableList<Any>?
    ) {
        if (data == null) {
            return
        }
        if (!payloads.isNullOrEmpty()) {
            helper.setText(R.id.tv_title, data.title)
            return
        }
        helper.setText(R.id.tv_title, data.title)
            .findViewById<ImageView>(R.id.iv_cover)
            .loadImage(data.images?.get(0) ?: "", corners = 6.dp)
    }
}
package cn.leo.pagingktx.adapter

import cn.leo.paging_ktx.PagedListAdapterKtx
import cn.leo.pagingktx.R
import cn.leo.pagingktx.db.bean.User

/**
 * @author : ling luo
 * @date : 2020/5/11
 */
class UserRvAdapter : PagedListAdapterKtx<User>(
    createDiffCallback(
        areItemsTheSame = { old, new ->
            old.id == new.id
        },
        areContentsTheSame = { old, new ->
            old.name == new.name && old.sex == new.sex
        },
        getChangePayload = { old, new ->
            if (old.sex != new.sex) {
                new.sex
            } else {
                null
            }
        }
    )
) {


    override fun getItemLayout(position: Int): Int {
        return R.layout.item_user
    }

    override fun bindData(helper: ItemHelper, data: User?, payloads: MutableList<Any>?) {
        if (data == null) {
            //开启占位会传递空数据过来，这时候可以渲染页面为占位图
            helper.setText(R.id.tv_name, "加载中。。。")
                .setText(R.id.tv_sex, "加载中。。。")
            return
        }
        if (payloads?.isNotEmpty() == true) {
            helper.setText(R.id.tv_sex, data.getSexText())
        } else {
            helper.setText(R.id.tv_name, data.name)
                .setText(R.id.tv_sex, data.getSexText())
        }
    }
}
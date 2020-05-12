package cn.leo.pagingktx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.leo.pagingktx.R

/**
 * @author : ling luo
 * @date : 2020/5/12
 */
class FooterAdapter : RecyclerView.Adapter<FooterAdapter.FooterHolder>() {

    private var hasNoMore = false

    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pbLoading by lazy {
            itemView.findViewById<ProgressBar>(R.id.pb_loading)
        }
        private val tvLoading by lazy {
            itemView.findViewById<TextView>(R.id.tv_loading)
        }

        fun showLoading() {
            pbLoading.visibility = View.VISIBLE
            tvLoading.text = "加载中。。。"
        }

        fun showNoMore() {
            pbLoading.visibility = View.GONE
            tvLoading.text = "人家也是有底线的"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_footer, parent, false)
        return FooterHolder(layout)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: FooterHolder, position: Int) {
        if (hasNoMore) {
            holder.showNoMore()
        } else {
            holder.showLoading()
        }
    }

    fun noMoreState(noMore: Boolean) {
        hasNoMore = noMore
        notifyItemChanged(0)
    }
}
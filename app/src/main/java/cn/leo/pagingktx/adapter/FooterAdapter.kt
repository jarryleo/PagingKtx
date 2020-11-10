package cn.leo.pagingktx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.leo.pagingktx.R

/**
 * @author : leo
 * @date : 2020/5/12
 */
class FooterAdapter : LoadStateAdapter<FooterAdapter.FooterHolder>() {

    override fun onBindViewHolder(holder: FooterHolder, loadState: LoadState) {
        if (loadState.endOfPaginationReached) {
            holder.showNoMore()
        } else {
            holder.showLoading()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FooterHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_footer, parent, false)
        return FooterHolder(layout)
    }


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
}
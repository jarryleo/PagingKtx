package cn.leo.pagingktx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.paging_ktx.SimplePagingAdapter
import cn.leo.paging_ktx.State
import cn.leo.pagingktx.adapter.NewsHolder
import cn.leo.pagingktx.bean.NewsBean
import cn.leo.pagingktx.model.ZhiHuNewsViewModel
import cn.leo.pagingktx.view.StatusPager
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import com.scwang.smartrefresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.activity_zhi_hu.*

class ZhiHuActivity : AppCompatActivity() {

    private val model by ViewModelCreator(ZhiHuNewsViewModel::class.java)

    private val adapter by lazy { SimplePagingAdapter(NewsHolder()) }

    private val statePager by lazy {
        StatusPager.builder(srl_refresh)
            .emptyViewLayout(R.layout.state_empty)
            .loadingViewLayout(R.layout.state_loading)
            .errorViewLayout(R.layout.state_error)
            .addRetryButtonId(R.id.btn_retry)
            .setRetryClickListener { _, _ ->
                adapter.refresh()
            }
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zhi_hu)
        initRv()
        statePager.showLoading()
    }

    private fun initRv() {
        rv_news.layoutManager = LinearLayoutManager(this)
        rv_news.adapter = adapter
        //点击事件
        adapter.setOnItemClickListener { a, _, position ->
            val news = a.getData(position) as? NewsBean.StoriesBean
            news?.let {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
            }
        }
        //设置下拉刷新
        srl_refresh.setOnRefreshListener {
            adapter.refresh()
        }
        //上拉加载更多
        srl_refresh.setOnLoadMoreListener {
            adapter.retry()
        }
        //绑定数据源
        model.allNews.observe(this, Observer {
            adapter.setData(this.lifecycle, it)
        })
        //请求状态
        adapter.setOnRefreshStateListener {
            when (it) {
                is State.Loading -> {
                    //如果是手动下拉刷新，则不展示loading页
                    if (srl_refresh.state != RefreshState.Refreshing) {
                        statePager.showLoading()
                    }
                    srl_refresh.resetNoMoreData()
                }
                is State.Finished -> {
                    statePager.showContent()
                    srl_refresh.finishRefresh(true)
                    srl_refresh.setNoMoreData(it.noMoreData)
                }
                is State.Error -> {
                    statePager.showError()
                    srl_refresh.finishRefresh(false)
                }
            }
        }
        //加载更多状态
        adapter.setOnLoadMoreStateListener {
            when (it) {
                is State.Loading -> {
                    //重置上拉加载状态，显示加载loading
                    srl_refresh.resetNoMoreData()
                }
                is State.Finished -> {
                    if (it.noMoreData) {
                        //没有更多了(只能用source的append)
                        srl_refresh.finishLoadMoreWithNoMoreData()
                    } else {
                        srl_refresh.finishLoadMore(true)
                    }
                }
                is State.Error -> {
                    srl_refresh.finishLoadMore(false)
                }
            }
        }
    }
}

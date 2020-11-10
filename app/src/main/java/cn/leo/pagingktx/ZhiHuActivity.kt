package cn.leo.pagingktx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.pagingktx.adapter.NewsRvAdapter
import cn.leo.pagingktx.bean.News
import cn.leo.pagingktx.model.ZhiHuNewsViewModel
import cn.leo.pagingktx.view.StatusPager
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import com.scwang.smartrefresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.activity_zhi_hu.*

class ZhiHuActivity : AppCompatActivity() {

    private val model by ViewModelCreator(ZhiHuNewsViewModel::class.java)

    private val adapter by lazy { NewsRvAdapter() }

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
            val news = a.getData(position) as? News.StoriesBean
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
            adapter.submitData(this.lifecycle, it)
        })
        //请求状态
        //因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是刷新后
        var hasRefreshing = false
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    hasRefreshing = true
                    //如果是手动下拉刷新，则不展示loading页
                    if (srl_refresh.state != RefreshState.Refreshing) {
                        statePager.showLoading()
                    }
                    srl_refresh.resetNoMoreData()
                }
                is LoadState.NotLoading -> {
                    if (hasRefreshing) {
                        hasRefreshing = false
                        statePager.showContent()
                        srl_refresh.finishRefresh(true)
                        //如果第一页数据就没有更多了，则展示没有更多了
                        if (it.source.append.endOfPaginationReached) {
                            //没有更多了(只能用source的append)
                            srl_refresh.finishLoadMoreWithNoMoreData()
                            srl_refresh.setNoMoreData(true)
                        }
                    }
                }
                is LoadState.Error -> {
                    statePager.showError()
                    srl_refresh.finishRefresh(false)
                }
            }
        }
        //加载更多状态
        //因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是加载更多后
        var hasLoadingMore = false
        adapter.addLoadStateListener {
            when (it.append) {
                is LoadState.Loading -> {
                    hasLoadingMore = true
                    //重置上拉加载状态，显示加载loading
                    srl_refresh.resetNoMoreData()
                }
                is LoadState.NotLoading -> {
                    if (hasLoadingMore) {
                        hasLoadingMore = false
                        if (it.source.append.endOfPaginationReached) {
                            //没有更多了(只能用source的append)
                            srl_refresh.finishLoadMoreWithNoMoreData()
                        } else {
                            srl_refresh.finishLoadMore(true)
                        }
                    }
                }
                is LoadState.Error -> {
                    srl_refresh.finishLoadMore(false)
                }
            }
        }
    }
}

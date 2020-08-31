package cn.leo.pagingktx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.paging_ktx.RequestDataState
import cn.leo.pagingktx.adapter.NewsRvAdapter
import cn.leo.pagingktx.bean.News
import cn.leo.pagingktx.model.ZhiHuNewsViewModel
import cn.leo.pagingktx.utils.toast
import cn.leo.pagingktx.view.StatusPager
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import kotlinx.android.synthetic.main.activity_zhi_hu.*

class ZhiHuActivity : AppCompatActivity() {

    private val model by ViewModelCreator(ZhiHuNewsViewModel::class.java)

    private val statePager by lazy {
        StatusPager.builder(srl_refresh)
            .emptyViewLayout(R.layout.state_empty)
            .loadingViewLayout(R.layout.state_loading)
            .errorViewLayout(R.layout.state_error)
            .addRetryButtonId(R.id.btn_retry)
            .setRetryClickListener(object : StatusPager.OnClickListener {
                override fun onClick(statusManager: StatusPager?, v: View?) {
                    model.dataSourceFactory.refresh()
                }
            })
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zhi_hu)
        initRv()
    }

    private fun initRv() {
        statePager.showLoading()
        val adapter = NewsRvAdapter()
        //val footer = FooterAdapter() //底部加载中提示
        //val mergeAdapter = MergeAdapter(adapter, footer)
        rv_news.layoutManager = LinearLayoutManager(this)
        rv_news.adapter = adapter

        model.allNews.observe(this, Observer {
            adapter.submitList(it)
            srl_refresh.finishRefresh()
        })

        adapter.setOnItemClickListener { a, _, position ->
            val news = a.getData(position) as? News.StoriesBean
            news?.let {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
            }
        }

        //设置下拉刷新
        srl_refresh.setOnRefreshListener {
            model.dataSourceFactory.refresh()
        }

        //监听是否还有更多数据
        model.dataSourceFactory.observer(this, Observer {
            when (it) {
                //is RequestDataState.LOADING -> if (!it.isLoadMore) srl_refresh.autoRefresh()
                is RequestDataState.SUCCESS -> {
                    srl_refresh.setNoMoreData(it.noMoreData)
                    statePager.showContent()
                }
                is RequestDataState.FAILED -> {
                    toast("加载失败：${it.throwable?.message}")
                    statePager.showError()
                }
            }
        })
    }
}

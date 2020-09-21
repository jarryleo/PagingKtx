package cn.leo.pagingktx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.pagingktx.adapter.NewsRvAdapter
import cn.leo.pagingktx.bean.News
import cn.leo.pagingktx.model.ZhiHuNewsViewModel
import cn.leo.pagingktx.view.StatusPager
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import kotlinx.android.synthetic.main.activity_zhi_hu.*
import kotlinx.coroutines.flow.collectLatest

class ZhiHuActivity : AppCompatActivity() {

    private val model by ViewModelCreator(ZhiHuNewsViewModel::class.java)

    private val adapter by lazy { NewsRvAdapter() }

    private val statePager by lazy {
        StatusPager.builder(srl_refresh)
            .emptyViewLayout(R.layout.state_empty)
            .loadingViewLayout(R.layout.state_loading)
            .errorViewLayout(R.layout.state_error)
            .addRetryButtonId(R.id.btn_retry)
            .setRetryClickListener(object : StatusPager.OnClickListener {
                override fun onClick(statusManager: StatusPager?, v: View?) {
                    adapter.refresh()
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
        //数据源
        model.allNews.observe(this@ZhiHuActivity, Observer {
            lifecycleScope.launchWhenCreated {
                adapter.submitData(it)
            }
        })
        //请求状态
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.Loading -> {
                        statePager.showLoading()
                    }
                    is LoadState.NotLoading -> {
                        srl_refresh.finishRefresh(true)
                        statePager.showContent()
                    }
                    is LoadState.Error -> {
                        statePager.showError()
                    }
                }
            }
        }
    }
}

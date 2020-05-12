package cn.leo.pagingktx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import cn.leo.pagingktx.adapter.FooterAdapter
import cn.leo.pagingktx.adapter.NewsRvAdapter
import cn.leo.pagingktx.bean.News
import cn.leo.pagingktx.model.ZhiHuNewsViewModel
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import kotlinx.android.synthetic.main.activity_zhi_hu.*

class ZhiHuActivity : AppCompatActivity() {

    private val model by ViewModelCreator(ZhiHuNewsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zhi_hu)
        initRv()
    }

    private fun initRv() {
        val adapter = NewsRvAdapter()
        val footer = FooterAdapter() //底部加载中提示
        val mergeAdapter = MergeAdapter(adapter, footer)
        rv_news.layoutManager = LinearLayoutManager(this)
        rv_news.adapter = mergeAdapter
        model.allNews.observe(this, Observer {
            adapter.submitList(it)
            srl_refresh.isRefreshing = false
        })

        adapter.setOnItemClickListener { a, _, position ->
            val news = a.getData(position) as? News.StoriesBean
            news?.let {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
            }
        }

        //设置下拉刷新
        srl_refresh.setOnRefreshListener {
            model.refresh()
        }

        //监听是否还有更多数据
        model.dataSourceFactory.noMoreDataObserver(this, Observer { footer.noMoreState(it) })
    }
}

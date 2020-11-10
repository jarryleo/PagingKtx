package cn.leo.pagingktx

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.pagingktx.adapter.FooterAdapter
import cn.leo.pagingktx.adapter.UserRvAdapter
import cn.leo.pagingktx.db.bean.User
import cn.leo.pagingktx.model.UserModel
import cn.leo.pagingktx.utils.toast
import cn.leo.pagingktx.view.StatusPager
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val model by ViewModelCreator(UserModel::class.java)

    private val userRvAdapter by lazy { UserRvAdapter() }

    private val statePager by lazy {
        StatusPager.builder(srl_refresh)
            .emptyViewLayout(R.layout.state_empty)
            .loadingViewLayout(R.layout.state_loading)
            .errorViewLayout(R.layout.state_error)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model.insert()  //第一次运行插入假数据
        initView()
        statePager.showLoading()
    }

    private fun initView() {
        srl_refresh.setEnableLoadMore(false)
        rv_user.layoutManager = LinearLayoutManager(this)
        rv_user.adapter = userRvAdapter.withLoadStateFooter(FooterAdapter())
        //数据源
        model.allStudents.observe(this, Observer {
            userRvAdapter.submitData(this.lifecycle, it)
        })
        //刷新状态
        var hasRefreshing = false
        userRvAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    hasRefreshing = true
                }
                is LoadState.NotLoading -> {
                    if (hasRefreshing) {
                        hasRefreshing = false
                        statePager.showContent()
                        srl_refresh.finishRefresh(true)
                    }
                }
                is LoadState.Error -> {
                    srl_refresh.finishRefresh(false)
                }
            }
        }
        //设置点击事件
        userRvAdapter.setOnItemClickListener { adapter, _, position ->
            val user = adapter.getData(position) as? User
            user?.let {
                model.update(User(user.id, user.name, 1 - user.sex))
                toast("修改条目成功（$position）")
            }
        }
        //设置下拉刷新
        srl_refresh.setOnRefreshListener {
            userRvAdapter.refresh()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_zhihu -> startActivity(Intent(this, ZhiHuActivity::class.java))
        }
        return true
    }

}

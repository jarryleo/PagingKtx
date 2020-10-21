package cn.leo.pagingktx

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.pagingktx.adapter.UserRvAdapter
import cn.leo.pagingktx.db.bean.User
import cn.leo.pagingktx.model.UserModel
import cn.leo.pagingktx.utils.toast
import cn.leo.pagingktx.view.StatusPager
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collectLatest

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
        rv_user.layoutManager = LinearLayoutManager(this)
        rv_user.adapter = userRvAdapter
        //数据源
        model.allStudents.observe(this, Observer {
            lifecycleScope.launchWhenCreated {
                userRvAdapter.submitData(it)
            }
        })
        //请求状态
        lifecycleScope.launchWhenCreated {
            userRvAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.Loading -> {
                        statePager.showLoading()
                    }
                    is LoadState.NotLoading -> {
                        srl_refresh.finishRefresh(true)
                        srl_refresh.finishLoadMoreWithNoMoreData()
                        statePager.showContent()
                    }
                    is LoadState.Error -> {
                        statePager.showError()
                    }
                }
            }
        }

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

package cn.leo.pagingktx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.paging_ktx.State
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
        statePager.showLoading()
        model.insert().invokeOnCompletion {
            lifecycleScope.launchWhenCreated { initView() }
        }
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
        userRvAdapter.setOnRefreshStateListener {
            when (it) {
                is State.Success -> {
                    statePager.showContent()
                    srl_refresh.finishRefresh(true)
                }
                is State.Error -> {
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
}

package cn.leo.pagingktx

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.pagingktx.adapter.UserRvAdapter
import cn.leo.pagingktx.db.bean.User
import cn.leo.pagingktx.model.UserModel
import cn.leo.pagingktx.utils.toast
import cn.leo.retrofit_ktx.view_model.ViewModelCreator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val model by ViewModelCreator(UserModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //model.insert()
        initView()
    }

    private fun initView() {
        val userRvAdapter = UserRvAdapter()
        //val footer = FooterAdapter() //底部加载中提示
        //val mergeAdapter = MergeAdapter(userRvAdapter, footer)
        rv_user.layoutManager = LinearLayoutManager(this)
        rv_user.adapter = userRvAdapter
        /*
        开启动画的话，在分页的一页最后一条局部刷新UI会导致下一页从下往上的动画。
        如果不开启 paging的占位，会导致列表下移
        如果在adapter里判断数据为空设置占位图的话，会导致下一页条目先展示占位，再出结果，有闪烁效果
         */
        rv_user.itemAnimator = null
        model.allStudents.observe(this, Observer {
            srl_refresh.finishRefresh()
            userRvAdapter.submitList(it)
        })
        userRvAdapter.setOnItemClickListener { adapter, _, position ->
            val user = adapter.getData(position) as? User
            user?.let {
                model.update(User(user.id, user.name, 1 - user.sex))
                toast("修改条目成功（$position）")
                //adapter.notifyItemChanged(position)
            }
        }

        //设置下拉刷新
        srl_refresh.setOnRefreshListener {
            model.refresh()
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

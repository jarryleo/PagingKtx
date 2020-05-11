package cn.leo.pagingktx

import android.os.Bundle
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
        initRv()
    }

    private fun initRv() {
        val userRvAdapter = UserRvAdapter()
        rv_user.layoutManager = LinearLayoutManager(this)
        rv_user.adapter = userRvAdapter
        model.allStudents.observe(this,
            Observer {
                userRvAdapter.submitList(it)
            })
        userRvAdapter.setOnItemClickListener { adapter, v, position ->
            val user = adapter.getData(position) as? User
            user?.let {
                model.update(User(user.id, user.name, 1 - user.sex))
                toast("修改条目成功（$position）")
                //adapter.notifyItemChanged(position)
            }
        }
    }
}

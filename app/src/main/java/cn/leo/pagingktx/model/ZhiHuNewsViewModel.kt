package cn.leo.pagingktx.model

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import cn.leo.paging_ktx.DataSourceFactoryKtx
import cn.leo.paging_ktx.RequestDataState
import cn.leo.paging_ktx.pageKeyedDataSource
import cn.leo.pagingktx.bean.News
import cn.leo.retrofit_ktx.http.await
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : ling luo
 * @date : 2020/5/12
 */
class ZhiHuNewsViewModel : BaseViewModel() {

    private val mDate = Calendar.getInstance().apply {
        add(Calendar.DATE, 1)
    }

    val dataSourceFactory by lazy {
        DataSourceFactoryKtx<Long, News.StoriesBean> {
            pageKeyedDataSource<Long, News.StoriesBean>(
                initial = { _, callback ->
                    viewModelScope.launch {
                        val date = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                            .format(mDate.time).toLong()
                        val result = api.getNews(date).await()
                        result?.let {
                            callback.onResult(it.stories, 0, it.date.toLong())
                        }
                    }
                    RequestDataState.SUCCESS
                },
                after = { params, callback ->
                    async {
                        val result = api.getNews(params.key).await()
                        result?.let {
                            callback.onResult(it.stories, it.date.toLong())
                            if (it.stories.isEmpty()) {
                                setNoMoreData(true)
                            }
                        }
                    }
                    RequestDataState.SUCCESS
                }
            )
        }
    }

    private val config = PagedList.Config.Builder()
        .setPageSize(30)                          //配置分页加载的数量
        .setEnablePlaceholders(false)             //配置是否启动PlaceHolders
        .setPrefetchDistance(10)                  //预取数据的距离
        .setInitialLoadSizeHint(30)               //初始化加载的数量
        .build()

    private val boundaryCallback =
        object : PagedList.BoundaryCallback<News.StoriesBean>() {
            override fun onItemAtEndLoaded(itemAtEnd: News.StoriesBean) {
                Log.e("BoundaryCallback", "加载更多！！${itemAtEnd.title}")
            }
        }

    val allNews =
        LivePagedListBuilder(dataSourceFactory, config)
            .setBoundaryCallback(boundaryCallback)
            .build()

    fun refresh() = async {
        dataSourceFactory.refresh()
    }
}
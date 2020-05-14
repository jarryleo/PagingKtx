package cn.leo.pagingktx.model

import android.util.Log
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import cn.leo.paging_ktx.DataSourceFactoryKtx
import cn.leo.paging_ktx.PageKeyedDataSourceKtx
import cn.leo.paging_ktx.RequestDataState
import cn.leo.pagingktx.bean.News
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
            object : PageKeyedDataSourceKtx<Long, News.StoriesBean>() {
                override fun loadInitial(
                    params: LoadInitialParams<Long>,
                    callback: LoadInitialCallback<Long, News.StoriesBean>
                ) {
                    super.loadInitial(params, callback)
                    observeOnce(apis::getNews) {
                        success {
                            callback.onResult(it.stories, 0, it.date.toLong())
                            changeState(RequestDataState.SUCCESS())
                        }
                        failed {
                            changeState(RequestDataState.FAILED())
                        }

                    }
                    val date = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                        .format(mDate.time).toLong()
                    apis.getNews(date)
                }

                override fun loadAfter(
                    params: LoadParams<Long>,
                    callback: LoadCallback<Long, News.StoriesBean>
                ) {
                    super.loadAfter(params, callback)
                    observeOnce(apis::getNews) {
                        success {
                            callback.onResult(it.stories, it.date.toLong())
                            changeState(RequestDataState.SUCCESS(true))
                        }
                        failed {
                            changeState(RequestDataState.FAILED())
                        }
                    }
                    apis.getNews(params.key)
                }
            }
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
}
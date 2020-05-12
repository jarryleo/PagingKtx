package cn.leo.pagingktx.model

import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import cn.leo.pagingktx.bean.News
import cn.leo.pagingktx.net.Apis
import cn.leo.pagingktx.net.LoggerInterceptor
import cn.leo.pagingktx.net.Urls
import cn.leo.retrofit_ktx.http.OkHttp3Creator
import cn.leo.retrofit_ktx.http.await
import cn.leo.retrofit_ktx.http.create
import cn.leo.retrofit_ktx.view_model.KNetViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : ling luo
 * @date : 2020/5/12
 */
class ZhiHuNewsViewModel : KNetViewModel<Apis>() {
    override fun getBaseUrl() = Urls.baseUrlZhiHu

    val api by lazy { createApi() }

    override fun createApi(): Apis {
        return Apis::class.java.create {
            baseUrl = getBaseUrl()
            httpClient = OkHttp3Creator.build {
                //网络请求日志打印拦截器
                interceptors.add(LoggerInterceptor())
            }
        }
    }

    private val mDate = Calendar.getInstance().apply {
        add(Calendar.DATE, 1)
    }

    private val dataSource by lazy {
        object : PageKeyedDataSource<Long, News.StoriesBean>() {
            override fun loadInitial(
                params: LoadInitialParams<Long>,
                callback: LoadInitialCallback<Long, News.StoriesBean>
            ) {
                viewModelScope.launch {
                    val date =
                        SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                            .format(mDate.time).toLong()
                    val result = api.getNews(date).await()
                    result?.let {
                        callback.onResult(it.stories, 0, it.date.toLong())
                    }
                }
            }

            override fun loadAfter(
                params: LoadParams<Long>,
                callback: LoadCallback<Long, News.StoriesBean>
            ) {
                viewModelScope.launch {
                    val result = api.getNews(params.key).await()
                    result?.let {
                        callback.onResult(it.stories, it.date.toLong())
                    }
                }
            }

            override fun loadBefore(
                params: LoadParams<Long>,
                callback: LoadCallback<Long, News.StoriesBean>
            ) {
            }
        }
    }

    private val dataSourceFactory by lazy {
        object : DataSource.Factory<Long, News.StoriesBean>() {
            override fun create(): DataSource<Long, News.StoriesBean> = dataSource
        }
    }

    private val config = PagedList.Config.Builder()
        .setPageSize(30)                          //配置分页加载的数量
        .setEnablePlaceholders(false)             //配置是否启动PlaceHolders
        .setPrefetchDistance(10)                  //预取数据的距离
        .setInitialLoadSizeHint(30)               //初始化加载的数量
        .build()

    val allNews =
        LivePagedListBuilder(dataSourceFactory, config).build()
}
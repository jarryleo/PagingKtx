package cn.leo.pagingktx.model

import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import cn.leo.paging_ktx.DataSourceFactoryKtx
import cn.leo.paging_ktx.pageKeyedDataSource
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
                },
                after = { params, callback ->
                    viewModelScope.launch {
                        val result = api.getNews(params.key).await()
                        result?.let {
                            callback.onResult(it.stories, it.date.toLong())
                            if (it.stories.isEmpty()) {
                                setNoMoreData(true)
                            }
                        }
                    }
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

    val allNews =
        LivePagedListBuilder(dataSourceFactory, config).build()

    fun refresh() = async {
        dataSourceFactory.refresh()
    }
}
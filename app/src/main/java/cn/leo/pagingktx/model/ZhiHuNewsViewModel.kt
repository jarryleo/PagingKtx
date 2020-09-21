package cn.leo.pagingktx.model

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
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
    val allNews = Pager(PagingConfig(20)) {
        object : PagingSource<Long, News.StoriesBean>() {
            override suspend fun load(params: LoadParams<Long>): LoadResult<Long, News.StoriesBean> {
                val date =
                    params.key ?: SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                        .format(mDate.time)
                        .toLong()
                return try {
                    val data = api.getNews(date).await()
                    LoadResult.Page(data.stories, null, data.date.toLong())
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }
    }.flow.asLiveData(viewModelScope.coroutineContext)
}
package cn.leo.pagingktx.model

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import cn.leo.pagingktx.bean.NewsBean
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : leo
 * @date : 2020/5/12
 */
class NewsViewModel : BaseViewModel() {

    private val mDate = Calendar.getInstance().apply {
        add(Calendar.DATE, 1)
    }

    private val initialKey = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        .format(mDate.time)
        .toLong()

    val allNews =
        Pager(PagingConfig(20), initialKey = initialKey) {
            object : PagingSource<Long, NewsBean.StoriesBean>() {
                override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NewsBean.StoriesBean> {
                    val date = params.key ?: initialKey
                    return try {
                        val data = api.getNews(date).await()
                        LoadResult.Page(data.stories, null, data.date?.toLongOrNull())
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }
            }
        }.flow.cachedIn(viewModelScope)
    //.asLiveData(viewModelScope.coroutineContext)
}
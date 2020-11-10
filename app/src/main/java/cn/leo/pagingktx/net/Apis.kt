package cn.leo.pagingktx.net

import cn.leo.pagingktx.bean.NewsBean
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author : leo
 * @date : 2020/5/12
 */
interface Apis {

    /**
     * 知乎日报历史记录
     */
    @GET("before/{time}")
    fun getNews(@Path("time") time: Long): Deferred<NewsBean>
}
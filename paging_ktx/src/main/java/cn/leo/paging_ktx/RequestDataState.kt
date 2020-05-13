package cn.leo.paging_ktx

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
sealed class RequestDataState(isLoadMore: Boolean = false) {
    /**
     * 加载中
     * @param isLoadMore 是否是分页的加载更多
     */
    class LOADING(isLoadMore: Boolean = false) : RequestDataState(isLoadMore)

    /**
     * 加载成功
     * @param isLoadMore 是否是分页的加载更多
     * @param noMoreData 是否还有下一页
     */
    class SUCCESS(isLoadMore: Boolean = false, noMoreData: Boolean = false) :
        RequestDataState(isLoadMore)

    /**
     * 加载失败
     * @param isLoadMore 是否是分页的加载更多
     * @param exception 失败原因
     */
    class FAILED(isLoadMore: Boolean = false, exception: Exception? = null) :
        RequestDataState(isLoadMore)
}
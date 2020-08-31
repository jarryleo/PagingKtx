package cn.leo.retrofitktx.bean

data class BaseListBean<T>(
    val isNextPage: Int,
    val list: ArrayList<T>?,
    val pageIndex: Int? = 0,
    val count: Long? = 0
) {
    fun hasNextPage() = isNextPage == 1
}
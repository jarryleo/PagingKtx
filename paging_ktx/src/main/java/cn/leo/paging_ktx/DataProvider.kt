package cn.leo.paging_ktx

/**
 * @author : ling luo
 * @date : 2020/9/5
 * @description : 从list提供指定位置数据
 */
class DataProvider<T>(private val list: List<T>) {

    /**
     * @param page 开始页
     * @param pageSize 每页数量
     */
    fun getList(page: Int, pageSize: Int): List<T> {
        var initialIndex: Int = page * pageSize
        var finalIndex: Int = initialIndex + pageSize
        if (initialIndex < 0) {
            initialIndex = 0
        }
        if (initialIndex > list.size) {
            initialIndex = list.size
        }
        if (finalIndex > list.size) {
            finalIndex = list.size
        }
        if (initialIndex > finalIndex) {
            initialIndex = finalIndex
        }
        return list.subList(initialIndex, finalIndex)
    }
}
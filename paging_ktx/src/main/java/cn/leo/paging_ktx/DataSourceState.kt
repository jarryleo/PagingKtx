package cn.leo.paging_ktx

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
interface DataSourceState {

    fun changeState(state: RequestDataState)

    fun retry()
}
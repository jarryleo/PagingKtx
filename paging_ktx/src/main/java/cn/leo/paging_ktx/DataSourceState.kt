package cn.leo.paging_ktx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
interface DataSourceState {

    fun changeState(state: RequestDataState)

    fun retry()

    fun refresh()

    fun observer(owner: LifecycleOwner, observer: Observer<RequestDataState>)
}
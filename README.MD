## 前言
Android 列表分页加载组件 paging3 alpha版本已经出来很久了。目前到了alpha7；
分享一下在项目中使用的经验和坑；不讲原理和源码，纯使用经验分享！
（不要问我为啥把alpha版本用在项目中，问就是任性，问就是paging2太难用了）

## 准备工作

### 1.依赖：
`本文撰写日期：2020-10-21；最新版为3.0.0-alpha07`
```
//java
implementation 'androidx.paging:paging-runtime:3.0.0-alpha07'
//kotlin
implementation 'androidx.paging:paging-runtime-ktx:3.0.0-alpha07'
```
根据语言二选一即可，我使用的是kotlin；

## 使用：

### 1.adapter
`使用paging3 ，RecyclerView的adapter 必须继承 PagingDataAdapter`
因为后续分页的UI和操作都归于 adapter 管理；

adpater 构造必须传参数 DiffUtil.ItemCallback ；
用过 AsyncListDiffer 的小伙伴应该明白它的作用；
不明白的可以参考一下这篇文章:[Android AsyncListDiffer-RecyclerView最好的伙伴](https://www.jianshu.com/p/66d0feab2b5b)

#### DiffUtil.ItemCallback 简单介绍：
DiffUtil.ItemCallback的作用就是取代notifyDataSetChanged粗暴刷新列表的；
毕竟粗暴刷新比较消耗性能；

主要介绍三个方法：
```
override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {}

override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {}

override fun getChangePayload(oldItem: T, newItem: T): Any? {}
```
paging3的设计理念是不建议对列表数据直接修改；而是对数据源进行操作，数据源的变化会自动更新到列表；
DiffUtil.ItemCallback 就是用来比对数据变化，从而决定更新对应UI；并执行条目动画；

- areItemsTheSame
比对新旧条目是否是同一个条目；
一般比对条目的唯一标示id即可，谨慎对待，如果条目不同则可能不会更新UI；

- areContentsTheSame
当上面的方法确定是同一个条目之后，这里比对条目的内容是否一样，不一样则会更新条目UI
建议这里的比对把UI展示的数据都写上，写漏了会导致UI不更新对应字段；

- getChangePayload （可选）
这个方法对应 RcyclerView的 adapter的 第三个参数；用于条目内部的局部刷新；
```
override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    )
```


### 2.数据请求处理

这里利用知乎日报的接口作为范例：
没有使用到paging3的数据库缓存方案 remoteMediator；因为参数被注解为
@OptIn(ExperimentalPagingApi::class)还在测试中；这里讲解纯网络请求分页方案；
实际项目中，不可能每个列表接口都做数据库缓存的，工作量太大；

paging3 数据请求主要用到3个类：
1. Pager
2. PagingConfig
3. PagingSource

- Pager 分页数据的主要入口，这是它的构造：
```
class Pager<Key : Any, Value : Any>
@JvmOverloads constructor(
    config: PagingConfig,
    initialKey: Key? = null,
    @OptIn(ExperimentalPagingApi::class)
    remoteMediator: RemoteMediator<Key, Value>? = null,
    pagingSourceFactory: () -> PagingSource<Key, Value>
)
```
它的泛型 
Key -> 分页标志 ，类似于页码，或者其它告诉后端我要哪一页的参数；
Value -> 列表数据的单个数据类型，就是每个条目的类型；

参数解释：
config ：分页配置，见下面介绍
initialKey ： 初始页的页码 （可选）
remoteMediator ：远程数据解调员；网络请求数据后处理的类，可以做数据缓存
pagingSourceFactory：数据源工厂（每次刷新数据都会生产新的数据源）

- PagingConfig 介绍
Pager第一个参数：config: PagingConfig 分页逻辑：每页多少条之类的设置；
构造：
```
class PagingConfig @JvmOverloads constructor(
	val pageSize: Int,
	@IntRange(from = 0)
    val prefetchDistance: Int = pageSize,
	val enablePlaceholders: Boolean = true,
	@IntRange(from = 1)
    val initialLoadSize: Int = pageSize*DEFAULT_INITIAL_PAGE_MULTIPLIER,
	val maxSize: Int = MAX_SIZE_UNBOUNDED,
	val jumpThreshold: Int = COUNT_UNDEFINED
)
```

参数解释：
pageSize：每页多少个条目；必填
prefetchDistance ：预加载下一页的距离，滑动到倒数第几个条目就加载下一页，无缝加载（可选）默认值是pageSize
enablePlaceholders：是否启用条目占位，当条目总数量确定的时候；列表一次性展示所有条目，但是没有数据；在adapter的onBindViewHolder里面绑定数据时候，是空数据，判断是空数据展示对应的占位item；可选，默认开启。
initialLoadSize ：第一页加载条目数量 ，可选，默认值是 3*pageSize （有时候需要第一页多点数据可用）
maxSize ：定义列表最大数量；可选，默认值是：Int.MAX_VALUE
jumpThreshold：暂时还不知道用法，从文档注释上看，是滚动大距离导致加载失效的阈值；可选，默认值是：Int.MIN_VALUE （表示禁用此功能）

- PagingSource 分页数据源
pagingSourceFactory 工厂生产的产品；
```
abstract class PagingSource<Key : Any, Value : Any> {
	abstract suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value>
}
```
泛型同 Pager 泛型，要实现的主要方法就一个：比paging2方便了不知道多少倍

参数解释：
params ：请求列表需要的参数
返回值：
LoadResult ：列表数据请求结果，包含下一页要请求的key

用法范例：

```
val allNews = Pager(PagingConfig(20), initialKey = initialKey) {
            object : PagingSource<Long, News.StoriesBean>() {
                override suspend fun load(params: LoadParams<Long>): LoadResult<Long, News.StoriesBean> {
                    val date = params.key ?: initialKey
                    return try {
                        val data = api.getNews(date).await() //网络请求数据
                        LoadResult.Page(data.stories, null, data.date.toLong())
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }
            }
        }
            .flow
            .cachedIn(viewModelScope)
            .asLiveData(viewModelScope.coroutineContext)
```

LoadResult.Page 解释：
```
constructor(
                data: List<Value>,
                prevKey: Key?,
                nextKey: Key?
            )
```
参数：
data ：返回的数据列表
prevKey ：上一页的key （传 null 表示没有上一页）
nextKey ：下一页的key （传 null 表示没有下一页）


paging3 使用 flow 传递数据，不了解的可以搜索一下flow
cachedIn 绑定协程生命周期，必须加上，否则可能崩溃
asLiveData 熟悉livedata的都知道怎么用

#### 绑定数据给adapter

```
model.allNews.observe(this@ZhiHuActivity, Observer {
            lifecycleScope.launchWhenCreated {
                adapter.submitData(it)
            }
        })
```
adapter.submitData 是一个协程挂起（suspend）操作，所以要放入协程赋值
lifecycleScope.launchWhenCreated 和 viewModelScope
需要依赖协程的生命周期辅助，见下面：
```
//生命周期辅助ktx
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-beta01'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-beta01'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.3.0-beta01'
```

### 3.UI状态处理和操作
#### 下拉刷新
第一次请求不需要任何操作，订阅数据直接请求；
手动下拉刷新直接调用：
```
adapter.refresh()
```
就是这么简单，比paging2方便多了

#### 上拉加载
paging3是无缝加载，实际没有手动上拉的操作
但是用户滑动过快的话还是会展示上拉的UI，下面会有UI的处理逻辑

#### 失败重试
```
adapter.retry()
```
主要用于加载更多的重试。

#### UI状态处理
adapter.addLoadStateListener ：添加状态监听:
```
adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {}
                is LoadState.NotLoading -> {}
                is LoadState.Error -> {}
            }
        }
```
状态返回的参数 CombinedLoadStates，包含了
`refresh，prepend，append,source,mediator` 五种行为的状态
分别是：
`刷新，向前加载更多，向后加载更多，数据源，调解员` 

每个行为分为3中状态：
- LoadState.Loading    加载中  (加载数据时候回调)
- LoadState.NotLoading 没有加载中 (加载数据前和加载数据完成后回调)
- LoadState.Error      加载失败 （加载数据失败回调）

我们一般业务只关注 刷新和向后加载更多；

以SmartRefreshLayout为例：

#### 下拉刷新状态处理：
```
//因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是刷新后
var hasRefreshing = false
adapter.addLoadStateListener {
    when (it.refresh) {
        is LoadState.Loading -> {
            hasRefreshing = true
            //如果是手动下拉刷新，则不展示loading页
            if (srl_refresh.state != RefreshState.Refreshing) {
                statePager.showLoading()
            }
        }
        is LoadState.NotLoading -> {
            if (hasRefreshing) {
				hasRefreshing= false
				statePager.showContent()
                srl_refresh.finishRefresh(true)
                //如果第一页数据就没有更多了，第一页不会触发append
                if (it.source.append.endOfPaginationReached){
                    //没有更多了(只能用source的append)
                    srl_refresh.finishLoadMoreWithNoMoreData()
                }
            }
        }
        is LoadState.Error -> {
			statePager.showError()
            srl_refresh.finishRefresh(false)
        }
    }
}
```

#### 上拉加载更多状态处理：
```
//因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是加载更多后
var hasLoadingMore = false
adapter.addLoadStateListener {
    when (it.append) {
        is LoadState.Loading -> {
            hasLoadingMore = true
            //重置上拉加载状态，显示加载loading
            srl_refresh.resetNoMoreData()
        }
        is LoadState.NotLoading -> {
            if (hasLoadingMore) {
                hasLoadingMore = false
                if (it.source.append.endOfPaginationReached){
                    //没有更多了(只能用source的append)
                    srl_refresh.finishLoadMoreWithNoMoreData()
                }else{
                    srl_refresh.finishLoadMore(true)
                }
            }
        }
        is LoadState.Error -> {
            srl_refresh.finishLoadMore(false)
        }
    }
}
```

上面代码就是刷新和加载更多状态监听了，有一个问题：
**第一页数据如果没有更多了，是不会触发 append 的 LoadState.Loading 状态，所以得在refresh里面判断一下；**
#### 刷新失败处理：
直接调用刷新即可
```
adapter.refresh()
```

#### 加载更多失败处理：

```
srl_refresh.setOnLoadMoreListener { 
    adapter.retry()
}
```
为什么是重试？
因为paging是无缝加载，所以没有手动上拉加载逻辑
retry()虽然是重试，但是paging已处理，只有失败后会重试，所以这里上拉加载调用重试没问题

## 关于Header和 Footer
PagingDataAdapter 是支持 添加Header和Footer 的
```
adapter.withLoadStateHeader(header: LoadStateAdapter<*>)
adapter.withLoadStateFooter(header: LoadStateAdapter<*>)
adapter.withLoadStateHeaderAndFooter(header: LoadStateAdapter<*>,
        footer: LoadStateAdapter<*>)
```

LoadStateAdapter ： 也是一个 RecyclerView.Adapter ；
类似于多条目布局，只是分成多个adapter
谷歌出过一个 MergeAdapter，就是把多个RecyclerView.Adapter 合并成一个，
有兴趣的小伙伴可以搜索一下。这里就不介绍了；
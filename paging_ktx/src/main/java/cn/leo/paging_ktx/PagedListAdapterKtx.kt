package cn.leo.paging_ktx

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.core.app.ActivityCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : ling luo
 * @date : 2020/5/11
 */
@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
abstract class PagedListAdapterKtx<T : Any> : PagedListAdapter<T, RecyclerView.ViewHolder> {

    constructor() : super(createDiffCallback())

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback)

    companion object {
        fun <T> createDiffCallback(
            areItemsTheSame: (T, T) -> Boolean = { _, _ -> false },
            areContentsTheSame: (T, T) -> Boolean = { _, _ -> false },
            getChangePayload: (T, T) -> Any? = { _, _ -> null }
        ): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return areContentsTheSame(oldItem, newItem)
                }

                override fun getChangePayload(oldItem: T, newItem: T): Any? {
                    return getChangePayload(oldItem, newItem)
                }
            }
        }
    }

    /**
     * 获取条目类型的布局
     *
     * @param position 索引
     * @return 布局id
     */
    @LayoutRes
    protected abstract fun getItemLayout(position: Int): Int


    /**
     * 给条目绑定数据
     *
     * @param helper  条目帮助类
     * @param data    对应数据
     * @param payloads item局部变更
     */
    protected abstract fun bindData(
        helper: ItemHelper,
        data: T?,
        payloads: MutableList<Any>? = null
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PagedListAdapterKtx<out Any>.ViewHolder)?.onBindViewHolder(position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayout(position)
    }

    fun getData(position: Int): T? {
        return getItem(position)
    }

    /**
     * 局部刷新
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val viewHolder = holder as? PagedListAdapterKtx<*>.ViewHolder
            val helper = viewHolder?.itemHelper
            val itemHolder = helper?.mItemHolder
            val item = getItem(position)
            if (itemHolder != null) {
                itemHolder.bindData(helper, item, payloads)
            } else {
                (holder as? PagedListAdapterKtx<out Any>.ViewHolder)?.onBindViewHolder(
                    position,
                    payloads
                )
            }
        }
    }


    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as? PagedListAdapterKtx<*>.ViewHolder
        val helper = viewHolder?.itemHelper
        val itemHolder = helper?.mItemHolder
        itemHolder?.onViewDetach(helper)
    }

    inner class ViewHolder internal constructor(parent: ViewGroup, layout: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        ),
        View.OnClickListener,
        View.OnLongClickListener {
        val itemHelper: ItemHelper = ItemHelper(this)

        init {
            itemHelper.setLayoutResId(layout)
            itemHelper.setOnItemChildClickListener(mOnItemChildClickListenerProxy)
            itemHelper.setOnItemChildLongClickListener(mOnItemChildLongClickListenerProxy)
            itemHelper.setRVAdapter(this@PagedListAdapterKtx)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        var mPosition = 0

        fun onBindViewHolder(position: Int, payloads: MutableList<Any>? = null) {
            mPosition = position
            bindData(itemHelper, getItem(mPosition), payloads)
        }

        override fun onClick(v: View) {
            if (::mOnItemClickListener.isInitialized) {
                mOnItemClickListener(this@PagedListAdapterKtx, v, mPosition)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (::mOnItemLongClickListener.isInitialized) {
                mOnItemLongClickListener(this@PagedListAdapterKtx, v, mPosition)
                return true
            }
            return false
        }
    }

    class ItemHelper(private val viewHolder: PagedListAdapterKtx<out Any>.ViewHolder) :
        View.OnClickListener, View.OnLongClickListener {
        private val viewCache = SparseArray<View>()
        private val clickListenerCache = ArrayList<Int>()
        private val longClickListenerCache = ArrayList<Int>()
        private val mTags = HashMap<String, Any>()
        lateinit var adapter: PagedListAdapterKtx<out Any>
            private set

        @LayoutRes
        @get:LayoutRes
        var itemLayoutResId: Int = 0
        val position
            get() = viewHolder.mPosition
        val itemView: View = viewHolder.itemView
        val context: Context = itemView.context
        var tag: Any? = null

        private lateinit var mOnItemChildClickListener:
                    (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit

        private lateinit var mOnItemChildLongClickListener:
                    (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit

        fun setLayoutResId(@LayoutRes layoutResId: Int) {
            this.itemLayoutResId = layoutResId
        }

        fun setOnItemChildClickListener(
            onItemChildClickListener:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
        ) {
            mOnItemChildClickListener = onItemChildClickListener
        }

        fun setOnItemChildLongClickListener(
            onItemChildLongClickListener:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
        ) {
            mOnItemChildLongClickListener = onItemChildLongClickListener
        }

        fun setRVAdapter(PagedListAdapter: PagedListAdapterKtx<out Any>) {
            adapter = PagedListAdapter
        }

        fun setTag(key: String, tag: Any) {
            mTags[key] = tag
        }

        fun getTag(key: String): Any? {
            return mTags[key]
        }

        @Suppress("UNCHECKED_CAST")
        fun <V : View> findViewById(@IdRes viewId: Int): V {
            val v = viewCache.get(viewId)
            val view: V?
            if (v == null) {
                view = itemView.findViewById(viewId)
                if (view == null) {
                    val entryName = itemView.resources.getResourceEntryName(viewId)
                    throw NullPointerException("id: R.id.$entryName can not find in this item!")
                }
                viewCache.put(viewId, view)
            } else {
                view = v as V
            }
            return view
        }

        fun <V : View> getViewById(@IdRes viewId: Int, call: (V) -> Unit = {}): ItemHelper {
            val view = findViewById<V>(viewId)
            call(view)
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param text   设置的文字
         */
        fun setText(@IdRes viewId: Int, text: CharSequence?): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.text = text
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param resId  设置的文字资源
         */
        fun setText(@IdRes viewId: Int, @StringRes resId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.text = try {
                        it.resources.getString(resId)
                    } catch (e: Exception) {
                        resId.toString()
                    }
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 设置文本颜色
         *
         * @param viewId 要设置文本的控件，TextView及其子类都可以
         * @param color  颜色int值，不是资源Id
         */
        fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.setTextColor(color)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 设置文本颜色
         *
         * @param viewId     要设置文本的控件，TextView及其子类都可以
         * @param colorResId 颜色资源Id
         */
        fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorResId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    ActivityCompat.getColor(context, colorResId)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 给图片控件设置资源图片
         *
         * @param viewId 图片控件id
         * @param resId  资源id
         */
        fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is ImageView) {
                    it.setImageResource(resId)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not ImageView")
                }
            }
            return this
        }

        /**
         * 设置view的背景
         *
         * @param viewId 控件id
         * @param resId  资源id
         */
        fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
            getViewById<View>(viewId) {
                it.setBackgroundResource(resId)
            }
            return this
        }

        fun setVisibility(@IdRes viewId: Int, visibility: Int): ItemHelper {
            getViewById<View>(viewId) {
                it.visibility = visibility
            }
            return this
        }

        fun setVisibleOrGone(@IdRes viewId: Int, visibility: () -> Boolean): ItemHelper {
            getViewById<View>(viewId) {
                it.visibility = if (visibility()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            return this
        }

        fun setVisibleOrInVisible(@IdRes viewId: Int, visibility: () -> Boolean): ItemHelper {
            getViewById<View>(viewId) {
                it.visibility = if (visibility()) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
            return this
        }

        fun setViewVisible(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id) {
                    it.visibility = View.VISIBLE
                }
            }
            return this
        }

        fun setViewInvisible(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id) {
                    it.visibility = View.INVISIBLE
                }
            }
            return this
        }

        fun setViewGone(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id) {
                    it.visibility = View.GONE
                }
            }
            return this
        }

        /**
         * 给条目中的view添加点击事件
         *
         * @param viewId 控件id
         */
        fun addOnClickListener(@IdRes viewId: Int): ItemHelper {
            val contains = clickListenerCache.contains(viewId)
            if (!contains) {
                getViewById<View>(viewId) { it.setOnClickListener(this) }
                clickListenerCache.add(viewId)
            }
            return this
        }

        /**
         * 给条目中的view添加长按事件
         *
         * @param viewId 控件id
         */
        fun addOnLongClickListener(@IdRes viewId: Int): ItemHelper {
            val contains = longClickListenerCache.contains(viewId)
            if (!contains) {
                getViewById<View>(viewId) { it.setOnLongClickListener(this) }
                longClickListenerCache.add(viewId)
            }
            return this
        }

        override fun onClick(v: View) {
            if (::mOnItemChildClickListener.isInitialized) {
                mOnItemChildClickListener(adapter, v, position)
            }
        }


        override fun onLongClick(v: View): Boolean {
            if (::mOnItemChildLongClickListener.isInitialized) {
                mOnItemChildLongClickListener(adapter, v, position)
                return true
            }
            return false
        }

        var mItemHolder: ItemHolder<Any>? = null

        @Suppress("UNCHECKED_CAST")
        fun setItemHolder(itemHolderClass: Class<out ItemHolder<out Any>>) {
            try {
                if (mItemHolder == null) {
                    val newInstance = itemHolderClass.newInstance()
                    mItemHolder = newInstance as ItemHolder<Any>?
                    mItemHolder?.initView(this, adapter.getItem(position))
                }
                mItemHolder?.bindData(this, adapter.getItem(position))
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }


    /**
     * 多条目类型防止 adapter臃肿，每个条目请继承此类
     *
     * @param <T> 数据类型
    </T> */
    abstract class ItemHolder<T : Any> {

        /**
         * 绑定数据
         *
         * @param helper 帮助类
         * @param item   数据
         */
        abstract fun bindData(helper: ItemHelper, item: T?, payload: Any? = null)

        /**
         * 初始化view，只在view第一次创建调用
         *
         * @param helper 帮助类
         * @param item   数据
         */
        open fun initView(helper: ItemHelper, item: T?) {}


        /**
         * 被回收时调用，用来释放一些资源，或者重置数据等
         *
         * @param helper 帮助类
         */
        open fun onViewDetach(helper: ItemHelper) {

        }
    }

    private lateinit var mOnItemClickListener:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemLongClickListener:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildClickListener:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildLongClickListener:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    val mOnItemChildClickListenerProxy:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit =
        { adapter, v, position ->
            if (::mOnItemChildClickListener.isInitialized) {
                mOnItemChildClickListener(adapter, v, position)
            }
        }
    val mOnItemChildLongClickListenerProxy:
                (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit =
        { adapter, v, position ->
            if (::mOnItemChildLongClickListener.isInitialized) {
                mOnItemChildLongClickListener(adapter, v, position)
            }
        }

    fun setOnItemClickListener(
        onItemClickListener:
            (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(
        onItemLongClickListener:
            (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    fun setOnItemChildClickListener(
        onItemChildClickListener:
            (adapter: PagedListAdapterKtx<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemChildClickListener = onItemChildClickListener
    }

}
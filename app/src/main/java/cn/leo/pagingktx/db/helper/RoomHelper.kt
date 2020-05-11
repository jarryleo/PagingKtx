package cn.leo.pagingktx.db.helper

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.leo.pagingktx.App
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author : ling luo
 * @date : 2019-12-03
 */
@Suppress("UNCHECKED_CAST", "UNUSED")
object RoomHelper {
    /**
     * 数据库名称，自定义
     */
    private const val DB_NAME = "db_name"

    /**
     * 数据库对象缓存
     */
    private val dbCache = ConcurrentHashMap<Class<out RoomDatabase>, RoomDatabase>()

    /**
     * 实例化数据库对象
     */
    fun <T : RoomDatabase> getDb(context: Context, clazz: Class<T>): T {
        return (dbCache[clazz] as? T) ?: Room.databaseBuilder(
            context, clazz, DB_NAME
        ).build().also { dbCache[clazz] = it }
    }

    /**
     * 释放数据库对象
     */
    fun release() {
        dbCache.values.forEach {
            if (it.isOpen) {
                it.close()
            }
        }
        dbCache.clear()
    }
}


/**
 * 数据库委托
 */
class DbProperty<T : RoomDatabase>(private val clazz: Class<T>) : ReadOnlyProperty<Context, T> {
    override fun getValue(thisRef: Context, property: KProperty<*>): T {
        return RoomHelper.getDb(thisRef, clazz)
    }
}

/**
 * 数据库委托
 */
class DbModelProperty<T : RoomDatabase>(private val clazz: Class<T>) : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return RoomHelper.getDb(App.context!!, clazz)
    }
}

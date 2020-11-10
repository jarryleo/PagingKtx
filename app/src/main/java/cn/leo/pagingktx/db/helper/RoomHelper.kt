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
    const val DB_NAME = "db_name"

    /**
     * 数据库对象缓存
     */
    private val dbCache = ConcurrentHashMap<Class<out RoomDatabase>, RoomDatabase>()

    /**
     * 实例化数据库对象
     */
    fun <T : RoomDatabase> getDb(context: Context, clazz: Class<T>, dbName: String = DB_NAME): T {
        val db = dbCache[clazz] as? T
        if (db != null) {
            if (db.isOpen) {
                return db
            } else {
                dbCache.remove(clazz)
            }
        }
        return Room.databaseBuilder(context, clazz, dbName)
            .fallbackToDestructiveMigration()
            .build()
            .also { dbCache[clazz] = it }
    }

    /**
     * 关闭数据库
     */
    fun <T : RoomDatabase> release(clazz: Class<T>) {
        dbCache[clazz]?.let {
            it.close()
            dbCache.remove(clazz)
        }
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
class DbProperty<T : RoomDatabase>(
    private val clazz: Class<T>,
    private val dbName: String = RoomHelper.DB_NAME
) : ReadOnlyProperty<Context, T> {
    override fun getValue(thisRef: Context, property: KProperty<*>): T {
        return RoomHelper.getDb(thisRef, clazz, dbName)
    }
}

/**
 * 数据库委托
 */
class DbModelProperty<T : RoomDatabase>(
    private val clazz: Class<T>,
    private val dbName: String = RoomHelper.DB_NAME
) : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return RoomHelper.getDb(App.context!!, clazz, dbName)
    }
}

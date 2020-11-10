package cn.leo.pagingktx.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.leo.pagingktx.db.bean.User

/**
 * @author : leo
 * @date : 2019-12-03
 */
@Database(entities = [User::class], version = 1)
abstract class DB : RoomDatabase() {
    abstract fun userDao(): UserDao
}
package cn.leo.pagingktx.db.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : ling luo
 * @date : 2019-12-03
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val name: String,

    val sex: Int = 0
) {
    companion object {
        val sexText = arrayListOf("男", "女", "保密")
    }

    fun getSexText() = sexText[sex]

    override fun toString(): String {
        return "(id = $id , name = $name , sex = ${getSexText()})"
    }
}
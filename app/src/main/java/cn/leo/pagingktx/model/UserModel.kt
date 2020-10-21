package cn.leo.pagingktx.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.leo.pagingktx.db.DB
import cn.leo.pagingktx.db.bean.User
import cn.leo.pagingktx.db.helper.DbModelProperty
import cn.leo.retrofit_ktx.utils.io
import kotlin.random.Random

/**
 * @author : ling luo
 * @date : 2020/5/11
 */
class UserModel : ViewModel() {

    private val CHEESE_DATA = arrayListOf(
        "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
        "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
        "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
        "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
        "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String", "Aromes au Gene de Marc",
        "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
        "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
        "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase",
        "Baylough", "Beaufort", "Beauvoorde", "Beenleigh Blue", "Beer Cheese", "Bel Paese",
        "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
        "Blarney", "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille",
        "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello", "Blue Rathgore",
        "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)"
    )

    private val db by DbModelProperty(DB::class.java)

    val allStudents =
        Pager(PagingConfig(20)) {
            db.userDao().getAllUser()
        }
            .flow
            .cachedIn(viewModelScope)
            .asLiveData(viewModelScope.coroutineContext)

    //插入假数据
    fun insert() = io {
        val userList = CHEESE_DATA.map {
            User(0, it, Random.nextInt(2))
        }.toTypedArray()
        println("userList : ---------${userList.size}")
        db.userDao().insert(*userList)
    }

    //更新条目
    fun update(user: User) = io {
        db.runInTransaction { db.userDao().update(user) }
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }
}
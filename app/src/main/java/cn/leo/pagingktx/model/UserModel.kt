package cn.leo.pagingktx.model

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import cn.leo.pagingktx.db.DB
import cn.leo.pagingktx.db.bean.User
import cn.leo.pagingktx.db.helper.DbModelProperty
import cn.leo.retrofit_ktx.view_model.KViewModel
import kotlin.random.Random

/**
 * @author : ling luo
 * @date : 2020/5/11
 */
class UserModel : KViewModel() {

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

    private val config = PagedList.Config.Builder()
        .setPageSize(10)                         //配置分页加载的数量
        .setEnablePlaceholders(true)             //配置是否启动PlaceHolders
        .setPrefetchDistance(3)                  //预取数据的距离
        .setInitialLoadSizeHint(20)              //初始化加载的数量
        .build()

    val allStudents =
        LivePagedListBuilder(db.userDao().getAllUser(), config).build()

    //插入假数据
    fun insert() = async {
        val userList = CHEESE_DATA.map { User(0, it, Random.nextInt(2)) }.toTypedArray()
        println("userList : ---------${userList.size}")
        db.userDao().delAll()
        db.userDao().insert(*userList)
    }

    //更新条目
    fun update(user: User) = async {
        db.runInTransaction {
            db.userDao().update(user)
        }
    }
}
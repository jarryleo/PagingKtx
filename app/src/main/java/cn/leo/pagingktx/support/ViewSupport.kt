package cn.leo.pagingktx.support

import android.widget.CheckBox
import android.widget.TextView
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author : leo
 * @date : 2020/5/12
 */

abstract class ViewProperty<V, T>(view: () -> V) : ReadWriteProperty<Any, T> {
    val targetView: V by lazy { view.invoke() }
}


fun bindTextView(view: () -> TextView) = object : ViewProperty<TextView, String>(view) {
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return targetView.text.toString()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        targetView.text = value
    }
}

fun bindCheckBox(view: () -> CheckBox) = object : ViewProperty<CheckBox, Boolean>(view) {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return targetView.isChecked
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        targetView.isChecked = value
    }
}
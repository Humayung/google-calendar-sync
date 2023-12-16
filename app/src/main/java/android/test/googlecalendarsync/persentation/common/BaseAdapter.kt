package android.test.googlecalendarsync.persentation.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseViewHolder<VB : ViewBinding>(var binding: VB, val context: Context) : RecyclerView.ViewHolder(binding.root)
abstract class BaseAdapter<VB : ViewBinding, N : BaseAdapter.Navigator, DataType : Any>() : RecyclerView.Adapter<BaseViewHolder<VB>>() {
    val dataLock = Any()
    var context: Context? = null
    private var _binding: ViewBinding? = null
    private var _data: MutableList<DataType> = mutableListOf<DataType>()
    val data: List<DataType> = _data

    protected val navigator: N
        get() = requireNotNull(nav) { "Navigator is not set for this adapter" }
    private var nav: N? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    fun setNavigator(navigator: N) {
        this.nav = navigator
    }

    open override fun getItemCount(): Int = _data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater.invoke(layoutInflater, parent, false)
        return BaseViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        onLoadItem(holder.binding, data[position], position, getItemViewType(position), holder.context)
    }

    fun add(item: DataType) {
        _data.add(item)
        notifyItemInserted(data.lastIndex)
    }

    fun add(index: Int, item: DataType) {
        _data.add(index, item)
        notifyItemInserted(index)
    }

    fun addToFirst(item: DataType) {
        _data.add(0, item)
        notifyItemInserted(0)
    }

    fun remove(item: DataType): Boolean {
        val index = _data.indexOf(item)
        val success = _data.remove(item)
        notifyItemRemoved(index)
        return success
    }

    fun remove(predicate: (DataType) -> Boolean) {
        val removedIndex = data.indexOfFirst(predicate)
        if (removedIndex != -1) {
            _data.removeAt(removedIndex)
            notifyItemRemoved(removedIndex)
        }
    }

    fun removeAll(predicate: (DataType) -> Boolean) {
        _data.removeAll(predicate)
    }

    fun remove(index: Int) {
        _data.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addAll(items: List<DataType>) {
        items.forEach {
            add(it)
        }
    }

    fun clear() {
        _data.clear()
        notifyDataSetChanged()
    }

    abstract fun onLoadItem(binding: VB, item: DataType, position: Int, itemViewType: Int, context: Context)
    open override fun getItemViewType(position: Int): Int = 0
    interface Navigator
}

package net.map.elixirmap.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
abstract class BaseBindingAdapter<T> : RecyclerView.Adapter<BaseBindingAdapter.BaseViewHolder>() {

    private var data = ArrayList<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        )

    override fun getItemCount() = data.size

    abstract val layoutId: Int

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val entity = data[position]
        holder.containerView.setOnClickListener {
            _onItemClickBlock?.invoke(entity)
        }
        holder.containerView.setOnLongClickListener {
            _onItemLongClickBlock?.invoke(entity)
            return@setOnLongClickListener true
        }
        onConvert(holder, entity, position)
    }

    abstract fun onConvert(holder: BaseViewHolder, item: T, position: Int)


    private var _onItemClickBlock: ((T) -> Unit)? = null

    private var _onItemLongClickBlock: ((T) -> Unit)? = null

    fun setOnItemClickListener(onItemClickBlock: (T) -> Unit) {
        _onItemClickBlock = onItemClickBlock
    }

    fun setOnItemLongClickListener(onItemLongClickBlock: (T) -> Unit){
        _onItemLongClickBlock = onItemLongClickBlock
    }

    class BaseViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer

    fun setItems(items: List<T>){
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun getItems(): ArrayList<T>{
        return data
    }
}

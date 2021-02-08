package com.idrok.yandexmap.ui.search.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.idrok.yandexmap.databinding.RvSearchItemBinding
import com.idrok.yandexmap.models.Place


class Adapter(context: Context, private val itemClickListener: ((String) -> Unit)) :
    ListAdapter<Place, Adapter.VH>(SearchDiffUtil()) {

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun submitList(list: MutableList<Place>?) {
        super.submitList(list)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RvSearchItemBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(currentList[position].uri)
        }
        holder.onBind(currentList[position])
    }
    class VH(private val binding: RvSearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(place: Place) {
            binding.place = place
        }

    }

}


class SearchDiffUtil :
    DiffUtil.ItemCallback<Place>() {


    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.displayName == newItem.displayName
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }


}




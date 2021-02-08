package com.idrok.yandexmap.ui.bookmark.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.idrok.yandexmap.R
import com.idrok.yandexmap.databinding.RvBookmarkItemBinding
import com.idrok.yandexmap.models.PlaceModel

class BookmarkAdapter(private val itemClickListener: ((PlaceModel) -> Unit)) :
    RecyclerView.Adapter<BookmarkAdapter.VH>() {

    private var listData = listOf<PlaceModel>()

    fun setData(listData: List<PlaceModel>) {
        this.listData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: RvBookmarkItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.rv_bookmark_item, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(listData[position])
        }
        holder.onBind(listData[position])
    }

    override fun getItemCount() = listData.size

    class VH(private val binding: RvBookmarkItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(placeModel: PlaceModel) {
            binding.place = placeModel
        }

    }
}
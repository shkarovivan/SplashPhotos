package com.shkarov.splashphotos.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.shkarov.splashphotos.database.data.collections_db.CollectionDb


class CollectionAdapter(
    cornerRadius: Int,
    onItemClick: (position: Int) -> Unit

) : AsyncListDifferDelegationAdapter<CollectionDb>(MoviesDiffUtilCallBack()) {

    init {
        delegatesManager.addDelegate(CollectionAdapterDelegate(onItemClick,cornerRadius))
    }
    class MoviesDiffUtilCallBack : DiffUtil.ItemCallback<CollectionDb>() {
        override fun areItemsTheSame(oldItem: CollectionDb, newItem: CollectionDb): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionDb, newItem: CollectionDb): Boolean {
            return oldItem.id == newItem.id && oldItem.userName == newItem.userName
        }
    }
}
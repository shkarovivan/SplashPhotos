package com.shkarov.splashphotos.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb

class PhotoAdapter(
	cornerRadius: Int,
	onItemClick: (position: Int) -> Unit

) : AsyncListDifferDelegationAdapter<PhotoDb>(MoviesDiffUtilCallBack()) {

	init {
		delegatesManager.addDelegate(PhotoAdapterDelegate(onItemClick,cornerRadius))
	}
	class MoviesDiffUtilCallBack : DiffUtil.ItemCallback<PhotoDb>() {
		override fun areItemsTheSame(oldItem: PhotoDb, newItem: PhotoDb): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: PhotoDb, newItem: PhotoDb): Boolean {
			return oldItem.id == newItem.id && oldItem.userName == newItem.userName
		}
	}
}
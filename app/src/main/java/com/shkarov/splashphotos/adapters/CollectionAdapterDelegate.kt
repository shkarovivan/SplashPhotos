package com.shkarov.splashphotos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.collections_db.CollectionDb
import com.shkarov.splashphotos.databinding.ItemCollectionBinding


class CollectionAdapterDelegate(
	private val onClick: (position: Int) -> Unit,
	private val cornerRadius: Int = 0
) : AbsListItemAdapterDelegate<CollectionDb, CollectionDb, CollectionAdapterDelegate.CollectionHolder>() {

	override fun isForViewType(
		item: CollectionDb,
		items: MutableList<CollectionDb>,
		position: Int,
	): Boolean {
		return true
	}

	override fun onCreateViewHolder(parent: ViewGroup): CollectionHolder {
		val itemBinding =
			ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return CollectionHolder(itemBinding, onClick, cornerRadius)
	}

	override fun onBindViewHolder(
        item: CollectionDb,
        holder: CollectionHolder,
        payloads: MutableList<Any>,

        ) {
		holder.bind(item)
	}


	class CollectionHolder(
		private val binding: ItemCollectionBinding,
		onClick: (position: Int) -> Unit,
		private val cornerRadius: Int
	) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.root.setOnClickListener {
				onClick(bindingAdapterPosition)
			}
		}

		fun bind(collectionDb: CollectionDb) {

			Glide.with(itemView)
				.load(collectionDb.coverImagePath)
				.transform(RoundedCorners(cornerRadius))
				.placeholder(R.drawable.ic_image)
				.into(binding.photoImage)
			Glide.with(itemView)
				.load(collectionDb.profileImagePath)
				.circleCrop()
				.placeholder(R.drawable.ic_person)
				.into(binding.authorAvatarImage)

			binding.authorName.text = collectionDb.userName
			val authorLink =  "@${collectionDb.userAvatar}"
			binding.authorLink.text = authorLink
			binding.numbersOfPhoto.text = collectionDb.totalPhotos.toString()
			binding.collectionTitle.text = collectionDb.title
		}
	}
}


package com.shkarov.splashphotos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.databinding.ItemPhotoBinding

class PhotoAdapterDelegate(
	private val onClick: (position: Int) -> Unit,
	private val cornerRadius: Int = 0
) : AbsListItemAdapterDelegate<PhotoDb, PhotoDb, PhotoAdapterDelegate.PhotoHolder>() {

	override fun isForViewType(
		item: PhotoDb,
		items: MutableList<PhotoDb>,
		position: Int,
	): Boolean {
		return true
	}

	override fun onCreateViewHolder(parent: ViewGroup): PhotoHolder {
		val itemBinding =
			ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return PhotoHolder(itemBinding, onClick, cornerRadius)
	}

	override fun onBindViewHolder(
        item: PhotoDb,
        holder: PhotoHolder,
        payloads: MutableList<Any>,

        ) {
		holder.bind(item)

		if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
			val layoutParams =
				holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
			layoutParams.isFullSpan = holder.layoutPosition == 0
		}

	}

	class PhotoHolder(
		private val binding: ItemPhotoBinding,
		onClick: (position: Int) -> Unit,
		private val cornerRadius: Int
	) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.root.setOnClickListener {
				onClick(bindingAdapterPosition)
			}
		}

		fun bind(photoDb: PhotoDb) {

			Glide.with(itemView)
			//	.load(Uri.parse(photoDb.photoPath))
				.load(photoDb.photoPath)
				.transform(RoundedCorners(cornerRadius))
				.placeholder(R.drawable.ic_image)
				.into(binding.photoImage)

			Glide.with(itemView)
			//	.load(Uri.parse(photoDb.profileImagePath))
				.load(photoDb.profileImagePath)
				.circleCrop()
				.placeholder(R.drawable.ic_person)
				.into(binding.authorAvatarImage)

			binding.likedImage.isActivated = photoDb.likedByUser

			binding.authorName.text = photoDb.userName
			val authorLink = "@${photoDb.userAvatar}"
			binding.authorLink.text = authorLink
			binding.likesNumber.text = photoDb.totalLikes.toString()
		}
	}
}


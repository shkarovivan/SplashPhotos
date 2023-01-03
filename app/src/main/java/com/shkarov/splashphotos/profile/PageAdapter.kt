package com.shkarov.splashphotos.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shkarov.splashphotos.profile.user_collections.UserCollectionsFragment
import com.shkarov.splashphotos.profile.user_liked_images.UserLikedPhotosFragment
import com.shkarov.splashphotos.profile.user_photos.UserPhotosFragment

class PageAdapter(
    private val userName: String,
    private val size: Int,
    private val totalPhotos: Long,
    private val totalLikedPhotos: Long,
    private val totalCollections: Long,
    fragment: ProfileFragment,
): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserPhotosFragment.newInstance(userName, totalPhotos)
            1 -> UserLikedPhotosFragment.newInstance(userName,totalLikedPhotos)
            else -> UserCollectionsFragment.newInstance(userName,totalCollections)
        }
    }
}
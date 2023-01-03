package com.shkarov.splashphotos.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.databinding.FragmentProfileBinding
import com.shkarov.splashphotos.unsplash_api.data.user.UserInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

    private val viewModel: ProfileFragmentViewModel by viewModels()
    private var snackBar: Snackbar? = null

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.locationIconImage.setOnClickListener {
            showLocation(viewModel.userInfo.value?.location)
        }

        binding.toolBar.setOnMenuItemClickListener {
                 showLogoutDialog()
            true
        }

        binding.updateButton.setOnClickListener {
            viewModel.getUserInfo()
            binding.updateButton.isVisible = false
        }

        setViewsVisibility()
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            showUserInfo(userInfo)
            openPages(userInfo.userName,userInfo.totalPhotos, userInfo.totalLikes, userInfo.totalCollections)
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::isLoading)

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            setViewsVisibilityWithError()
            binding.updateButton.isVisible = true
            snackBar = createSnackBar(
                resources.getString(it),
                getString(R.string.close_text)
            )
            {
                snackBar!!.dismiss()
            }
        }
    }

    private fun openPages(userName: String,photosNumber: Long, likedNumber: Long, collectionsNumber: Long){
        binding.viewPager.isVisible = true
        val adapter = PageAdapter(userName,3, photosNumber, likedNumber,collectionsNumber, this)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.setPageTransformer(ZoomOutTransformation())
        // set TabLayout
        TabLayoutMediator(binding.viewPagerTabLayout, binding.viewPager) { tab, position ->
            when (position){
                0 -> tab.text = photosNumber.toString() + "\n" + resources.getString(R.string.profile_photos_text)
                1 -> tab.text = likedNumber.toString() + "\n"+ resources.getString(R.string.profile_liked_text)
                2 -> tab.text = collectionsNumber.toString()+"\n" + resources.getString(R.string.profile_collections_text)
            }
        }.attach()
    }

    private fun showUserInfo(userInfo: UserInfo) {
        binding.collectionInfo.isVisible = true
        binding.emailIconImage.isVisible = true
        binding.downloadIconImage.isVisible = true
        binding.locationIconImage.isVisible = true
        binding.userFullName.text = userInfo.name
        val text = "@${userInfo.userName}"
        binding.userName.text = text
        binding.userBio.text = userInfo.bio
        binding.userLocation.text = userInfo.location
        binding.userEmail.text = userInfo.email
        binding.userDownloads.text = userInfo.downloads.toString()
        binding.locationIconImage.isEnabled = true

        Glide.with(this)
            .load(userInfo.profileImage.large)
            .circleCrop()
            .placeholder(R.drawable.ic_image)
            .into(binding.userAvatarImageView)

        binding.updateButton.isVisible = false
    }

    private fun showLocation(location: String?) {
        if (location != null) {
            val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(mapIntent)
        }
    }

    private fun setViewsVisibility() {
        binding.collectionInfo.isVisible = false
        binding.updateButton.isVisible = false
        binding.loadBar.isVisible = false
        binding.viewPager.isVisible = false
    }

    private fun setViewsVisibilityWithError() {
        binding.collectionInfo.isVisible = false
        binding.loadBar.isVisible = false
        binding.updateButton.isVisible = true
    }

    private fun createSnackBar(text: String, actionString: String, callback: () -> Unit): Snackbar {
        val contextView =
            binding.profileViews.rootView
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val snackBar = Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
        snackBar.anchorView = bottomBar
        snackBar.isAnchorViewLayoutListenerEnabled = true
        snackBar.setAction(actionString) {
            callback()
        }.show()

        return snackBar
    }

    private fun isLoading(value: Boolean) {
        binding.loadBar.isVisible = value
    }

    private fun showLogoutDialog() {
        val action = ProfileFragmentDirections.actionProfileFragmentToLogoutDialogFragment()
        findNavController().navigate(action)
    }

}
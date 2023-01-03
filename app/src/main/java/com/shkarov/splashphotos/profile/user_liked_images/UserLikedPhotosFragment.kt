package com.shkarov.splashphotos.profile.user_liked_images

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.adapters.PhotoAdapter
import com.shkarov.splashphotos.databinding.FragmentUserPhotosBinding
import com.shkarov.splashphotos.profile.ProfileFragmentDirections
import com.shkarov.splashphotos.utils.autoCleared
import com.shkarov.splashphotos.withArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserLikedPhotosFragment : Fragment(R.layout.fragment_user_photos) {

    private val binding: FragmentUserPhotosBinding by viewBinding(FragmentUserPhotosBinding::bind)
    private val viewModel: UserLikedPhotosViewModel by viewModels()
    private var photoAdapter: PhotoAdapter by autoCleared()
    private var snackBar: Snackbar? = null


    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = requireArguments().getString(KEY_USER_NAME)
        viewModel.totalLiked = requireArguments().getLong(KEY_TOTAL_NUMBER)

        binding.updateButton.isVisible = false
        setViewsVisibility()
        initList()
        bindViewModel()

        if (userName != null) {
            if (!viewModel.isInit) {
                viewModel.getUserLikedPhotos(userName)
                viewModel.isInit = true
            }
        }

        binding.updateButton.setOnClickListener {
            if (userName != null) {
                viewModel.getUserLikedPhotos(userName)
            }
        }

        binding.moreButton.setOnClickListener {
            if (userName != null) {
                viewModel.getUserLikedPhotos(userName)
            }
        }
    }

    private fun setViewsVisibility() {
        binding.updateButton.isVisible = false
        binding.moreButton.isVisible = false
        binding.loadBar.isVisible = false
    }

    private fun setViewsVisibilityWithError() {
        binding.loadBar.isVisible = false
        binding.updateButton.isVisible = true
        binding.moreButton.isVisible = false
    }


    private fun bindViewModel() {

        viewModel.userLikedPhotos.observe(viewLifecycleOwner) { photos ->
            photoAdapter.items = photos
        }

        viewModel.isAllPhotosLoaded.observe(viewLifecycleOwner) {
            binding.moreButton.isVisible = !it
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

    private fun initList() {
        photoAdapter = PhotoAdapter(resources.getInteger(R.integer.photo_radius)) { position ->
            val photoId = photoAdapter.items[position].id
            val action =
                ProfileFragmentDirections.actionProfileFragmentToDetailPhotoFragment(photoId)
            findNavController().navigate(action)
        }

        with(binding.imageList) {
            adapter = photoAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createSnackBar(text: String, actionString: String, callback: () -> Unit): Snackbar {
        val contextView =
            binding.photosImageView.rootView
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

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_TOTAL_NUMBER = "total_number"
        fun newInstance(
            userName: String,
            totalNumber: Long
        ): UserLikedPhotosFragment {
            return UserLikedPhotosFragment().withArguments {
                putString(KEY_USER_NAME, userName)
                putLong(KEY_TOTAL_NUMBER, totalNumber)
            }
        }
    }
}
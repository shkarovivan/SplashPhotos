package com.shkarov.splashphotos.profile.user_collections

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
import com.shkarov.splashphotos.adapters.CollectionAdapter
import com.shkarov.splashphotos.databinding.FragmentUserPhotosBinding
import com.shkarov.splashphotos.profile.ProfileFragmentDirections
import com.shkarov.splashphotos.utils.autoCleared
import com.shkarov.splashphotos.withArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserCollectionsFragment: Fragment(R.layout.fragment_user_photos) {

    private val binding: FragmentUserPhotosBinding by viewBinding(FragmentUserPhotosBinding::bind)
    private val viewModel: UserCollectionsViewModel by viewModels()
    private var collectionAdapter: CollectionAdapter by autoCleared()
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
                viewModel.getUserCollections(userName)
                viewModel.isInit = true
            }
        }

        binding.updateButton.setOnClickListener {
            if (userName != null) {
                viewModel.getUserCollections(userName)
            }
        }

        binding.moreButton.setOnClickListener {
            if (userName != null) {
                viewModel.getUserCollections(userName)
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

        viewModel.userCollections .observe(viewLifecycleOwner) { photos ->
            collectionAdapter.items = photos
        }

        viewModel.isAllCollectionsLoaded.observe(viewLifecycleOwner) {
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
        collectionAdapter = CollectionAdapter(resources.getInteger(R.integer.photo_radius)) { position ->
            val collectionId = collectionAdapter.items[position].id
            val action =
                ProfileFragmentDirections.actionProfileFragmentToCollectionPhotosFragment(collectionId)
            findNavController().navigate(action)
        }

        with(binding.imageList) {
            adapter = collectionAdapter
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
        ): UserCollectionsFragment {
            return UserCollectionsFragment().withArguments {
                putString(KEY_USER_NAME, userName)
                putLong(KEY_TOTAL_NUMBER, totalNumber)
            }
        }
    }
}
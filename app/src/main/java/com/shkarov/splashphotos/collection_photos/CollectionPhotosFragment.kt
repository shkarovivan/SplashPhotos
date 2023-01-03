package com.shkarov.splashphotos.collection_photos

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.adapters.PhotoAdapter
import com.shkarov.splashphotos.databinding.FragmentCollectionPhotosBinding
import com.shkarov.splashphotos.toast
import com.shkarov.splashphotos.unsplash_api.data.collections.CollectionUnsplash
import com.shkarov.splashphotos.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionPhotosFragment : Fragment(R.layout.fragment_collection_photos) {

    private val binding: FragmentCollectionPhotosBinding by viewBinding(
        FragmentCollectionPhotosBinding::bind
    )

    private var snackBar: Snackbar? = null
    private val viewModel: CollectionPhotosViewModel by viewModels()

    private var photoAdapter: PhotoAdapter by autoCleared()

    private val args: CollectionPhotosFragmentArgs by navArgs()

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collectionId = args.collectionID
        viewModel.getCollectionInfo(collectionId)

        binding.buttonMore.setOnClickListener {
            viewModel.getCollectionPhotos(collectionId)
        }
        binding.updateButton.setOnClickListener {
            viewModel.getCollectionInfo(collectionId)
            binding.updateButton.isVisible = false
        }

        setViewsVisibility()
        initList()
        bindViewModel()
        if (viewModel.getPageNumber() == 0) {
            viewModel.getCollectionPhotos(collectionId)
        }
    }

    private fun setViewsVisibility() {
        binding.loadBar.isVisible = false
        binding.errorString.isVisible = false
        binding.buttonMore.isVisible = false
        binding.collectionInfo.isVisible = false
        binding.updateButton.isVisible = false
    }

    private fun initList() {
        photoAdapter = PhotoAdapter(resources.getInteger(R.integer.photo_radius)) { position ->
            val detailPhotoId = photoAdapter.items[position].id
            val action =
                CollectionPhotosFragmentDirections.actionCollectionPhotosFragmentToDetailPhotoFragment(
                    detailPhotoId
                )
            findNavController().navigate(action)
        }
        with(binding.imageList) {
            adapter = photoAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
        }
        photoAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun bindViewModel() {
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            photoAdapter.items = photos
            binding.collectionInfo.isVisible = true
            binding.buttonMore.isVisible = true
        }

        viewModel.detailCollection.observe(viewLifecycleOwner) { collectionUnsplash ->
            showCollectionInfo(collectionUnsplash)
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::isLoading)

        viewModel.error.observe(viewLifecycleOwner, ::showError)

        viewModel.toastLiveData.observe(viewLifecycleOwner) { toast(it) }

        viewModel.isNetworkLiveData.observe(viewLifecycleOwner) {
            if (viewModel.getPageNumber() == 0) {
                setViewsVisibility()
                binding.updateButton.isVisible = true
            }
            snackBar = createSnackBar(
                resources.getString(it),
                getString(R.string.close_text)
            )
            {
                snackBar!!.dismiss()
            }
        }
    }

    private fun showCollectionInfo(detailCollection: CollectionUnsplash) {
        binding.collectionInfo.isVisible = true
        binding.CollectionTitle.text = detailCollection.title
        var text = ""
        detailCollection.tags.forEach { tag ->
            text += "#" + tag.title + " "
        }
        binding.CollectionTags.text = text
        if (detailCollection.description == null) {
            binding.collectionDescription.isGone = true
        } else {
            binding.collectionDescription.isGone = false
            binding.collectionDescription.text = detailCollection.description
        }
        text =
            detailCollection.totalPhotos.toString() + " " + (context?.getString(R.string.photos_text)
                ?: "")
        binding.collectionPhotosNumber.text = text
    }

    private fun createSnackBar(text: String, actionString: String, callback: () -> Unit): Snackbar {
        val contextView =
            binding.collectionPhotosViews.rootView
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
        binding.errorString.isVisible = false
        binding.buttonMore.isVisible = value.not()
        binding.collectionInfo.isVisible = value.not()
    }

    private fun showError(error: String) {
        binding.errorString.isVisible = true
        binding.errorString.text = error
        binding.updateButton.isVisible = true
    }

}
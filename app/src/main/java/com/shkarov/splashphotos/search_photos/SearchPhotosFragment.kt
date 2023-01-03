package com.shkarov.splashphotos.search_photos

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.adapters.PhotoAdapter
import com.shkarov.splashphotos.databinding.FragmentSearchPhotosBinding
import com.shkarov.splashphotos.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPhotosFragment: Fragment(R.layout.fragment_search_photos) {

    private val binding: FragmentSearchPhotosBinding by viewBinding(FragmentSearchPhotosBinding::bind)

    private val args: SearchPhotosFragmentArgs by navArgs()

    private val viewModel: SearchPhotosViewModel by viewModels()

    private var photoAdapter: PhotoAdapter by autoCleared()

    private var snackBar: Snackbar? = null

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = args.query
        binding.toolBar.title = resources.getString(R.string.searching_results_text) + query

        binding.buttonMore.setOnClickListener {
            viewModel.getListPhotos(query)
        }

        binding.updateButton.setOnClickListener {
            viewModel.getListPhotos(query)
            binding.updateButton.isVisible = false
        }

        setViewsVisibility()
        initList()
        bindViewModel()
        if (viewModel.getPageNumber() == 0) {
            viewModel.getListPhotos(query)
        }
    }

    private fun setViewsVisibility() {
        binding.loadBar.isVisible = false
        binding.errorString.isVisible = false
        binding.buttonMore.isVisible = false
        binding.updateButton.isVisible = false
    }

    private fun initList() {
        photoAdapter = PhotoAdapter(resources.getInteger(R.integer.photo_radius)) { position ->
            val detailPhotoId = photoAdapter.items[position].id
            val action = SearchPhotosFragmentDirections.actionSearchPhotosFragmentToDetailPhotoFragment(detailPhotoId)
            findNavController().navigate(action)
        }
        with(binding.imageList) {
            adapter = photoAdapter
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        photoAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun bindViewModel() {
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            photoAdapter.items = photos
            binding.buttonMore.isVisible = true
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::isLoading)

        viewModel.error.observe(viewLifecycleOwner, ::showError)

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

    private fun createSnackBar(text: String, actionString: String, callback: () -> Unit): Snackbar {
        val contextView =
            binding.searchPhotosViews.rootView
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
        binding.updateButton.isVisible = false
        binding.buttonMore.isVisible = value.not()
    }

    private fun showError(error: String) {
        binding.errorString.isVisible = true
        binding.errorString.text = error
        binding.updateButton.isVisible = true
    }
}
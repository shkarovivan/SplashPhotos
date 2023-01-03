package com.shkarov.splashphotos.collections

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.adapters.CollectionAdapter
import com.shkarov.splashphotos.closeApp
import com.shkarov.splashphotos.databinding.FragmentCollectionsBinding
import com.shkarov.splashphotos.showBackPressDialog
import com.shkarov.splashphotos.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment: Fragment(R.layout.fragment_collections) {

    private val binding: FragmentCollectionsBinding by viewBinding(FragmentCollectionsBinding::bind)

    private val viewModel: CollectionsViewModel by viewModels()

    private var collectionAdapter: CollectionAdapter by autoCleared()

    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            showBackPressDialog(requireContext()) {
                activity?.let { closeApp(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonMore.setOnClickListener {
            viewModel.getListCollections()
        }

        binding.updateButton.setOnClickListener {
            viewModel.getListCollections()
            binding.updateButton.isVisible = false
        }

        setViewsVisibility()
        initList()
        bindViewModel()
    }

    private fun setViewsVisibility() {
        binding.loadBar.isVisible = false
        binding.errorString.isVisible = false
        binding.buttonMore.isVisible = false
        binding.updateButton.isVisible = false
    }

    private fun initList() {
        collectionAdapter = CollectionAdapter(resources.getInteger(R.integer.photo_radius)) { position ->
            val collectionId = collectionAdapter.items[position].id
            val action = CollectionsFragmentDirections.actionCollectionsFragmentToCollectionPhotosFragment(collectionId)
            findNavController().navigate(action)
        }
        with(binding.imageList) {
            adapter = collectionAdapter
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
        }

        collectionAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//        collectionAdapter.items = viewModel.getSavedCollections()
    }

    private fun bindViewModel() {
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            collectionAdapter.items = collections
            binding.buttonMore.isVisible = true
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::isLoading)

        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            snackBar = createSnackBar(
                resources.getString(it),
                getString(R.string.close_text)
            )
            {
                snackBar!!.dismiss()
            }
        }

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
            binding.collectionsViews.rootView
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


}
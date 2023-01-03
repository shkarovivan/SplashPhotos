package com.shkarov.splashphotos.ribbon

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shkarov.splashphotos.*
import com.shkarov.splashphotos.adapters.PhotoAdapter
import com.shkarov.splashphotos.databinding.FragmentRibbonPhotosBinding
import com.shkarov.splashphotos.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RibbonPhotosFragment : Fragment(R.layout.fragment_ribbon_photos) {

	private val binding: FragmentRibbonPhotosBinding by viewBinding(FragmentRibbonPhotosBinding::bind)

	private val viewModel: RibbonPhotosViewModel by viewModels()

	private var photoAdapter: PhotoAdapter by autoCleared()

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

		binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextChange(newText: String?): Boolean {
				return false
			}

			override fun onQueryTextSubmit(query: String?): Boolean {
			if (!query.isNullOrBlank()){
				findNavController().navigate(RibbonPhotosFragmentDirections.actionFragmentRibbonPhotosToSearchPhotosFragment(query))
			}
				return false
			}
		})

		binding.buttonMore.setOnClickListener {
			viewModel.getListPhotos()
		}

		binding.updateButton.setOnClickListener {
			viewModel.getListPhotos()
		}

		setViewsVisibility()
		initList()
		bindViewModel()
	}

	private fun setViewsVisibility() {
		binding.loadBar.isVisible = false
		binding.errorString.isVisible = false
		binding.buttonMore.isVisible = false
		binding.topTodayTextString.isVisible = false
		binding.updateButton.isVisible = false
	}

	private fun initList() {
		photoAdapter = PhotoAdapter(resources.getInteger(R.integer.photo_radius)) { position ->
			val detailPhotoId = photoAdapter.items[position].id
				val action = RibbonPhotosFragmentDirections.actionFragmentRibbonPhotosToDetailPhoto(detailPhotoId)
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
			binding.topTodayTextString.isVisible = true
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
			} }

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
			binding.ribbonPhotosViews.rootView
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
		binding.topTodayTextString.isVisible = value.not()
	}
}


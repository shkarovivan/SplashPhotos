package com.shkarov.splashphotos.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.shkarov.splashphotos.MainActivity
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.databinding.FragmentStartPageBinding


class StartPageFragment : Fragment(R.layout.fragment_start_page) {

	private val binding: FragmentStartPageBinding by viewBinding(FragmentStartPageBinding::bind)
	private var startPageNumber = 0

	override fun onResume() {
		super.onResume()
		(activity as MainActivity).showBottomMenu(false)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		showStarPageInfo(startPageNumber)

		binding.buttonRight.setOnTouchListener { v, event ->
			when (event?.action) {
				MotionEvent.ACTION_DOWN -> v.isSelected = true
				MotionEvent.ACTION_UP -> {
					v.isSelected = false
					startPageNumber++
					showStarPageInfo(startPageNumber)
				}
			}
			true
		}
		binding.buttonLeft.setOnTouchListener { v, event ->
			when (event?.action) {
				MotionEvent.ACTION_DOWN -> v.isSelected = true
				MotionEvent.ACTION_UP -> {
					v.isSelected = false
					startPageNumber--
					showStarPageInfo(startPageNumber)
				}
			}
			true
		}
	}

	private fun showStarPageInfo(number: Int) {
		val array = resources.getStringArray(R.array.start_page_text)
		binding.buttonLeft.isVisible = (0 != startPageNumber)
		if (startPageNumber >= array.size) {
			(activity as MainActivity).notFirstAppRun(true)
			val action =
				StartPageFragmentDirections.actionStartPageFragmentToSignInFragment()
			findNavController().navigate(action)
		} else {
			binding.infoTextView.text = array[number]
		}
	}
}


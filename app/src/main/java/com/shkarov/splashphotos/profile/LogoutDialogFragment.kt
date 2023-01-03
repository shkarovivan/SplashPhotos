package com.shkarov.splashphotos.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kts.skillboxgithubtest.utils.resetNavGraph
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.databinding.FragmentLogoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogoutDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLogoutBinding? = null
    private val binding: FragmentLogoutBinding
        get() = _binding!!

    private val viewModel: LogoutViewModel by viewModels()

    private lateinit var dialog: BottomSheetDialog


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogoutBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutYesButton.setOnClickListener {
            viewModel.deleteDirs()
        }

        binding.logoutNoButton.setOnClickListener {
            this@LogoutDialogFragment.dismiss()
        }
        bindViewModel()

    }

    private fun bindViewModel(){
        viewModel.deletedSuccess.observe(viewLifecycleOwner,::deleteSuccess)
        viewModel.error.observe(viewLifecycleOwner){ error ->
            Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
            this@LogoutDialogFragment.dismiss()
        }
    }

    private fun deleteSuccess(value: Boolean){
        if (value){
            findNavController().resetNavGraph(R.navigation.nav_graph)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.shkarov.splashphotos.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kts.skillboxgithubtest.utils.launchAndCollectIn
import com.kts.skillboxgithubtest.utils.toast
import com.shkarov.splashphotos.MainActivity
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.closeApp
import com.shkarov.splashphotos.databinding.FragmentSignInBinding
import com.shkarov.splashphotos.showBackPressDialog
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import timber.log.Timber

class SignInFragment: Fragment(R.layout.fragment_sign_in) {

	private val viewModel: AuthViewModel by viewModels()
	private val binding: FragmentSignInBinding by viewBinding(FragmentSignInBinding::bind)
	private val args: SignInFragmentArgs by navArgs()
	private var deepLinkPhotoId: String? = null

	private val getAuthResponse =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			val dataIntent = it.data ?: return@registerForActivityResult
			handleAuthResponseIntent(dataIntent)
		}

	override fun onResume() {
		super.onResume()
		(activity as MainActivity).showBottomMenu(false)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requireActivity().onBackPressedDispatcher.addCallback(this) {
			showBackPressDialog(requireContext()) {
				activity?.let { closeApp(it) }
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		deepLinkPhotoId = args.detailPhotoId

		binding.loginButton.setOnClickListener { viewModel.openLoginPage() }
		bindViewModel()
	}

	private fun bindViewModel() {

		viewModel.loadingFlow.launchAndCollectIn(viewLifecycleOwner) {
			updateIsLoading(it)
		}
		viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
			openAuthPage(it)
		}
		viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
			toast(it)
		}
		viewModel.authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
			if (deepLinkPhotoId != null) {
				val link = deepLinkPhotoId!!
				findNavController().navigate(
					SignInFragmentDirections.actionSignInFragmentToDetailPhotoFragment(
						link
					)
				)
			} else {
				findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToFragmentRibbonPhotos())
			}
			(activity as MainActivity).showBottomMenu(true)

		}
	}

	private fun updateIsLoading(isLoading: Boolean) = with(binding) {
		loginButton.isVisible = !isLoading
		loginProgress.isVisible = isLoading
	}

	private fun openAuthPage(intent: Intent) {
		getAuthResponse.launch(intent)
	}

	private fun handleAuthResponseIntent(intent: Intent) {
		// пытаемся получить ошибку из ответа. null - если все ок
		val exception = AuthorizationException.fromIntent(intent)
		Timber.tag("Oauth").d("exception = $exception")
		// пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
		val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
			?.createTokenExchangeRequest()
		when {
			// авторизация завершались ошибкой
			exception != null -> viewModel.onAuthCodeFailed(exception)

			// авторизация прошла успешно, меняем код на токен
			tokenExchangeRequest != null ->{
				val param = tokenExchangeRequest.requestParameters.toString()
				Timber.tag("Oauth").d("3.3. requestParameters = $param")
				viewModel.onAuthCodeReceived(tokenExchangeRequest)}
		}
	}
}
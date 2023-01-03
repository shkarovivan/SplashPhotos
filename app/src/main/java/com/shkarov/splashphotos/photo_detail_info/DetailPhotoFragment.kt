package com.shkarov.splashphotos.photo_detail_info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.databinding.FragmentDetailPhotoBinding
import com.shkarov.splashphotos.unsplash_api.data.detail_photo.DetailPhoto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPhotoFragment : Fragment(R.layout.fragment_detail_photo) {

    private val binding: FragmentDetailPhotoBinding by viewBinding(FragmentDetailPhotoBinding::bind)
    private val args: DetailPhotoFragmentArgs by navArgs()
    private val viewModel: DetailPhotoViewModel by viewModels()

    private var snackBar: Snackbar? = null
    private var isWorkManagerStarted = false

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startWorkManager()
        val detailPhoto = args.detailPhotoId

        if (!viewModel.isLoadedInfo()) {
            viewModel.getDetailPhotoInfo(detailPhoto)
        }

        binding.downloadImage.setOnClickListener {
            savePhoto()
        }

        binding.locationIconImage.setOnClickListener {
            viewModel.detailPhoto.value?.let { detailPhoto ->
                showLocation(detailPhoto)
            }
        }

        binding.likedImage.setOnClickListener {
            viewModel.changeLike()
        }

        setStartVisibility()
        bindViewModel()

        binding.updateButton.setOnClickListener {
            viewModel.getDetailPhotoInfo(detailPhoto)
        }

        binding.shareLink.setOnClickListener {
            shareLink(detailPhoto)
        }
    }

    private fun bindViewModel() {
        viewModel.detailPhoto.observe(viewLifecycleOwner) {
            showInfo(it)
        }
        viewModel.isLoading.observe(viewLifecycleOwner, ::isLoading)
        viewModel.error.observe(viewLifecycleOwner, ::showError)
        viewModel.like.observe(viewLifecycleOwner,::setLike)

        viewModel.saveSuccessLiveData.observe(viewLifecycleOwner) {
            snackBar = createSnackBar(
                getString(R.string.photo_load_success),
                getString(R.string.open_text)
            )
            {
                snackBar!!.dismiss()
                showPhoto(it)
            }
        }

        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            snackBar = createSnackBar(
                resources.getString(it),
                getString(R.string.close_text)
            )
            {
                snackBar!!.dismiss()
            }
        }

        viewModel.isWorkManagerStarted.observe(viewLifecycleOwner) {
            isWorkManagerStarted = it
        }
    }

    private fun showPhoto(photoUri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(photoUri, "image/*")
        val shareIntent = Intent.createChooser(
            intent,
            requireContext().getString(R.string.share_photo_intent_text)
        )
        startActivity(shareIntent)
    }

    private fun setStartVisibility() {
        binding.allDetailViews.isVisible = false
        binding.errorGroup.isVisible = false
    }

    private fun isLoading(value: Boolean) {
        binding.loadBar.isVisible = value
        binding.errorGroup.isVisible = false
    }

    private fun showError(error: String) {
        binding.errorGroup.isVisible = true
        binding.errorString.text = error
        binding.allDetailViews.isVisible = false
    }

    private fun setLike(value: Boolean) {
        binding.likedImage.isActivated = value
    }

    private fun showLocation(detailPhoto: DetailPhoto) {
        if ((detailPhoto.location?.position?.latitude != null) && (detailPhoto.location.position.longitude != null)) {
            val gmmIntentUri =
                Uri.parse("geo:${detailPhoto.location.position.latitude},${detailPhoto.location.position.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            startActivity(mapIntent)
        } else {
            if (detailPhoto.location?.city != null || detailPhoto.location?.country != null) {
                val geoCity = (detailPhoto.location.city ?: "").replace(" ", "+")
                val geoCountry = (detailPhoto.location.country ?: "").replace(" ", "+")
                val gmmIntentUri = Uri.parse("geo:0,0?q=${geoCity}, $geoCountry")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                startActivity(mapIntent)
            }
        }
    }

    private fun savePhoto(){
        if (viewModel.detailPhoto.value != null) {
            val url = viewModel.detailPhoto.value!!.urls.raw
            val name = viewModel.detailPhoto.value!!.id
            viewModel.startDownload(url, name)
        }
    }

    private fun showInfo(detailPhoto: DetailPhoto) {

        binding.allDetailViews.isVisible = true
        Glide.with(this)
            .load(detailPhoto.urls.regular)
            .placeholder(R.drawable.ic_image)
            .into(binding.photoImage)

        Glide.with(this)
            .load(detailPhoto.user.profileImage.medium)
            .placeholder(R.drawable.ic_image)
            .circleCrop()
            .into(binding.authorAvatarImage)

        if (detailPhoto.location?.name != null) {
            binding.locationText.text = detailPhoto.location.name
        } else {
            binding.locationText.text = resources.getString(R.string.no_location_text)
        }
        var infoString: String
        if (detailPhoto.tags?.isNotEmpty() == true) {
            infoString = ""
            detailPhoto.tags.forEach {
                infoString += "#${it.title} "
            }
            binding.tagsText.text = infoString
            binding.tagsText.isGone = false
        } else {
            binding.tagsText.text = resources.getString(R.string.no_tags_text)
        }

        binding.likedImage.isActivated = detailPhoto.likedByUser
        binding.likesNumber.text = detailPhoto.likes.toString()

        infoString =
            "${resources.getString(R.string.camera_made_text)} ${detailPhoto.exif?.make ?: ""}\n" +
                    "${resources.getString(R.string.camera_model_text)} ${detailPhoto.exif?.model ?: ""}\n" +
                    "${resources.getString(R.string.camera_exposure_text)} ${detailPhoto.exif?.exposureTime ?: ""}\n" +
                    "${resources.getString(R.string.camera_aperture_text)} ${detailPhoto.exif?.aperture ?: ""}\n" +
                    "${resources.getString(R.string.camera_focal_length_text)} ${detailPhoto.exif?.focalLength ?: ""}\n" +
                    "${resources.getString(R.string.camera_iso_text)} ${detailPhoto.exif?.iso ?: ""}"

        binding.cameraInfoText.text = infoString

        infoString =
            "${resources.getString(R.string.about_text)} ${detailPhoto.user.userName}\n" +
                    "${detailPhoto.user.bio ?: ""}\n"
        binding.userInfoText.text = infoString

        infoString = "(${detailPhoto.downloads})"
        binding.totalDownLoaded.text = infoString

        binding.authorName.text = detailPhoto.user.name
        binding.authorLink.text = detailPhoto.user.userName
    }

    private fun createSnackBar(text: String, actionString: String, callback: () -> Unit): Snackbar {
        val contextView =
            binding.detailPhotoViews.rootView
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val snackBar = Snackbar.make(contextView, text, Snackbar.LENGTH_SHORT)
        snackBar.anchorView = bottomBar
        snackBar.isAnchorViewLayoutListenerEnabled = true
        snackBar.setAction(actionString) {
            callback()
        }.show()

        return snackBar
    }

    private fun startWorkManager() {
        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(DOWNLOAD_WORK_ID)
            .observe(viewLifecycleOwner) {
                if (isWorkManagerStarted) {
                    handleWorkInfo(it.first())
                }
            }
    }

    private fun shareLink(id: String) {
        val urlLink = resources.getString(R.string.share_base_url) + id
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, urlLink)
        intent.type = "text/plain"
        val shareIntent =
            Intent.createChooser(intent,requireContext().getString(R.string.share_link_text))
        context?.startActivity(shareIntent)
    }

    private fun handleWorkInfo(workInfo: WorkInfo) {

        when (workInfo.state) {

            WorkInfo.State.ENQUEUED -> {
                if (!viewModel.isNetwork()) {
                    snackBar = createSnackBar(
                        getString(R.string.network_error_download_text),
                        getString(R.string.close_text)
                    )
                    {
                        snackBar!!.dismiss()
                    }
                }
            }

            WorkInfo.State.RUNNING -> {
                snackBar = createSnackBar(
                    getString(R.string.photo_is_loading),
                    getString(R.string.close_text)
                )
                {
                    snackBar!!.dismiss()
                }
            }
            WorkInfo.State.FAILED -> {
                val isFailed = workInfo.outputData.getString(DownloadWorker.DOWNLOAD_ERROR_KEY)
                if (isFailed != null) {
                    showError(isFailed)
                }
                Toast.makeText(requireContext(), "FAILED", Toast.LENGTH_SHORT).show()
            }
            WorkInfo.State.SUCCEEDED -> {
                val uriData: Uri =
                    Uri.parse(workInfo.outputData.getString(DownloadWorker.DOWNLOAD_URI_KEY))
                snackBar!!.dismiss()
                snackBar = createSnackBar(
                    getString(R.string.photo_load_success),
                    getString(R.string.open_text)
                )
                {
                    snackBar!!.dismiss()
                    showPhoto(uriData)
                }
            }
            WorkInfo.State.CANCELLED -> {
            }
            else -> {

            }
        }
    }

    companion object {
        private const val DOWNLOAD_WORK_ID = "download work"
    }

}
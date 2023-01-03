package com.shkarov.splashphotos

import android.app.Activity
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlin.system.exitProcess

fun showBackPressDialog(context: Context, listener: () ->Unit){
	AlertDialog.Builder(context)//,R.style.AlertDialogCustom)
		.setTitle(R.string.exit_app_text)
		.setPositiveButton(R.string.positive_button_text)
		{ dialog, _ ->
			run {
				listener()
				dialog.dismiss()
			}
		}
		.setNegativeButton(R.string.cancel_button_text)
		{ dialog, _ ->
			run {
				dialog.dismiss()
			}
		}
		.show()
}

fun closeApp(activity: Activity){
	activity.finishAffinity()
	exitProcess(0);
}

fun <T : Fragment> T.toast(@StringRes message: Int) {
	Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun haveQ(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun haveM(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

fun isNetworkConnected(context: Context): Boolean {
	var isConnected = true
	if (haveM()) {
		val connectivityManager =
			(context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
		val capabilities =
			connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
		isConnected = capabilities != null
	}
	return isConnected
}

fun <T : Fragment> T.withArguments(action: Bundle.() -> Unit): T {
	return apply {
		val args = Bundle().apply(action)
		arguments = args
	}
}
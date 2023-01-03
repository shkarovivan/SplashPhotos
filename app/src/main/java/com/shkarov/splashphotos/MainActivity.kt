package com.shkarov.splashphotos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.shkarov.splashphotos.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ShowMenu {

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    private lateinit var sharedPrefs: SharedPreferences

    override fun onResume() {
        super.onResume()
        showBottomMenu(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.tag("RunApp").d("Oncreate")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        sharedPrefs = application.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        try {
            if (sharedPrefs.getBoolean(NOT_FIRST_RUN_NAME, false)) {
                if (!FirstRun.firstRun) {
                    FirstRun.firstRun = true
                    notFirstAppRun(true)

                        navController.navigate(R.id.signInFragment)
                }
            }
        } catch (t: Throwable) {
            Toast.makeText(application, "$t", Toast.LENGTH_SHORT).show()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragmentRibbonPhotos -> {
                    navController.navigate(R.id.fragmentRibbonPhotos)
                }
                R.id.collectionsFragment -> {
                    navController.navigate(R.id.collectionsFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }
            }
            true
        }

    }

    override fun showBottomMenu(value: Boolean) {
        binding.bottomNavigation.isVisible = value
    }



    override fun notFirstAppRun(value: Boolean) {
		lifecycleScope.launch(Dispatchers.IO) {
			sharedPrefs.edit()
				.putBoolean(NOT_FIRST_RUN_NAME, value)
				.apply()
		}
    }

    companion object {
        const val SHARED_PREFS_NAME = "not_first_app_run"
        const val NOT_FIRST_RUN_NAME = "not_first_run"
    }
}
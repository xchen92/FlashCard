package com.xchen92.flashcard

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
   private lateinit var navHostFragment: NavHostFragment
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.navController)
         navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.let {
                it.title = when (destination.id) {
                    R.id.settingsFragment -> getString(R.string.settings)
                    R.id.infoFragment -> getString(R.string.info)
                    R.id.detailFragment -> getString(R.string.detail)
                    else -> getString(R.string.Your_goal)
                }
            }
        }
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.navHostFragment).navigateUp()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_menuItem -> {
                navHostFragment.navController.navigate(R.id.action_mainFragment_to_settingsFragment)
                true
            }
            R.id.info_menuItem -> {
                navHostFragment.navController.navigate(R.id.action_mainFragment_to_infoFragment)
                true
            }
            R.id.delete_all_menuItem ->{
                with(prefs.edit()) {
                    putBoolean(DELETE_ALL, true)
                    apply()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        const val HIDE_COMPLETED = "hide_complete"
        const val HIDE_IMAGE = "hide_image"
        const val HIDE_LABEL = "hide_label"
        const val DELETE_ALL ="delete_all"
    }
}

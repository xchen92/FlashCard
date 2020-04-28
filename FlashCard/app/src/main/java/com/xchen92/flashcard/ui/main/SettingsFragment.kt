package com.xchen92.flashcard.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.xchen92.flashcard.MainActivity.Companion.HIDE_COMPLETED
import com.xchen92.flashcard.MainActivity.Companion.HIDE_IMAGE
import com.xchen92.flashcard.MainActivity.Companion.HIDE_LABEL

import com.xchen92.flashcard.R
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() {
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(activity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hide_label_switch.isChecked = prefs.getBoolean(HIDE_LABEL, false)
        hide_label_switch.setOnCheckedChangeListener { _, isChecked ->
            with(prefs.edit()) {
                putBoolean(HIDE_LABEL, isChecked)
                apply()
            }
        }
        hide_img_switch.isChecked = prefs.getBoolean(HIDE_IMAGE, false)
        hide_img_switch.setOnCheckedChangeListener { _, isChecked ->
            with(prefs.edit()) {
                putBoolean(HIDE_IMAGE, isChecked)
                apply()
            }
        }
        hide_completed_switch.isChecked = prefs.getBoolean(HIDE_COMPLETED, false)
        hide_completed_switch.setOnCheckedChangeListener { _, isChecked ->
            with(prefs.edit()) {
                putBoolean(HIDE_COMPLETED, isChecked)
                apply()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

}

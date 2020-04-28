package com.xchen92.flashcard.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.VISIBLE
import androidx.fragment.app.activityViewModels
import com.xchen92.flashcard.BuildConfig

import com.xchen92.flashcard.R
import kotlinx.android.synthetic.main.info_fragment.*

class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment()
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
        return inflater.inflate(R.layout.info_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id_textView.text = BuildConfig.APPLICATION_ID
        title_textView.text = resources.getString(R.string.app_name)
        version_textView.text = BuildConfig.VERSION_NAME
        copyright_textView.text = resources.getString(R.string.copyright)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

}

package com.xchen92.flashcard.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso

import com.xchen92.flashcard.R
import com.xchen92.flashcard.database.Item
import kotlinx.android.synthetic.main.add_item_fragment.*
import java.io.File
import java.util.*

class AddItemFragment :  BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private val picasso = Picasso.get()
    private var newItem: Item = Item()
    private var uuid = UUID.randomUUID()
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_item_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoFile = File(context?.applicationContext?.filesDir, "IMG_$uuid.jpg")
        photoUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.xchen92.flashcard.fileprovider",
            photoFile
        )

        // workaround to show full dialog in landscape mode
        dialog?.setOnShowListener {
            val dlg = dialog as BottomSheetDialog
            val bottomSheetInternal = dlg.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal).peekHeight =
                resources.displayMetrics.heightPixels
        }
        ArrayAdapter.createFromResource(
            view.context,
            R.array.typesArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            type_spinner.adapter = adapter
        }


        type_spinner.onItemSelectedListener = this


        photo_imageButton.apply {
            val pm = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolveActivity =
                pm.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null || !cameraPermission()) {
                isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities =
                    pm.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                    )
                    startActivityForResult(captureImage, REQUEST_PHOTO)
                }
            }
        }
        complete_checkBox.setOnCheckedChangeListener { buttonView, _ ->
            newItem.completed = buttonView.isChecked
        }

        add_button.setOnClickListener {
            onDone()
            dismiss()
        }

        cancel_button.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                if (photoFile.exists()) {
                    picasso.load(photoUri)
                        .fit()
                        .centerCrop()
                        .into(type_imageView)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            type_spinner -> newItem.type = position
        }

        type_imageView.setImageResource(newItem.typeImage)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun cameraPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun onDone() {
        newItem.uuid = uuid
        newItem.title = title_editText.text.toString()
        vm.addItem(newItem)
    }

    companion object {
        private const val TAG = "AddItemFragment"
        private const val REQUEST_PHOTO = 0

        fun showDialog(fragmentManager: FragmentManager) {
            val dialog = AddItemFragment()
            dialog.isCancelable = false
            dialog.show(
                fragmentManager,
                TAG
            )
        }
    }

}

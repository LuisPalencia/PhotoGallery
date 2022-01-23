package com.cice.photogallery.views.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.cice.photogallery.R
import com.cice.photogallery.models.Image
import com.cice.photogallery.utils.GenericFunctions

class ImageDetailsDialog(val image: Image): DialogFragment() {

    private lateinit var rootView: View

    // Return a new dialog with the image info
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            rootView = inflater.inflate(R.layout.dialog_image_details, null)

            builder.setView(rootView)
                // Add action buttons
                .setTitle("${image.displayName}")
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })

            // Set the info of the image
            rootView.findViewById<TextView>(R.id.txtDisplayName).text = "${getString(R.string.file_name)}: ${image.displayName}"
            rootView.findViewById<TextView>(R.id.txtDate).text = "${getString(R.string.date)}: ${GenericFunctions.longToDateString(image.dateTaken)}"
            rootView.findViewById<TextView>(R.id.txtSize).text = "${getString(R.string.size)}: ${GenericFunctions.bytesToMegabytes(image.size)} MB"

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
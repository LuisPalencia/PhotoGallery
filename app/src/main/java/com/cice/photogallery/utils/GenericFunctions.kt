package com.cice.photogallery.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class GenericFunctions {

    companion object {
        // Method that converts a long storing a date to a string readable object
        fun longToDateString(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
            return format.format(date)
        }

        fun bytesToMegabytes(bytes: Int): String{
            val df = DecimalFormat("0.00")
            return df.format(bytes / (1024.0 * 1024.0))
        }
    }
}
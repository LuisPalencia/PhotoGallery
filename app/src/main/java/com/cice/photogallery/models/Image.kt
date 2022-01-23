package com.cice.photogallery.models

import android.net.Uri

//Class that stores all the info of an image
data class Image(
    var uri: Uri,
    var displayName: String,
    var size: Int,
    var dateTaken: Long
)

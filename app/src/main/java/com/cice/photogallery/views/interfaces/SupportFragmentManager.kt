package com.cice.photogallery.views.interfaces

import com.cice.photogallery.models.Image

//Interface for fragments so they can communicate with their belonging activity
interface SupportFragmentManager {
    fun checkStoragePermission()
    fun showImage(position: Int)
    fun errorShowImage()
    fun takePhoto()
    fun showImageDetails(image: Image)
    fun removeImage(image: Image): Boolean
}
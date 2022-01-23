package com.cice.photogallery.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cice.photogallery.models.Image
import com.cice.photogallery.models.ImageProvider

// Viewmodel for the MainActivity and its fragments
class GalleryViewModel : ViewModel() {
    // Mutable live data for the list of Images of the system
    val imagesGallery = MutableLiveData<MutableList<Image>>()

    // Method that gets from the storage all the images
    fun getImagesFromStorage(context: Context){
        imagesGallery.postValue(ImageProvider.getImagesFromStorage(context))
    }

    // Method that returns the image of a position
    fun getImage(position: Int): Image? {
        //return imagesGallery.value?.get(position)
        return ImageProvider.getImage(position)
    }

    // Function that returns the total number of images
    fun getGallerySize(): Int{
        return ImageProvider.getGallerySize()
    }

    // Method that adds a new Uri to the list
    fun addUriImage(uri: Uri, context: Context){
        ImageProvider.addUriImage(uri, context)
    }

    // Method that returns the image list
    fun getImagesList(): MutableList<Image>{
        return ImageProvider.getGalleryImagesList()
    }

    // Method that removes an image from the local storage of the app
    fun removeImage(image: Image){
        ImageProvider.removeImage(image)
    }
}
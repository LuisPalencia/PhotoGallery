package com.cice.photogallery.models

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.cice.photogallery.Storage
import java.io.File
import java.lang.Exception

class ImageProvider {

    companion object{
        private val TAG = "ImageProvider"

        //Method that gets all the images from the device
        fun getImagesFromStorage(context: Context): MutableList<Image>{
            //Create list
            var imagesStorage = Storage.imagesStorage

            // Get all the images from the storage
            //imagesStorage += getImagesFromUri(MediaStore.Images.Media.INTERNAL_CONTENT_URI, context)
            imagesStorage += getImagesFromUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, context)

            // Sort the images by the date
            imagesStorage.sortBy {
                it.dateTaken
            }

            return imagesStorage
        }

        //Method that gets all the images from a specific uri
        private fun getImagesFromUri(uri: Uri, context: Context): MutableList<Image>{

            val imagesList = mutableListOf<Image>()
            // Projection of data required from the request to the contentresolver
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN
            )

            // Order all the images by the date
            val orderBy = MediaStore.Images.Media.DATE_TAKEN

            try{
                //Perform the query
                val cursor = context.contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    orderBy
                )

                if(cursor != null){
                    // Get the column indexes
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val idDisplayName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val idSize = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val idDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                    // Check all the results from the query
                    while (cursor.moveToNext()){
                        //Get the data of the current element
                        val imageId = cursor.getLong(idColumn)
                        val imageDisplayName = cursor.getString(idDisplayName)
                        val imageSize = cursor.getInt(idSize)
                        val imageDateTaken = cursor.getLong(idDateTaken)

                        // Get the uri of the image
                        val uriImage = ContentUris.withAppendedId(uri, imageId)

                        // Create the image object
                        val image = Image(
                            uri = uriImage,
                            displayName = imageDisplayName,
                            size = imageSize,
                            dateTaken = imageDateTaken
                        )

                        // Add the image to the list
                        imagesList.add(image)
                    }
                }
            }catch (e: Exception){
                Log.e(TAG, e.toString())
            }

            return imagesList
        }

        // Method that creates an Image object from an Uri and adds it to the list
        fun addUriImage(uri: Uri, context: Context){
            //Data needed for the new image
            val projection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN)

            try{
                //Perform the query
                val cursor = context.contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    null
                )
                if(cursor != null){
                    //Get the data of the current element
                    val idDisplayName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val idSize = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val idDateTaken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                    cursor.moveToNext()

                    //Get the data for the new image
                    val imageDisplayName = cursor.getString(idDisplayName)
                    val imageSize = cursor.getInt(idSize)
                    val imageDateTaken = cursor.getLong(idDateTaken)

                    //Create the new image object
                    val image = Image(
                        uri = uri,
                        displayName = imageDisplayName,
                        size = imageSize,
                        dateTaken = imageDateTaken
                    )

                    // Add the new image to the local list
                    addImageToStorage(image)
                }
            }catch (e: Exception){

            }
        }

        //Method that adds an Image object to the Storage list
        private fun addImageToStorage(image: Image){
            Storage.imagesStorage.add(image)
        }

        // Method that returns the local Storage list of Images
        fun getGalleryImagesList(): MutableList<Image>{
            return Storage.imagesStorage
        }

        // Method that returns the number of images in the local Storage list
        fun getGallerySize(): Int{
            return Storage.imagesStorage.size
        }

        fun getImage(position:  Int): Image{
            return Storage.imagesStorage[position]
        }

        // Method that removes an image from the local Storage list
        fun removeImage(image: Image){
            Storage.imagesStorage.remove(image)
        }

    }
}
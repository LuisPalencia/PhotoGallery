package com.cice.photogallery.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.cice.photogallery.R
import com.cice.photogallery.constants.Constants
import com.cice.photogallery.databinding.ActivityMainBinding
import com.cice.photogallery.models.Image
import com.cice.photogallery.models.URIPathHelper
import com.cice.photogallery.viewmodels.GalleryViewModel
import com.cice.photogallery.views.dialogs.ImageDetailsDialog
import com.cice.photogallery.views.fragments.GalleryFragment
import com.cice.photogallery.views.fragments.ImageSliderFragment
import com.cice.photogallery.views.fragments.NoPermissionsFragment
import com.cice.photogallery.views.interfaces.SupportFragmentManager
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), SupportFragmentManager {

    private val TAG = "MainActivity"

    //Viewmodel
    private val galleryViewModel: GalleryViewModel by viewModels()

    //Gallery Fragment
    private lateinit var galleryFragment: GalleryFragment
    private var noPermissionsFragment: NoPermissionsFragment? = null

    //Binding
    private lateinit var binding: ActivityMainBinding

    //Uri temporally stored for the photo taken with the camera
    private var uriPhotoCamera: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //First it needs to check if the Storage permission is enabled or not in order to use the app
        checkStoragePermission()
    }

    private fun getSystemImages(){
        // Get the images from the storage
        galleryViewModel.getImagesFromStorage(this)
    }

    // Method that creates the gallery fragment and shows in the FragmentContainerView
    private fun createGalleryFragment(){
        // Create the gallery fragment
        galleryFragment = GalleryFragment()
        // Set the fragment visible
        setFragment(galleryFragment, false)

        // Check if the no permissions fragment was created before
        if(noPermissionsFragment != null) {
            // Remove the reference to the fragment
            noPermissionsFragment = null
            //supportFragmentManager.popBackStack()
        }
    }

    // Method that creates the NoPermissionsFragment and shows in the FragmentContainerView
    private fun showNoPermissionsFragment(){
        // Check if the fragment is not created yet
        if(noPermissionsFragment == null) {
            noPermissionsFragment = NoPermissionsFragment()
            setFragment(noPermissionsFragment!!, false)
        }else{
            // If it was created, show a toast with the error message
            Toast.makeText(this, getString(R.string.open_permissions_settings), Toast.LENGTH_LONG).show()
        }
    }

    //Fun that set up the layout of this activity depending on the permissions accepted by the user
    override fun checkStoragePermission(){
        when {
            //Permissions are granted
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED) -> {
                // get the images from the storage and show the gallery fragment
                getSystemImages()
                createGalleryFragment()
            }
            //Permissions are not granted and the dialog in order to request them is not showed
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // Show no permissions fragment
                showNoPermissionsFragment()
            }
            //Permissions are not granted and the dialog in order to request them can be showed
            else -> {
                // Request the storage permission
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.WRITE_EXTERNAL_STORAGE_PERMISSIONS_CODE
                )
            }
        }
    }

    // Function that checks if the camera permission is enabled
    private fun checkCameraPermission(){
        when {
            //Permissions are granted
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED) -> {
                // Take the photo from the camera
                takePhotoFromCamera()
            }
            //Permissions are not granted and the dialog in order to request them is not showed
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // Show an error message
                Toast.makeText(this, getString(R.string.open_permissions_settings), Toast.LENGTH_LONG).show()
            }
            //Permissions are not granted and the dialog in order to request them can be showed
            else -> {
                // Request the camera permission
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    Constants.CAMERA_PERMISSIONS_CODE
                )
            }
        }
    }

    // Overrided function that is executed after a permission requests
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.WRITE_EXTERNAL_STORAGE_PERMISSIONS_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Get the images from the gallery and show the gallery fragment
                    getSystemImages()
                    createGalleryFragment()
                }else{
                    showNoPermissionsFragment()
                }
                return
            }

            Constants.CAMERA_PERMISSIONS_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Show the activity in order to take a photo from the camera
                    takePhotoFromCamera()
                }else{
                    Snackbar.make(binding.fragmentContainer, getString(R.string.camera_permission_not_enabled_error), Snackbar.LENGTH_LONG).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    //Function that opens the activity in order to take a picture from the camera and save it
    private fun takePhotoFromCamera(){
        Log.d(TAG, "takePhotoFromCamera")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //Set up the display name of the image
        val displayName = "img_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"

        uriPhotoCamera = null

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //Insert new value in order to get the uri for the new photo
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            try{
                // Get the uri for the new photo from the contentResolver
                uriPhotoCamera = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("Failed to create new MediaStore record.")

            }catch (e: IOException){
                uriPhotoCamera?.let { orphanUri ->
                    // Don't leave an orphan entry in the MediaStore
                    contentResolver.delete(orphanUri, null, null)
                    Snackbar.make(binding.fragmentContainer, getString(R.string.error_camera), Snackbar.LENGTH_LONG).show()
                }
                uriPhotoCamera = null
            }
        }else{
            //Get the uri for the new image
            val fileImage = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), displayName)
            uriPhotoCamera = FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".provider", fileImage);
        }

        if(uriPhotoCamera != null){ //The uri has been created successfully
            // Show the camera activity
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhotoCamera)
            resultLauncherTakePhotoFromCamera.launch(intent)
        }else{
            Snackbar.make(binding.fragmentContainer, getString(R.string.error_camera), Snackbar.LENGTH_LONG).show()
        }
    }

    //Callback for the result when a new picture is taken
    private var resultLauncherTakePhotoFromCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            // There are no request codes

            //val imageBitmap = result.data?.extras?.get("data") as Bitmap
                // Add the uri image
            galleryViewModel.addUriImage(uriPhotoCamera!!, this)
            galleryFragment.addItemToGallery()
        }else if(result.resultCode == Activity.RESULT_CANCELED){
            // If the action was cancelled, the app needs to remove the uri previously created
            uriPhotoCamera?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                contentResolver.delete(orphanUri, null, null)
            }
        }
        // Reset the variable uriPhotoCamera
        uriPhotoCamera = null
    }

    // Method that sets a new fragment in the FragmentcontainerView
    private fun setFragment(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.commit {
            // Animation for the transition to the new fragment
            setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            // Replace the new fragment
            replace(R.id.fragmentContainer, fragment, fragment.tag)
            setReorderingAllowed(true)
            // check if previous fragment needs to be added to the fragment backstack
            if(addToBackStack){
                addToBackStack(null)
            }
        }
    }

    // Method that is executed when an image of the gallery is selected. It shows the image selected in a new fragment full screen
    override fun showImage(position: Int) {
        // Create the new fragment
        val fragment = ImageSliderFragment()
        // Create bundle in order to pass the position of the image selected
        var bundle = bundleOf()
        bundle.putInt(Constants.IMAGE_POSITION, position)
        fragment.arguments = bundle
        // Set visible the new fragment
        setFragment(fragment, true)
    }

    // Method that is executed when the camera button is pressed. It tries to take a new photo from the camera if the permissions are enabled
    override fun takePhoto() {
        // Check if camera permission is enabled first
        checkCameraPermission()
    }

    // Method that is executed when the info button of an image is pressed
    override fun showImageDetails(image: Image) {
        // Show a dialog with the image details
        ImageDetailsDialog(image).show(supportFragmentManager, "MyCustomFragment")
    }

    // Method that is executed when the remove button of an image is pressed. Tries to remove the image from the system
    override fun removeImage(image: Image): Boolean {
        // Get the real path of the image
        var realPath = URIPathHelper.getPath(this, image.uri)
        Log.d(TAG, "REAL PATH: $realPath")

        var deleted = false

        if(realPath != null){ // Check if real path is not null
            val file = File(realPath)

            // If the file exists, try to remove it
            if(file.exists()){
                // Store if image has been deleted or not
                deleted = file.delete()
            }
        }

        // Show a message depending if image has been deleted ot not
        if(deleted){
            Snackbar.make(binding.fragmentContainer, R.string.image_removed, Snackbar.LENGTH_SHORT).show()
            // Remove the image from the app Storage
            galleryViewModel.removeImage(image)
        }else{
            Snackbar.make(binding.fragmentContainer, R.string.error_image_removed, Snackbar.LENGTH_SHORT).show()
        }

        return deleted
    }

    override fun errorShowImage() {
        supportFragmentManager.popBackStack()
        Snackbar.make(binding.fragmentContainer, R.string.error_image_showed, Snackbar.LENGTH_SHORT).show()
    }
}
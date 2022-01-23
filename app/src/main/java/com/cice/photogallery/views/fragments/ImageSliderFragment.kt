package com.cice.photogallery.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.cice.photogallery.constants.Constants
import com.cice.photogallery.databinding.FragmentImageSliderBinding
import com.cice.photogallery.viewmodels.GalleryViewModel
import com.cice.photogallery.views.activities.MainActivity
import com.cice.photogallery.views.adapters.ImageSliderAdapter
import com.cice.photogallery.views.interfaces.SupportFragmentManager
import java.io.IOException


class ImageSliderFragment : Fragment() {


    private val TAG = "ImageSliderFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager

    private var _binding: FragmentImageSliderBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var rootView: View

    //Adapter for the recyclerview
    private lateinit var adapter: ImageSliderAdapter

    //Viewmodel
    private val galleryViewModel: GalleryViewModel by activityViewModels()

    private var imagePosition = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Set the supportFragmentManager so the fragment is able to communicate with the activity
        try{
            supportFragmentManager = context as MainActivity
        }catch (e: IOException){
            Log.d(TAG, "MainActivity is on null state")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImageSliderBinding.inflate(inflater, container, false)
        rootView = binding.root

        // Get the selected image
        imagePosition = arguments?.getInt(Constants.IMAGE_POSITION, -1)!!

        if(imagePosition != -1){
            //Set up the viewpager and the bottom buttons
            setViewPager()
            setButtons()
        }else{
            // If there is an error with the image position, go back to the previous fragment
            supportFragmentManager.errorShowImage()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method that sets up the viewpager
    private fun setViewPager(){
        // Create the ImageSliderAdapter
        adapter = ImageSliderAdapter(this.requireContext())
        adapter.setImagesList(galleryViewModel.getImagesList())

        // Set the adapter and the layoutManager of the viewPager
        binding.viewPagerGallery.adapter = adapter
        binding.viewPagerGallery.setCurrentItem(imagePosition, false)

        binding.viewPagerGallery.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            //Listener when the image is changed
            override fun onPageSelected(position: Int) {
                // The previous image is re-loaded in case the user has done a zoom, in order so the image has not zoom when the image is back
                adapter.notifyItemChanged(imagePosition)
                // Set the new position
                imagePosition = position
                super.onPageSelected(position)
            }
        })

    }

    // Method that set up the buttons of the fragment
    private fun setButtons(){
        // Info button
        binding.layoutBtnInfoImage.setOnClickListener {
            val image = galleryViewModel.getImage(imagePosition)
            if(image != null){
                // Show the image details
                supportFragmentManager.showImageDetails(image)
            }
        }

        // Remove button
        binding.layoutBtnRemoveImage.setOnClickListener {
            val image = galleryViewModel.getImage(imagePosition)

            if(image != null){
                // Remove the image
                val imageDeleted = supportFragmentManager.removeImage(image)
                if(imageDeleted){
                    //If the image has been deleted successfully, notify to the adapter that an element has been removed
                    adapter.notifyItemRemoved(imagePosition)
                }
            }
        }
    }
}
package com.cice.photogallery.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.cice.photogallery.databinding.FragmentGalleryBinding
import com.cice.photogallery.viewmodels.GalleryViewModel
import com.cice.photogallery.views.activities.MainActivity
import com.cice.photogallery.views.adapters.GalleryAdapter
import com.cice.photogallery.views.interfaces.SupportFragmentManager
import java.io.IOException

class GalleryFragment : Fragment() {

    private val TAG = "GalleryFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager

    private var _binding: FragmentGalleryBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var rootView: View

    //Adapter for the recyclerview
    private lateinit var adapter: GalleryAdapter

    //Viewmodel
    private val galleryViewModel: GalleryViewModel by activityViewModels()

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
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        rootView = binding.root

        // Set the button logic, the observer and the recyclerview of the fragment
        setButton()
        setObserver()
        setRecyclerView()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Fun that set up the observer for all the gallery items
    private fun setObserver(){
        galleryViewModel.imagesGallery.observe(this, Observer { imagesGallery ->
            // Set the image list of the adapter and notify the change to it
            adapter.setImagesList(imagesGallery)
            adapter.notifyDataSetChanged()
        })
    }

    // Function that notifies to the adapter that a new item has been added to the gallery at the bottom
    fun addItemToGallery(){
        adapter.notifyItemInserted(adapter.itemCount)
    }

    //Function that sets up the recyclerview for the gallery
    private fun setRecyclerView(){
        // Create the GalleryAdapter
        adapter = GalleryAdapter(this.requireContext())

        adapter.setOnItemClickListener(object : GalleryAdapter.OnItemClickListener{
            // An item of the gallery is selected
            override fun onItemClick(position: Int) {
                val image = galleryViewModel.getImage(position)
                // If the image is not null, communicate with the activity in order to show the image in full screen
                if(image != null){
                    supportFragmentManager.showImage(position)
                }
            }

        })

        // Set the adapter and the layoutManager of the recyclerView
        binding.recyclerViewGallery.adapter = adapter
        binding.recyclerViewGallery.layoutManager = GridLayoutManager(this.requireContext(), 4)
    }

    //Set the logic of the camera button
    private fun setButton(){
        //Camera button
        binding.btnTakePicture.setOnClickListener {
            // Communicate with the activity in order to take the photo
            supportFragmentManager.takePhoto()
        }
    }
}
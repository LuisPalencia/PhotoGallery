package com.cice.photogallery.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cice.photogallery.databinding.FragmentNoPermissionsBinding
import com.cice.photogallery.views.activities.MainActivity
import com.cice.photogallery.views.interfaces.SupportFragmentManager
import java.io.IOException

class NoPermissionsFragment : Fragment() {

    private val TAG = "GalleryFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager

    private var _binding: FragmentNoPermissionsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var rootView: View

    override fun onAttach(context: Context) {
        super.onAttach(context)

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
        _binding = FragmentNoPermissionsBinding.inflate(inflater, container, false)
        rootView = binding.root

        setButton()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //Set the logic of the check storage permissions button
    private fun setButton(){
        binding.btnCheckStoragePermissions.setOnClickListener{
            supportFragmentManager.checkStoragePermission()
        }
    }


}
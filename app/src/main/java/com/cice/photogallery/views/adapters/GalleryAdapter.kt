package com.cice.photogallery.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cice.photogallery.R
import com.cice.photogallery.models.Image


class GalleryAdapter(private val context: Context): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var TAG = "GalleryAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<Image>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // Function when an item of the gallery is selected
    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    // function in order to set the list of images
    fun setImagesList(images: MutableList<Image>){
        data = images
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgPhotoGallery: ImageView

        init {
            imgPhotoGallery = view.findViewById(R.id.imgPhotoGallery)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_gallery, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Show the image in the ImageView
        Glide.with(context)
            .asBitmap()
            .load(data[position].uri)
            .into(viewHolder.imgPhotoGallery)

        // Set the onclicklistener
        viewHolder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.nasaPhoto

class nasaPhotoListAdapter : RecyclerView.Adapter<nasaPhotoListAdapter.nasaPhotoHolder>() {
    var nasaPhotoList = mutableListOf<nasaPhoto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): nasaPhotoHolder {
        return nasaPhotoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.nasa_photo_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return nasaPhotoList.size
    }

    override fun onBindViewHolder(holder: nasaPhotoHolder, position: Int) {
        holder.bind(nasaPhotoList[position])
    }

    class nasaPhotoHolder(root: View) : RecyclerView.ViewHolder(root) {
        var photoImage : ImageView
        var photoDescription : TextView

        init {
            photoImage = itemView.findViewById(R.id.photo_image_view)
            photoDescription = itemView.findViewById(R.id.phot_description)
        }

        fun bind(photo: nasaPhoto) {

        }
    }
}
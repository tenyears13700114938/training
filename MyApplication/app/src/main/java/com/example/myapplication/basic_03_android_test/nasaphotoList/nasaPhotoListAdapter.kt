package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.nasaPhoto

class nasaPhotoListAdapter : PagedListAdapter<nasaPhoto,nasaPhotoListAdapter.nasaPhotoHolder>(Diff_Callback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): nasaPhotoHolder {
        return nasaPhotoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.nasa_photo_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: nasaPhotoHolder, position: Int) {
        getItem(position)?.also {
            holder.bind(it)
        }
    }

    class nasaPhotoHolder(root: View) : RecyclerView.ViewHolder(root) {
        var photoImage : ImageView
        var photoDescription : TextView

        init {
            photoImage = itemView.findViewById(R.id.photo_image_view)
            photoDescription = itemView.findViewById(R.id.phot_description)
        }

        fun bind(photo: nasaPhoto) {
            photoDescription.text = photo.title
            photoImage.layoutParams.height = photoImage.resources.displayMetrics.widthPixels / 2
            photoImage.layoutParams.width = photoImage.layoutParams.height
            Glide.with(photoImage)
                .asBitmap()
                .load(if(photo.media_type.equals("image")) photo.url else R.drawable.saturn_card_view_default)
                .into(photoImage)

        }
    }

    class Diff_Callback : DiffUtil.ItemCallback<nasaPhoto>(){
        override fun areItemsTheSame(oldItem: nasaPhoto, newItem: nasaPhoto): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: nasaPhoto, newItem: nasaPhoto): Boolean {
            return oldItem.url == newItem.url
        }
    }
}
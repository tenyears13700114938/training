package com.example.myapplication.basic_03_android_test.nasaphotoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
import com.example.myapplication.util.getNasaPhotoFile
import io.reactivex.subjects.PublishSubject

class nasaPhotoListAdapter : PagedListAdapter<NasaPhotoEntity,nasaPhotoListAdapter.nasaPhotoHolder>(Diff_Callback()) {
    val clickPublishSubject = PublishSubject.create<NasaPhotoEntity>()
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
            holder.itemView.setOnClickListener{_view ->
                clickPublishSubject.onNext(it)
            }
        }
    }

    class nasaPhotoHolder(root: View) : RecyclerView.ViewHolder(root) {
        var photoImage : ImageView
        var photoDescription : TextView

        init {
            photoImage = itemView.findViewById(R.id.photo_image_view)
            photoDescription = itemView.findViewById(R.id.phot_description)
        }

        fun bind(photo: NasaPhotoEntity) {
            photoDescription.text = photo.title
            photoImage.layoutParams.height = photoImage.resources.displayMetrics.widthPixels / 2
            photoImage.layoutParams.width = photoImage.layoutParams.height
            getNasaPhotoFile(photo, photoImage.context).also { _photoFile ->
                if (_photoFile.exists()) {
                    Glide.with(photoImage)
                        .asBitmap()
                        .load(_photoFile)
                        .into(photoImage)
                } else {
                    Glide.with(photoImage)
                        .asBitmap()
                        .load(if (photo.media_type.equals("image")) photo.url else R.drawable.saturn_card_view_default)
                        .into(photoImage)
                }
            }

        }

    }

    class Diff_Callback : DiffUtil.ItemCallback<NasaPhotoEntity>(){
        override fun areItemsTheSame(oldItem: NasaPhotoEntity, newItem: NasaPhotoEntity): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: NasaPhotoEntity, newItem: NasaPhotoEntity): Boolean {
            return oldItem.url == newItem.url
        }
    }
}
package com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.nasaPhotoEntity
import com.example.myapplication.util.getNasaPhotoFile

class nasaPhotoDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = nasaPhotoDetailsFragment()
    }

    private lateinit var viewModel: nasaPhotoDetailsViewModel
    private lateinit var dateTextView : TextView
    private lateinit var titleTextView : TextView
    private lateinit var explantionTextView : TextView
    private lateinit var photoImageView : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.nava_photo_details_fragment, container, false).also {
            dateTextView = it.findViewById(R.id.nasa_photo_date)
            titleTextView = it.findViewById(R.id.nasa_photo_Title)
            explantionTextView = it.findViewById(R.id.nasa_photo_explantion)
            photoImageView = it.findViewById(R.id.nasa_photo_image)

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(nasaPhotoDetailsViewModel::class.java)
        viewModel.nasaPhotoInfo?.also{
            show(it)
        }
    }

    fun show(entity : nasaPhotoEntity) {
        dateTextView.text = entity.date
        titleTextView.text = entity.title
        explantionTextView.text = entity.explanation

        photoImageView.layoutParams.height = photoImageView.resources.displayMetrics.widthPixels
        photoImageView.layoutParams.width = photoImageView.layoutParams.height

        getNasaPhotoFile(entity, photoImageView.context).also {_photoFile ->
            if (_photoFile.exists()) {
                Glide.with(photoImageView)
                    .asBitmap()
                    .load(_photoFile)
                    .into(photoImageView)
            } else {
                Glide.with(photoImageView)
                    .asBitmap()
                    .load(if (entity.media_type.equals("image")) entity.url else R.drawable.saturn_card_view_default)
                    .into(photoImageView)
            }
        }
    }

}

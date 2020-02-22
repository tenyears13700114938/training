package com.example.myapplication.basic_03_android_test.nasaPhotoContentDetails

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GestureDetectorCompat
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.model.NasaPhotoEntity
import com.example.myapplication.basic_03_android_test.nasaphotoRepository.nasaRepository
import com.example.myapplication.util.getDateStr
import com.example.myapplication.util.getNasaPhotoFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlin.math.abs

class NasaPhotoDetailsFragment : Fragment() {
    companion object {
        val TAG = NasaPhotoDetailsFragment::class.java.simpleName
        fun newInstance() = NasaPhotoDetailsFragment()
    }

    private lateinit var viewModel: NasaPhotoDetailsViewModel
    private lateinit var dateTextView : TextView
    private lateinit var titleTextView : TextView
    private lateinit var explantionTextView : TextView
    private lateinit var photoImageView : ImageView
    private lateinit var mDetector : GestureDetectorCompat
    private val actionSubject : PublishSubject<GestureAction> = PublishSubject.create();
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.nava_photo_details_fragment, container, false).also {
            dateTextView = it.findViewById(R.id.nasa_photo_date)
            titleTextView = it.findViewById(R.id.nasa_photo_Title)
            explantionTextView = it.findViewById(R.id.nasa_photo_explantion)
            photoImageView = it.findViewById(R.id.nasa_photo_image)

            mDetector = GestureDetectorCompat(this@NasaPhotoDetailsFragment.context, PhotoSwipeListener(actionSubject))
            it.setOnTouchListener{_view ,_event ->
                //Log.d(TAG, "fragment root onTouch")
                mDetector.onTouchEvent(_event)
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(NasaPhotoDetailsViewModel::class.java)
        viewModel.nasaPhotoInfo?.also {
            show(it)
        }
        compositeDisposable.add(actionSubject.subscribe {
            @RequiresApi(Build.VERSION_CODES.O)
            fun loadPhoto(preOrNext : Boolean){
                viewModel.nasaPhotoInfo?.let { _photoEntity ->
                    val targetDate= getDateStr(_photoEntity.date, if(preOrNext) 1 else -1)
                    nasaRepository.getNasaPhotoFromDb(this@NasaPhotoDetailsFragment.requireContext(), targetDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{_nasaPhoto ->
                            viewModel.nasaPhotoInfo = _nasaPhoto
                            show(_nasaPhoto)
                        }
                }
            }

            when (it) {
                GestureAction.SWIPE_RIGHT -> {
                    loadPhoto(true)
                }
                GestureAction.SWIPE_LEFT -> {
                    loadPhoto(false)
                }
                else -> Log.d(TAG, "else")
            }
        })
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    fun show(entity : NasaPhotoEntity) {
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

    enum class GestureAction(val swipeAction : Int) {
        SWIPE_LEFT(0),
        SWIPE_RIGHT(1);
    }

    class PhotoSwipeListener(val swipeSubject : PublishSubject<GestureAction>) : GestureDetector.SimpleOnGestureListener() {
        val SWIPE_THRESHOLD = 100
        val SWIPE_VELOCITY_THRESHOLD = 100
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if(e1 is MotionEvent && e2 is MotionEvent){
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                Log.d(TAG, "diffX :${diffX} diffY${diffY} velocityX: $velocityX")
                if ((abs(diffX) > abs(diffY) && abs(diffX) > SWIPE_THRESHOLD) &&
                    abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if(diffX > 0){
                        onSwipeRight()
                    }
                    else {
                        onSwipeLeft()
                    }
                }
            }
            return true
        }

        private fun onSwipeLeft() {
            Log.d(NasaPhotoDetailsFragment.TAG, "onSwipeLeft")
            swipeSubject.onNext(GestureAction.SWIPE_LEFT)
        }

        private fun onSwipeRight() {
            Log.d(NasaPhotoDetailsFragment.TAG, "onSwipeRight")
            swipeSubject.onNext(GestureAction.SWIPE_RIGHT)
        }
    }
}

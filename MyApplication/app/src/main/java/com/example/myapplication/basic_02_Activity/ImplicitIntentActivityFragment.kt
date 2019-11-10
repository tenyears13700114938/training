package com.example.myapplication.basic_02_Activity

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.R

/**
 * A placeholder fragment containing a simple view.
 */
class ImplicitIntentActivityFragment : Fragment() {
    lateinit var mWebEditText : EditText
    lateinit var mLocationEditText : EditText
    lateinit var mShareEditText: EditText
    val TAG = ImplicitIntentActivityFragment::class.java.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_implicit_intent, container, false)
            .also { _rootView ->
                mLocationEditText = _rootView.findViewById(R.id.locationEditText)
                mWebEditText = _rootView.findViewById(R.id.openWebsiteEditText)
                mShareEditText = _rootView.findViewById(R.id.shareTextEditText)

                _rootView.findViewById<Button>(R.id.openWebSiteButton).setOnClickListener(openWebClickListener)

                _rootView.findViewById<Button>(R.id.openLocationButton).setOnClickListener(openLocationClickListener)

                _rootView.findViewById<Button>(R.id.shareInfoButton).setOnClickListener(shareInfoClickListener)

                _rootView.findViewById<Button>(R.id.takePhotoButton).setOnClickListener (takePhotoClickListener)
            }
    }

    private val openWebClickListener = View.OnClickListener { _openWebBtn ->
        mWebEditText.text.toString().also { _url ->
            var intent = Intent(ACTION_VIEW, Uri.parse(_url))
            if(activity != null && activity is FragmentActivity) {
                var fragmentActivity = activity as FragmentActivity
                if (intent.resolveActivity(fragmentActivity.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d(TAG, "no activity can reaction");
                }
            }
        }
    }

    private val openLocationClickListener = View.OnClickListener { _openLocationBtn ->
        mLocationEditText.text.toString().also { _location ->
            val intent = Intent(ACTION_VIEW, Uri.parse("geo:0,0?q=$_location"))
            if (activity != null && activity is FragmentActivity) {
                val fragmentActivity = activity as FragmentActivity
                if (intent.resolveActivity(fragmentActivity.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.d(TAG, "no activity can location")
                }
            }
        }
    }

    private val shareInfoClickListener = View.OnClickListener { _shareInfoBtn ->
        mShareEditText.text.toString().also { _shareText ->
            ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setChooserTitle("Share text with:")
                .setText(_shareText)
                .startChooser()
        }
    }

    private val takePhotoClickListener = View.OnClickListener { _takePhotoBtn ->
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity != null && activity is FragmentActivity) {
            val fragmentActivity = activity as FragmentActivity
            if (intent.resolveActivity(fragmentActivity.packageManager) != null) {
                startActivity(intent)
            } else {
                Log.d(TAG, "no activity can take photo")
            }
        }
    }
}

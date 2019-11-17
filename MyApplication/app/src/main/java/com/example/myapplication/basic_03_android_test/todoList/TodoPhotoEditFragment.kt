package com.example.myapplication.basic_03_android_test.todoList


import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

import com.example.myapplication.R
import java.io.File
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 */
private const val REQUEST_CODE_CAMERA_PERMISSION = 10

class TodoPhotoEditFragment : Fragment() {
    private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private lateinit var  viewFinder : TextureView
    private val captureExecutor = Executors.newSingleThreadExecutor()
    private lateinit var todoViewModel : TodoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_photo_edit, container, false)
        view.findViewById<Button>(R.id.next_button).let {
            if (it is Button)
                it.text = "OK"
            it
        }.setOnClickListener {
            (activity as TodoListActivity).saveTodo(todoViewModel.todoInfo)
            it.findNavController().popBackStack(R.id.todoListFragment, false)
        }

        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            it.findNavController().popBackStack()
        }

        viewFinder = view.findViewById<TextureView>(R.id.view_finder).also {
           it.addOnLayoutChangeListener {_,_,_,_,_,_,_,_,_->
                updateTransform()
            }
        }

        todoViewModel = activity?.run{
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view

        if (allPermissionGranted()) {
            viewFinder.post { startCamera() }
        } else {
            activity?.run{
                ActivityCompat.requestPermissions(activity as FragmentActivity, REQUEST_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSION)
            }
        }

        if(activity is TodoListActivity){
            (activity as TodoListActivity).setTitle("Take a photo")
        }
        return view
    }

    private fun startCamera(){
        //preview config
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(500, 500))
        }.build()
        val preview = Preview(previewConfig).also {
            it.setOnPreviewOutputUpdateListener { _previewOutput ->
                val parent = viewFinder.parent as ViewGroup
                parent.removeView(viewFinder)
                parent.addView(viewFinder, 0)
                viewFinder.surfaceTexture = _previewOutput.surfaceTexture
                updateTransform()
            }
        }

        //capture config
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
        }.build()
        val imageCapture = ImageCapture(imageCaptureConfig)
        view?.findViewById<ImageButton>(R.id.imageButton)?.setOnClickListener{
            val file = File(it.context.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
            imageCapture.takePicture(file, captureExecutor, object: ImageCapture.OnImageSavedListener {
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    cause: Throwable?
                ) {
                    val msg = "photo capture failed:$message"
                    Log.e("CameraX", message)
                    viewFinder.post {
                        Toast.makeText(viewFinder.context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onImageSaved(file: File) {
                    todoViewModel.todoInfo.imageUrl = file.absolutePath
                }
            })
        }

        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun updateTransform() {
        val matrix = android.graphics.Matrix()

        val centerx = viewFinder.width / 2f
        val centery = viewFinder.height / 2f

        val rotationDegrees = when(viewFinder.display.rotation){
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotationDegrees.toFloat(), centerx, centery)
        viewFinder.setTransform(matrix)
    }

    private fun allPermissionGranted() = REQUEST_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this.requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (allPermissionGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this.requireContext(), "can not be granted by user", Toast.LENGTH_SHORT).show()
                viewFinder.findNavController().popBackStack()
            }
        }
    }
}

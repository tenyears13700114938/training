package com.example.myapplication.basic_03_android_test.todoList


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Size
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util

import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailActivity
import com.example.myapplication.basic_03_android_test.todoDetail.TodoDetailViewModel
import com.example.myapplication.util.copyTodo
import com.example.myapplication.util.getFileDirs
import com.example.myapplication.util.toBitmapFile
import java.io.File
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 */
private const val REQUEST_CODE_CAMERA_PERMISSION = 10

class TodoPhotoEditFragment : Fragment() {
    private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private lateinit var  viewFinder : TextureView
    private lateinit var mPhotoImage : ImageView
    private val captureExecutor = Executors.newSingleThreadExecutor()
    private lateinit var todoViewModel : TodoViewModel
    private lateinit var todoDetailViewModel: TodoDetailViewModel
    private var preview : Preview? = null
    private var imageCapture : ImageCapture? = null
    private lateinit var imageButton: ImageButton
    private val args : TodoPhotoEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo_photo_edit, container, false)
        view.findViewById<View>(R.id.next_button).let {
            if (it is Button)
                it.text = "OK"
        it
    }.setOnClickListener {
            if(!TextUtils.isEmpty(todoViewModel.todoInfo.imageUrl)){
                var originalFile = todoViewModel.todoInfo.imageUrl
                todoViewModel.todoInfo.imageUrl = toBitmapFile(originalFile!!, 400)
                File(originalFile).delete()

            }
        if (args.editType == TodoEditType.CREATE) {
            it.findNavController().popBackStack(R.id.todoListFragment, false)
            (activity as TodoListActivity).saveTodo(todoViewModel.todoInfo)
        } else {
            copyTodo(todoViewModel.todoInfo, todoDetailViewModel.todoDetail.value!!)
            (activity as TodoDetailActivity).updateTodo(todoDetailViewModel.todoDetail.value!!)
            it.findNavController().popBackStack(R.id.todoDetailFragment, false)
        }
    }

    view.findViewById<Button>(R.id.back_button).setOnClickListener {
        it.findNavController().popBackStack()
    }

        mPhotoImage = view.findViewById(R.id.todo_image)
        imageButton = view.findViewById(R.id.image_button)
        imageButton.setOnClickListener { _imageButton ->
            if (TextUtils.isEmpty(todoViewModel.todoInfo.imageUrl)) {
                val file =
                    File(
                        getFileDirs("todoPhoto", context!!),
                        "${System.currentTimeMillis()}.jpg"
                    )
                imageCapture?.takePicture(
                    file,
                    captureExecutor,
                    object : ImageCapture.OnImageSavedListener {
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
                            viewFinder.post {
                                todoViewModel.todoInfo.imageUrl = file.absolutePath
                                configPhotoImageOrCamera()
                            }
                        }
                    })


            } else {
                viewFinder.post {
                    if (!TextUtils.isEmpty(todoViewModel.todoInfo.imageUrl)) {
                        File(todoViewModel.todoInfo.imageUrl!!).delete()
                    }
                    todoViewModel.todoInfo.imageUrl = null
                    configPhotoImageOrCamera()
                }
            }
        }

        viewFinder = view.findViewById<TextureView>(R.id.view_finder).also {
           it.addOnLayoutChangeListener {_,_,_,_,_,_,_,_,_->
                updateTransform()
            }
        }

        todoViewModel = activity?.run{
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view

        todoDetailViewModel = activity?.run {
            ViewModelProviders.of(this).get(TodoDetailViewModel::class.java)
        } ?: return view

        configPhotoImageOrCamera()

        if(activity is NavCommonActivity){
            (activity as NavCommonActivity).setTitle("Take a photo")
        }
        return view
    }

    private fun configPhotoImageOrCamera() {
        if(TextUtils.isEmpty(todoViewModel.todoInfo.imageUrl)) {
            mPhotoImage.visibility = INVISIBLE
            viewFinder.visibility = VISIBLE

            if (allPermissionGranted()) {
                viewFinder.post { startCamera() }
            } else {
                activity?.run {
                    ActivityCompat.requestPermissions(
                        activity as FragmentActivity,
                        REQUEST_PERMISSIONS,
                        REQUEST_CODE_CAMERA_PERMISSION
                    )
                }
            }
        }
        else {
            mPhotoImage.visibility = VISIBLE
            viewFinder.visibility = INVISIBLE
            Glide.with(this).load(todoViewModel.todoInfo.imageUrl).centerCrop().into(mPhotoImage)
            stopCamera()
        }
        Glide.with(this).load(android.R.drawable.ic_menu_camera).into(imageButton)
    }

    private fun startCamera(){
        //preview config
        if (preview == null || imageCapture == null) {
            val previewConfig = PreviewConfig.Builder().apply {
                setTargetResolution(Size(800, 600))
            }.build()
            preview = Preview(previewConfig).also {
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
            imageCapture = ImageCapture(imageCaptureConfig)
            CameraX.bindToLifecycle(this, preview, imageCapture)
        }
    }

    private fun stopCamera(){
        /*if(preview != null && imageCapture != null){
            CameraX.unbind(preview, imageCapture)
        }*/
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

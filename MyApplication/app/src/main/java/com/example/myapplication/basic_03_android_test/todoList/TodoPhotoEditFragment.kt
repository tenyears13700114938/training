package com.example.myapplication.basic_03_android_test.todoList


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.basic_03_android_test.Flux.CoroutineDispatcher
import com.example.myapplication.basic_03_android_test.Flux.TodoActionCreator
import com.example.myapplication.basic_03_android_test.Flux.TodoStore
import com.example.myapplication.basic_03_android_test.activityCommon.NavCommonActivity
import com.example.myapplication.basic_03_android_test.model.Todo
import com.example.myapplication.basic_03_android_test.model.TodoEditType
import com.example.myapplication.databinding.FragmentTodoPhotoEditBinding
import com.example.myapplication.util.getFileDirs
import com.example.myapplication.util.toBitmapFile
import com.google.android.material.transition.MaterialSharedAxis
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.nav_buttons.view.*
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
private const val REQUEST_CODE_CAMERA_PERMISSION = 10

class TodoPhotoEditFragment : DaggerFragment() {
    private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val captureExecutor = Executors.newSingleThreadExecutor()

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    lateinit var todoStore: TodoStore

    @Inject
    lateinit var actionCreator: TodoActionCreator

    lateinit var binding: FragmentTodoPhotoEditBinding

    //private lateinit var todoDetailViewModel: TodoDetailViewModel
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var photoImageUrl: String? = null
    private val args: TodoPhotoEditFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*(activity as TodoListActivity).todoListComponent.inject(this)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodoPhotoEditBinding.inflate(inflater, container, false)
        binding.navButtons.next_button.apply {
            if (this is Button)
                text = "OK"

            setOnClickListener {
                val todo = todoStore.editingTodo.value ?: Todo()
                todo.imageUrl?.let {
                    if (!TextUtils.isEmpty(it) && !it.contains("bitmap")) {
                        var originalFile = it
                        todo.imageUrl = toBitmapFile(originalFile!!, 800, 600)
                        File(originalFile).delete()
                    }
                }
                todo.imageUrl = photoImageUrl
                if (args.editType == TodoEditType.CREATE) {
                    it.findNavController().popBackStack(R.id.todoListFragment, false)
                    dispatcher.dispatch(actionCreator.addTodo(todo))
                    dispatcher.dispatch(actionCreator.editedTodo(Todo()))
                } else {
                    //copyTodo(todoViewModel.todoInfo, todoDetailViewModel.todoDetail.value!!)
                    dispatcher.dispatch(actionCreator.updateTodo(todo))
                    it.findNavController().popBackStack(R.id.todoDetailFragment, false)
                }
            }
        }

        binding.navButtons.back_button.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.imageButton.setOnClickListener { _imageButton ->
            if (TextUtils.isEmpty(todoStore.editingTodo.value?.imageUrl)) {
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
                            binding.viewFinder.post {
                                Toast.makeText(binding.viewFinder.context, msg, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onImageSaved(file: File) {
                            binding.viewFinder.post {
                                photoImageUrl = file.absolutePath
                                configPhotoImageOrCamera()
                            }
                        }
                    })


            } else {
                binding.viewFinder.post {
                    if (!TextUtils.isEmpty(photoImageUrl)) {
                        File(photoImageUrl).delete()
                    }
                    photoImageUrl = null
                    configPhotoImageOrCamera()
                }
            }
        }
        photoImageUrl = todoStore.editingTodo.value?.imageUrl

        binding.viewFinder.apply {
            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTransform()
            }
        }

        /*todoViewModel = activity?.run{
            ViewModelProviders.of(this).get(TodoViewModel::class.java)
        } ?: return view*/

        /*todoDetailViewModel = activity?.run {
            ViewModelProviders.of(this).get(TodoDetailViewModel::class.java)
        } ?: return view*/

        configPhotoImageOrCamera()

        if (activity is NavCommonActivity) {
            (activity as NavCommonActivity).setTitle("Take a photo")
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backward =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.X, false).apply {
                secondaryTransition = null
                duration = 100
            }
        exitTransition = backward

        val forward =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.X, true).apply {
                secondaryTransition = null
                duration = 100
            }
        enterTransition = forward
    }

    private fun configPhotoImageOrCamera() {
        if (TextUtils.isEmpty(photoImageUrl)) {
            binding.todoImage.visibility = INVISIBLE
            binding.viewFinder.visibility = VISIBLE

            if (allPermissionGranted()) {
                binding.viewFinder.post { startCamera() }
            } else {
                //activity?.run {
                //ActivityCompat.requestPermissions(
                //activity as FragmentActivity,
                requestPermissions(
                    REQUEST_PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSION
                )
                //}
            }
        } else {
            binding.todoImage.visibility = VISIBLE
            binding.viewFinder.visibility = INVISIBLE
            Glide.with(this).load(photoImageUrl).centerCrop()
                .into(binding.todoImage)
            stopCamera()
        }
        Glide.with(this).load(android.R.drawable.ic_menu_camera).into(binding.imageButton)
    }

    private fun startCamera() {
        //preview config
        if (preview == null || imageCapture == null) {
            val previewConfig = PreviewConfig.Builder().apply {
                setTargetResolution(Size(800, 600))
            }.build()
            preview = Preview(previewConfig).also {
                it.setOnPreviewOutputUpdateListener { _previewOutput ->
                    val parent = binding.viewFinder.parent as ViewGroup
                    parent.removeView(binding.viewFinder)
                    parent.addView(binding.viewFinder, 0)
                    binding.viewFinder.surfaceTexture = _previewOutput.surfaceTexture
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

    private fun stopCamera() {
        /*if(preview != null && imageCapture != null){
            CameraX.unbind(preview, imageCapture)
        }*/
    }

    private fun updateTransform() {
        val matrix = android.graphics.Matrix()

        val centerx = binding.viewFinder.width / 2f
        val centery = binding.viewFinder.height / 2f

        val rotationDegrees = when (binding.viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotationDegrees.toFloat(), centerx, centery)
        binding.viewFinder.setTransform(matrix)
    }

    private fun allPermissionGranted() = REQUEST_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this.requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (allPermissionGranted()) {
                binding.viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "can not be granted by user",
                    Toast.LENGTH_SHORT
                ).show()
                binding.viewFinder.findNavController().popBackStack()
            }
        }
    }
}

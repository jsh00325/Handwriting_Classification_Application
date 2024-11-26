package com.classification.handwriting.presentation.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.classification.handwriting.R
import com.classification.handwriting.databinding.ActivityCameraBinding
import com.classification.handwriting.domain.model.ModelItem
import com.classification.handwriting.presentation.IntentContract.CROPPED_IMAGE_EXTRA_NAME
import com.classification.handwriting.presentation.IntentContract.MODEL_LIST_EXTRA_NAME
import com.classification.handwriting.presentation.model_result.ModelResultActivity
import java.io.ByteArrayOutputStream

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var selectedModelList: List<ModelItem>
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null

    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, R.string.camera_require_permission, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSelectedModelList()
        requestCameraPermission()
        setupShutterButton()
    }

    private fun getSelectedModelList() {
        selectedModelList = intent.getParcelableArrayListExtra(MODEL_LIST_EXTRA_NAME) ?: emptyList()
    }

    private fun requestCameraPermission() {
        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)
    }

    private fun setupShutterButton() {
        binding.cameraShutterButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()

            // 후면 카메라 선택
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 기존 바인딩 해제
                cameraProvider?.unbindAll()

                // 새로운 바인딩 생성
                camera = cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                // PreviewView에 프리뷰 연결
                preview?.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)

            } catch (e: Exception) {
                Toast.makeText(this, R.string.camera_binding_error, Toast.LENGTH_SHORT).show()
                finish()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = image.toBitmap()
                    val rotatedBitmap = rotateBitmap(bitmap)
                    val croppedBitmap = cropBitmap(rotatedBitmap)
                    val resizedBitmap = resizeBitmap(croppedBitmap)

                    navigateToModelResultActivity(resizedBitmap)

                    image.close()
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        R.string.camera_capture_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(ROTATE_DEGREE)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val previewView = binding.cameraPreviewView
        val guideBox = binding.cameraGuideBoundingBox

        val scaleX = bitmap.width.toFloat() / previewView.width
        val scaleY = bitmap.height.toFloat() / previewView.height

        val cropStartX = (guideBox.left * scaleX).toInt()
        val cropStartY = (guideBox.top * scaleY).toInt()

        val targetWidth = (guideBox.width * scaleX).toInt()
        val targetHeight = (guideBox.height * scaleY).toInt()

        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, targetWidth, targetHeight)
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, RESIZE_WIDTH, RESIZE_HEIGHT, false)
    }

    private fun navigateToModelResultActivity(resultBitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val intent = Intent(this, ModelResultActivity::class.java).apply {
            putParcelableArrayListExtra(MODEL_LIST_EXTRA_NAME, ArrayList(selectedModelList))
            putExtra(CROPPED_IMAGE_EXTRA_NAME, byteArray)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
    }

    companion object {
        private const val ROTATE_DEGREE = 90f
        private const val RESIZE_WIDTH = 180
        private const val RESIZE_HEIGHT = 76
    }
}
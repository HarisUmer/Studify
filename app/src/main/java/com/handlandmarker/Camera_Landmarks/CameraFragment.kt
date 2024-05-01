package com.handlandmarker.Camera_Landmarks

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.handlandmarker.Canvas.CustomView
import com.handlandmarker.HandLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(private val context: Context,private val life: LifecycleOwner, private  var customView: CustomView) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var backgroundExecutor: ExecutorService
    private lateinit var handLandmarkerHelper: HandLandmarkerHelper


    fun initializeCamera() {
        // Initialize the background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Initialize camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
            initializeHandLandmarkerHelper()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return

        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        imageAnalyzer = ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(backgroundExecutor) { image ->
                    detectHandGesture(image)
                }
            }

        try {
            camera = cameraProvider.bindToLifecycle(life, cameraSelector, imageAnalyzer)
        } catch (exc: Exception) {
            // Handle camera binding exception
        }
    }

    private fun initializeHandLandmarkerHelper() {
        backgroundExecutor.execute {
            handLandmarkerHelper = HandLandmarkerHelper(
                context = context,
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = 0.5F,
                minHandTrackingConfidence = 0.5F,
                minHandPresenceConfidence = 0.5F,
                maxNumHands = 1, // Adjust as per your requirement
                currentDelegate = HandLandmarkerHelper.DELEGATE_CPU,
                handLandmarkerHelperListener = object : HandLandmarkerHelper.LandmarkerListener {
                    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {
                        // Handle hand gesture recognition results
                        if(resultBundle.results.isNotEmpty()) {
                            var p1 = resultBundle.results.get(0)
                            var minX = Float.MAX_VALUE
                            var minY = Float.MAX_VALUE
                            for (hand in p1.landmarks()) {
                                if(hand.size==21)
                                {


                                    for (land in hand) {
                                        val x = land.x()
                                        val y = land.y()
                                        minX = minOf(minX, x)
                                        minY = minOf(minY, y)

                                    }
                                    customView.post {
                                        customView.drawWithCoordinates(minX, minY)
                                    }
                                }
                            }
                        }
                    }

                    override fun onError(error: String, errorCode: Int) {
                        // Handle error from HandLandmarkerHelper
                        Log.e("HandGesture", "Error: $error, ErrorCode: $errorCode")
                    }
                }
            )
        }
    }


    private fun detectHandGesture(imageProxy: ImageProxy) {
        handLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = true // Adjust according to your camera setup
        )
    }

    fun releaseCamera() {
        backgroundExecutor.shutdown()
        cameraProvider?.unbindAll()
        handLandmarkerHelper.clearHandLandmarker()
    }
}

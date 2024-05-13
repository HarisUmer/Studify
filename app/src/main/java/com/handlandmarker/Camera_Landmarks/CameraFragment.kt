package com.handlandmarker.Camera_Landmarks
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.handlandmarker.Canvas.CustomView
import com.handlandmarker.HandLandmarkerHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.*
import com.fasterxml.jackson.databind.ObjectMapper;

import kotlinx.coroutines.*

class CameraManager(private val context: Context, private val life: LifecycleOwner, private var customView: CustomView) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var backgroundExecutor: ExecutorService
    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
    private val coroutineScope = CoroutineScope(Dispatchers.Default) // Coroutine scope

    var url: String = "https://14ec-223-123-6-14.ngrok-free.app/api/predict"

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
                        if (resultBundle.results.isNotEmpty()) {
                            var arr: ArrayList<Float> = ArrayList()
                            var p1 = resultBundle.results.get(0)
                            var minX = Float.MAX_VALUE
                            var minY = Float.MAX_VALUE
                            for (hand in p1.landmarks()) {
                                if (hand.size == 21) {
                                    for (land in hand) {
                                        val x = land.x()
                                        val y = land.y()
                                        arr.add(x)
                                        arr.add(y)
                                        if (x < minX) minX = x
                                        if (y < minY) minY = y
                                    }
                                    val dataAux: ArrayList<Float> = ArrayList()
                                    for (i in 0 until arr.size step 2) {
                                        val xRelative = arr[i] - minX
                                        val yRelative = arr[i + 1] - minY
                                        dataAux.add(xRelative)
                                        dataAux.add(yRelative)
                                    }
                                    customView.post{
                                        customView.drawWithCoordinates(minX,minY)
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


    private suspend fun postArrayListToServer(arrayList: ArrayList<Float>,minX:Float, minY:Float) {
        try {
            // Convert ArrayList to JSONArray
            val jsonArray = JSONArray(arrayList)

            // Convert JSONArray to string
            val jsonString = jsonArray.toString()

            // Make the POST request
            url.httpPost()
                .header("Content-Type" to "application/json")
                .body(jsonString)
                .response { _, response, result ->
                    when (result) {
                        is Result.Success -> {
                            val jsonResponse = String(response.data)
                            val jsonObject = JSONObject(jsonResponse)
                            val responseData = jsonObject.optString("prediction")
                            Log.d("Predictionnn", "$responseData")
                            // Process the prediction here if needed
                            processPrediction(responseData,minX,minY)
                        }
                        is Result.Failure -> {
                            Log.d("Error in result", "${result.error}")
                        }
                    }
                }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    private fun processPrediction(prediction: String,minX : Float, minY : Float) {
        // Handle the prediction result here
        // Implement your logic based on the prediction string
        // For example:
        if (prediction == "one") {
            customView.post{
                customView.moveCursor(minX,minY)
            }
            // Do something for prediction "one"
        } else if (prediction == "two") {
            customView.post{
                customView.drawWithCoordinates(minX,minY)
            }
            // Do something for prediction "two"
        } else if (prediction == "three")  {
            customView.post{
                customView.removeAtCursor(minX,minY)
            }
            // Do something for other predictions
        }
        else  if(prediction == "thumbsUp")  {
            customView.post{
                customView.moveCursor(minX,minY)
            }
        }
    }
}


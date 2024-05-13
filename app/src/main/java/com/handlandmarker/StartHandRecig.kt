/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.handlandmarker


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studify.R
import com.handlandmarker.Camera_Landmarks.CameraManager
import com.handlandmarker.Canvas.CustomView


class StartHandRecig : AppCompatActivity() {
    private  lateinit var view1 : CustomView
    private lateinit var C1: CameraManager
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.canvas)
        view1 = findViewById(R.id.Canvas1)
        C1 = CameraManager(this, this,view1)
        C1.initializeCamera()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        C1.releaseCamera()
        finish()
    }
}

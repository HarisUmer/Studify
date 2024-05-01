plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("de.undercouch.download")
}

android {
    namespace = "com.example.mediap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mediap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}



dependencies {

    implementation ("androidx.core:core-ktx:1.13.0")

    // App compat and UI things
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    // Navigation library
    val nav_version = "2.5.3"
    //noinspection GradleDependency
    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
    //noinspection GradleDependency
    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")

    // CameraX core library
    var camerax_version = "1.2.0-alpha02"
    //noinspection GradleDependency
    implementation ("androidx.camera:camera-core:$camerax_version")

    // CameraX Camera2 extensions
    //noinspection GradleDependency
    implementation ("androidx.camera:camera-camera2:$camerax_version")

    // CameraX Lifecycle library
    //noinspection GradleDependency
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")

    // CameraX View class
    //noinspection GradleDependency
    implementation ("androidx.camera:camera-view:$camerax_version")

    // WindowManager
    implementation ("androidx.window:window:1.1.0-alpha03")

    // Unit testing
    testImplementation ("junit:junit:4.13.2")

    // Instrumented testing
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")

    implementation ("io.agora.rtc:full-sdk:4.0.1")
    implementation ("com.github.AgoraIO-Community.VideoUIKit-Android:final:v4.0.1")
    //TenserFlow
    implementation ("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
    implementation ("com.google.android.gms:play-services-tflite-gpu:16.2.0")
    // MediaPipe Library
    implementation ("com.google.mediapipe:tasks-vision:0.10.0")

    implementation("commons-codec:commons-codec:1.11")
}
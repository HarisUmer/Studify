plugins {
    id ("com.android.application")
    id("com.google.gms.google-services")
    id ("org.jetbrains.kotlin.android")
    id ("de.undercouch.download")
}

android {
    namespace = "com.example.studify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studify"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

       // testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    implementation ("androidx.core:core-ktx:1.13.1")

    // App compat and UI things
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.fragment:fragment-ktx:1.7.0")

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

    //Agora SDK
    implementation(files("libs/AgoraScreenShareExtension.aar"))
    implementation("io.agora.rtc:full-sdk:4.3.1")
    implementation ("com.github.AgoraIO-Community.VideoUIKit-Android:final:v4.0.1")
    //TenserFlow
    implementation ("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
    implementation ("com.google.android.gms:play-services-tflite-gpu:16.2.0")
    // MediaPipe Library
    implementation ("com.google.mediapipe:tasks-vision:0.20230731")
    //pOWERPoINT
    // build.gradle.kts

    implementation ("org.apache.poi:poi:4.1.0")
    implementation("org.apache.poi:poi-ooxml:5.2.1") // for working with Office Open XML format


    //Firebase
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))


    implementation("com.github.kittinunf.fuel:fuel:2.3.1")

    //wrapper
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.13.0") // Or the latest version

    implementation("commons-codec:commons-codec:1.11")
}




plugins {

    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.beautyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.beautyapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    dependencies {
        // Core
        implementation ("androidx.core:core-ktx:1.12.0")
        implementation ("androidx.activity:activity-compose:1.8.2")
        implementation ("androidx.compose.material3:material3:1.2.1")
        implementation ("com.google.android.material:material:1.9.0")

        // Firebase (dùng BOM)
        implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation ("com.google.firebase:firebase-storage:20.0.1")
        implementation ("com.google.firebase:firebase-core:21.0.0")

        // Database
        implementation ("androidx.room:room-ktx:2.6.1")
        implementation ("io.github.pilgr:paperdb:2.7.2")

        // Network
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")

        // RxJava
        implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
        implementation ("io.reactivex.rxjava3:rxjava:3.0.0")

        // Media
        implementation ("com.google.android.exoplayer:exoplayer:2.19.1")

        // UI
        implementation ("com.airbnb.android:lottie:6.1.0")
        implementation ("de.hdodenhof:circleimageview:3.1.0")
        implementation ("com.github.bumptech.glide:glide:4.16.0")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

        // CameraX
        implementation ("androidx.camera:camera-core:1.3.0")
        implementation ("androidx.camera:camera-camera2:1.3.0")
        implementation ("androidx.camera:camera-lifecycle:1.3.0")
        implementation ("androidx.camera:camera-view:1.3.0")

        // Material
        implementation ("com.google.android.material:material:1.11.0")

        // Dhaval
        implementation ("com.github.dhaval2404:imagepicker:2.0")

        implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
        implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")

        implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    }

}
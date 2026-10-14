import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

// Self-contained automatic materialization of debug.keystore for local developer builds
val rootDebugKeystore = file("${rootDir}/debug.keystore")
if (!rootDebugKeystore.exists()) {
  try {
    val embeddedBase64 = "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"
    rootDebugKeystore.writeBytes(Base64.getDecoder().decode(embeddedBase64))
  } catch (_: Exception) {
    val defaultAndroidKeystore = file("${System.getProperty("user.home")}/.android/debug.keystore")
    if (defaultAndroidKeystore.exists()) {
      defaultAndroidKeystore.copyTo(rootDebugKeystore, overwrite = true)
    }
  }
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.noordaily.islamic"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.firestore)

  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.play.services.ads)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

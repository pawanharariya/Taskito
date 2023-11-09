plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.psh.taskito"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.psh.taskito"
        minSdk = 24
        targetSdk = 33
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
    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }
}

dependencies {
    val androidXVersion = "1.0.0"
    val androidXTestCoreVersion = "1.5.0"
    val androidXTestExtKotlinRunnerVersion = "1.1.5"
    val androidXTestRulesVersion = "1.2.0-beta01"
    val androidXAnnotations = "1.0.1"
    val androidXLegacySupport = "1.0.0"
    val appCompatVersion = "1.6.1"
    val archLifecycleVersion = "2.2.0"
    val archTestingVersion = "2.0.0"
    val constraintLayoutVersion = "2.1.4"
    val coreKtxVersion = "1.9.0"
    val coroutinesVersion = "1.3.7"
    val dexMakerVersion = "2.12.1"
    val espressoVersion = "3.2.0-beta01"
    val fragmentVersion = "1.1.0-alpha07"
    val junitVersion = "4.13.2"
    val materialVersion = "1.9.0"
    val mockitoVersion = "2.8.9"
    val navigationVersion = "2.7.4"
    val refreshLayoutVersion = "1.0.0"
    val robolectricVersion = "4.9"
    val roomVersion = "2.6.0"
    val rulesVersion = "1.0.1"
    val timberVersion = "4.7.1"
    val truthVersion = "0.44"

    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$refreshLayoutVersion")
    implementation("com.jakewharton.timber:timber:$timberVersion")

    // Architecture Components
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:$archLifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-compiler:$archLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$archLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$archLifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // AndroidX Test - JVM testing
    testImplementation("androidx.test:core-ktx:$androidXTestCoreVersion")
    testImplementation("org.robolectric:robolectric:$robolectricVersion")
    testImplementation("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
    testImplementation("androidx.arch.core:core-testing:$archTestingVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    androidTestImplementation("junit:junit:$junitVersion")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    debugImplementation("androidx.fragment:fragment-testing:$fragmentVersion")
    implementation("androidx.test:core:$androidXTestCoreVersion")
    androidTestImplementation("org.mockito:mockito-core:$mockitoVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navigationVersion")

    // Architecture component testing library to your instrumented tests
    androidTestImplementation("androidx.arch.core:core-testing:$archTestingVersion")
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:$androidXTestExtKotlinRunnerVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
}
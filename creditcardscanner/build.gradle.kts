plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.haghpanah.scanner"
    compileSdk = 34

    buildTypes {
        debug {
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
            }
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
            }
        }
    }

    defaultConfig {
        minSdk = 24

        externalNativeBuild {
            cmake {
                // Passes optional arguments to CMake.
                arguments.addAll(listOf("-DANDROID_ARM_NEON=TRUE", "-DANDROID_TOOLCHAIN=clang"))

                // Sets a flag to enable format macro constants for the C compiler.
                cFlags.add("-D__STDC_FORMAT_MACROS")

                // Sets optional flags for the C++ compiler.
                cppFlags.addAll(listOf("-fexceptions", "-frtti", "-fstack-protector-all"))

            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.18.1"
        }
    }

    buildFeatures {
        viewBinding = true
    }
    ndkVersion = "27.1.12297006"
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    implementation(libs.hilt.andorid)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt(libs.hilt.compiler)

    implementation(libs.text.recognition)

    implementation(project(":opencv:sdk"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
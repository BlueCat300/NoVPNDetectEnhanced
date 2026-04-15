@file:Suppress("UnstableApiUsage")

plugins {
    alias (libs.plugins.android.application)
    alias (libs.plugins.devtools.ksp)
}

android {
    namespace = "ru.bluecat.novpndetectenhanced"
    compileSdk = 37

    defaultConfig {
        applicationId = "ru.bluecat.novpndetectenhanced"
        minSdk = 26
        targetSdk = 37
        versionCode = 5
        versionName = "1.1"

        externalNativeBuild {
            cmake {
                cppFlags.addAll(listOf(
                    "-std=c++20",
                    "-fvisibility=hidden",
                    "-fvisibility-inlines-hidden",
                    "-funwind-tables",
                    "-fasynchronous-unwind-tables"
                ))
            }
        }

        ndk.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
    }

    signingConfigs {
        create("release") {
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = false
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            externalNativeBuild {
                cmake {
                    arguments.addAll(listOf(
                        "-DCMAKE_BUILD_TYPE=Release",
                        "-DCMAKE_SHARED_LINKER_FLAGS=-Wl,--gc-sections",
                        "-DCMAKE_CXX_FLAGS_RELEASE=-O2"
                    ))
                    cppFlags.addAll(listOf(
                        "-ffunction-sections",
                        "-fdata-sections",
                        "-flto=full"
                    ))
                }
            }

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes.addAll(listOf(
                "DebugProbesKt.bin",
                "META-INF/**.version",
                "kotlin-tooling-metadata.json",
                "kotlin/**.kotlin_builtins",
                "org/bouncycastle/pqc/**.properties",
                "org/bouncycastle/x509/**.properties",
            ))
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions.freeCompilerArgs = listOf(
        "-Xno-param-assertions",
        "-Xno-call-assertions",
        "-Xno-receiver-assertions"
    )
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.forEach { output ->
            if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                val versionName = output.versionName.get()
                output.outputFileName = "NoVPNDetect Enhanced v$versionName.apk"
            }
        }
    }
}

dependencies {
    implementation (libs.androidx.core)
    implementation (libs.kavaref.core)
    implementation (libs.kavaref.extension)

    compileOnly (libs.xposed.api)
    ksp (libs.yukihook.ksp)
    implementation (libs.yukihook.api)
}

tasks.configureEach {
    if (name.contains("Test")) {
        enabled = false
    }
}
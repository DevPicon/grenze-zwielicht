plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "la.devpicon.android.grenzezwielicht"
    compileSdk = 35

    defaultConfig {
        applicationId = "la.devpicon.android.grenzezwielicht"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.compiler)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx){
        exclude(group = "com.intellij", module = "annotations")
    }
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)

    kapt(libs.hilt.android.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    constraints {
        implementation("org.jetbrains:annotations:23.0.0")
    }
    modules {
        module("com.intellij:annotations") {
            replacedBy("org.jetbrains:annotations", "Newer version of annotations")
        }
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

// Add this at the bottom of your build.gradle
tasks.register("findAnnotationDeps") {
    doLast {
        configurations.forEach { config ->
            if (config.isCanBeResolved) {
                println("\nConfiguration: ${config.name}")
                config.resolvedConfiguration.lenientConfiguration.allModuleDependencies.forEach { dep ->
                    if (dep.module.toString().contains("annotations")) {
                        println("Found: ${dep.module}")
                        dep.children.forEach { child ->
                            println("\t|- Child: ${child.module}")
                        }
                    }
                }
            }
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains:annotations:23.0.0")
        // Needed because some internal dependencies might use different versions
        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    }
}
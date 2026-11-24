plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "app.void73.ide"
    compileSdk = 35
    defaultConfig {
        applicationId = "app.void73.ide"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        ndk { abiFilters += listOf("arm64-v8a") }
    }
    packaging { jniLibs { useLegacyPackaging = true } } // extractNativeLibs=true
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    sourceSets["main"].java.srcDir("build/generated/uniffi")
}

dependencies {
    implementation("net.java.dev.jna:jna:5.14.0@aar")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.core:core-ktx:1.13.1")
}

val repoRoot = rootProject.projectDir.parentFile

val buildRust by tasks.registering(Exec::class) {
    workingDir = repoRoot
    commandLine("cargo", "ndk", "-t", "arm64-v8a", "-o",
        "$projectDir/src/main/jniLibs", "build", "--release", "-p", "devcore")
}

val genBindings by tasks.registering(Exec::class) {
    dependsOn(buildRust)
    workingDir = repoRoot
    commandLine("cargo", "run", "-p", "uniffi-bindgen", "--", "generate",
        "--library", "target/aarch64-linux-android/release/libdevcore.so",
        "--language", "kotlin", "--out-dir", "$projectDir/build/generated/uniffi")
}

tasks.named("preBuild") { dependsOn(genBindings) }

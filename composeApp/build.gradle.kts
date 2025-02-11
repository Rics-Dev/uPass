import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)

    id("com.github.gmazzo.buildconfig") version "5.5.0"

}

val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties().apply {
    load(FileInputStream(apikeyPropertiesFile))
}


buildConfig {
    buildConfigField("String", "SUPABASE_KEY", apikeyProperties["SUPABASE_KEY"] as String)
}

val buildConfigOutputDir = layout.buildDirectory.dir("generated/buildConfig")

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }


    // Room step6 part1 for adding ksp src directory to use AppDatabase::class.instantiateImpl() in iosMain:
    // Due to https://issuetracker.google.com/u/0/issues/342905180
    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
        kotlin.srcDir(buildConfigOutputDir)
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.androidx.startup.runtime)

            implementation(libs.cryptography.provider.jdk)
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.biometric)
            implementation("dev.icerock.moko:biometry-compose:0.4.0")

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.material3)
            implementation(libs.androidx.materialIconsExtended)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.navigation.compose)

            implementation(libs.cryptography.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.adaptive)
            implementation(libs.adaptive.layout)
            implementation(libs.adaptive.navigation)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.material3.window.size.classe)


            implementation("io.matthewnelson.encoding:base16:2.2.2")
            implementation("io.matthewnelson.encoding:base32:2.2.2")
            implementation("io.matthewnelson.encoding:base64:2.2.2")


            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            implementation("com.ditchoom:buffer:1.4.2")
            implementation(libs.kotlincrypto.hash.sha2)
            implementation(libs.kotlincrypto.macs.hmac.sha1)
            implementation(libs.kotlincrypto.macs.hmac.sha2)



            implementation("io.github.vinceglb:filekit-compose:0.8.2")


            implementation(project.dependencies.platform("io.github.jan-tennert.supabase:bom:3.0.0-rc-1"))
            implementation("io.github.jan-tennert.supabase:auth-kt")
            implementation("io.github.jan-tennert.supabase:compose-auth")
            implementation("io.github.jan-tennert.supabase:compose-auth-ui")
            implementation("io.ktor:ktor-client-cio:3.0.0-rc-1")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.cryptography.provider.jdk)
        }
        iosMain.dependencies {
            implementation(libs.cryptography.provider.apple)
        }
    }


}

android {
    namespace = "com.ricsdev.upass"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")


    defaultConfig {
        applicationId = "com.ricsdev.upass"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose.desktop {
    application {
        mainClass = "com.ricsdev.upass.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb,
                TargetFormat.AppImage
            )
            packageName = "com.ricsdev.upass"
            packageVersion = "1.0.0"
            linux {
                modules("jdk.security.auth")
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    // Update: https://issuetracker.google.com/u/0/issues/342905180
    add("kspCommonMainMetadata", "androidx.room:room-compiler:2.7.0-alpha04")

}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
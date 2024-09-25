package com.ricsdev.uconnect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ricsdev.uconnect.di.initKoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

fun main() = application {
//    val appDataManager = AppDataManager("uConnect")

    initKoin()

//    kotlinx.coroutines.runBlocking {
//        appDataManager.clearAppData()
//    }



    Window(
        onCloseRequest = ::exitApplication,
        title = "uConnect",
    ) {
        App()
    }
}



//class AppDataManager(private val appName: String) {
//    private val appDataDir: File by lazy {
//        when {
//            System.getProperty("os.name").lowercase().contains("win") -> {
//                File(System.getenv("LOCALAPPDATA"), appName)
//            }
//            System.getProperty("os.name").lowercase().contains("mac") -> {
//                File(System.getProperty("user.home"), "Library/Application Support/$appName")
//            }
//            else -> {
//                File(System.getProperty("user.home"), ".${appName.lowercase()}")
//            }
//        }
//    }
//
//    suspend fun clearAppData() = withContext(Dispatchers.IO) {
//        if (appDataDir.exists()) {
//            appDataDir.deleteRecursively()
//        }
//    }
//}
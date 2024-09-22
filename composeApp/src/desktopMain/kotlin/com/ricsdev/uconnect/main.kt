package com.ricsdev.uconnect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ricsdev.uconnect.di.initKoin

fun main() = application {


    initKoin()



    Window(
        onCloseRequest = ::exitApplication,
        title = "uConnect",
    ) {
        App()
    }
}
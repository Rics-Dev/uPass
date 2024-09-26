package com.ricsdev.uconnect

interface Platform {
    val name: String
    val type: PlatformType
}

expect fun getPlatform(): Platform


enum class PlatformType {
    ANDROID,
    DESKTOP,
    IOS,
    JS
}
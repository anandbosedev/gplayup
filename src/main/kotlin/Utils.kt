package com.anandbose

fun logDecorated(title: String, message: String? = null) {
    if (LOG_ENABLED) {
        if (message != null) {
            println("--- Begin $title ---")
            println(message)
            println("--- End $title ---")
        } else {
            println("* $title")
        }
    }
}
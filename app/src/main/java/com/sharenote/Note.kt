package com.sharenote

data class Note(
    val courseName: String? = null,
    val title: String? = null,
    val description: String? = null,
    val driveLink: String? = null,
    var id: String = ""
)

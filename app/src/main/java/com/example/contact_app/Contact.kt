package com.example.contact_app
import android.net.Uri

data class Contact(
    val name: String,
    val number: String,
    val profilePicUri: Uri
)

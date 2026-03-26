package com.example.contact_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddContactActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonPickImage: Button
    private lateinit var buttonSave: Button

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        editTextName = findViewById(R.id.editTextName)
        editTextNumber = findViewById(R.id.editTextNumber)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        buttonPickImage = findViewById(R.id.buttonPickImage)
        buttonSave = findViewById(R.id.buttonSave)

        buttonPickImage.setOnClickListener {
            pickImageFromGallery()
        }

        buttonSave.setOnClickListener {
            saveContact()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageViewProfile.setImageURI(selectedImageUri)
        }
    }

    private fun saveContact() {
        val name = editTextName.text.toString().trim()
        val number = editTextNumber.text.toString().trim()
        val imageUri = selectedImageUri
        val timeSaved = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (name.isEmpty() || number.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val contact = Contact(name, number, imageUri)
        val resultIntent = Intent()
        resultIntent.putExtra("name", name)
        resultIntent.putExtra("number", number)
        resultIntent.putExtra("imageUri", imageUri.toString())
        resultIntent.putExtra("timeSaved", timeSaved)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}

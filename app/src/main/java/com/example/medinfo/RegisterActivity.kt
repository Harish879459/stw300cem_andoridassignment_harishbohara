package com.example.medinfo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.medinfo.databinding.ActivityRegisterBinding
import com.example.medinfo.entity.User
import com.example.medinfo.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var view: ActivityRegisterBinding

    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        view = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.tvGoToLogin.setOnClickListener {
            goToLogin()
        }
        view.btnRegister.setOnClickListener {
            if (areFieldValid()) {
                register()
            }
        }

        view.imgProfile.setOnClickListener {
            if (!hasPermission()) {
                requestPermission()
            }
            loadPopUpMenu()
        }
    }

    private fun register() {
        val fullName = view.etFullname.text.toString()
        val email = view.etEmail.text.toString()
        val phone = view.etPhone.text.toString()
        val password = view.etPassword.text.toString()
        val user = User(fullName = fullName, email = email, phone = phone, password = password)
        if (view.etPassword.text.toString() != view.etReenterPassword.text.toString()) {
            view.etReenterPassword.error = "Password Does not match"
            view.etReenterPassword.requestFocus()
            return
        } else {
            CoroutineScope(Dispatchers.IO).apply {
                launch {
                    try {
                        val userRepo = UserRepository()
                        val response = userRepo.register(user)
                        if (response.success == true) {
                            if (imageUrl != null) {
                                uploadImage(response?.data?._id.toString())
                            }
                            withContext(Main) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                goToLogin()
                            }
                        }
                    } catch (ex: IOException) {
                        withContext(Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "${ex.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun areFieldValid(): Boolean {

        if (TextUtils.isEmpty(view.etFullname.text.toString())) {
            view.etFullname.error = "Required";
            view.etFullname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(view.etPhone.text.toString())) {
            view.etPhone.error = "Required";
            view.etPhone.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(view.etPassword.text.toString())) {
            view.etPassword.error = "Required";
            view.etPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(view.etReenterPassword.text.toString())) {
            view.etReenterPassword.error = "Required";
            view.etReenterPassword.requestFocus();
            return false;
        } else if (!view.etPassword.text.toString()
                .equals(view.etReenterPassword.text.toString())
        ) {
            view.etReenterPassword.error = "Password Doesnot Match";
            view.etReenterPassword.requestFocus();
            return false;
        }
        return true;
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun uploadImage(id: String) {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val reqFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)

            val body =
                MultipartBody.Part.createFormData("photo", file.name, reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRepo = UserRepository()
                    val response = userRepo.uploadImage(id, body)
                    if (response.success == true) {
                        withContext(Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Image Uploaded",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    withContext(Main) {
                        Log.d("Error Uploading Image ", ex.localizedMessage)
                        Toast.makeText(
                            this@RegisterActivity,
                            ex.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this@RegisterActivity, view.imgProfile)
        popupMenu.menuInflater.inflate(R.menu.gallery_camera, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCamera ->
                    openCamera()
                R.id.menuGallery ->
                    openGallery()
            }
            true
        }
        popupMenu.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestGalleryCode && data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = contentResolver
                val cursor =
                    contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imageUrl = cursor.getString(columnIndex)
                view.imgProfile.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == requestCameraCode && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                view.imgProfile.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
            }
        }
    }

    private fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? {
        var file: File? = null
        return try {
            file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitMapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitMapData)
            fos.flush()
            fos.close()
            file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }


    private var requestGalleryCode = 0
    private var requestCameraCode = 1
    private var imageUrl: String? = null


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestGalleryCode)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, requestCameraCode)
    }

    //Request Permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@RegisterActivity,
            permissions, 1434
        )
    }


    //Check If Permission is given
    private fun hasPermission(): Boolean {
        var hasPermission = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = false
            }
        }
        return hasPermission
    }
}
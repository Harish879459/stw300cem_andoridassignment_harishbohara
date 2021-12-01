package com.example.medinfo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.medinfo.api.ServiceBuilder
import com.example.medinfo.databinding.ActivityProfileBinding
import com.example.medinfo.entity.User
import com.example.medinfo.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var view: ActivityProfileBinding

    var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        view = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(view.root)

        loadProfile()
        view.tvEditProfile.setOnClickListener {
            disableFields(true)
        }
        view.btnUpdate.setOnClickListener {
            updateMe()
        }

        view.profileImage.setOnClickListener {
            loadPopUpMenu()
        }
        view.tvLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun disableFields(flag: Boolean) {
        view.etFullName.isEnabled = flag;
        view.etEmail.isEnabled = flag;
        view.etPhone.isEnabled = flag;
    }

    private fun loadProfile() {

        CoroutineScope(Dispatchers.IO).launch {
            val userRepo = UserRepository();
            val data = userRepo.profile(LoginActivity.USER_ID!!);
            val response = data.success;
            val userData = data.data;
            if (userData != null) {
                userId = userData._id!!
            };
            if (response == true) {
                withContext(Main) {
                    if (userData != null) {
                        view.etFullName.setText(userData.fullName.toString())
                        view.etEmail.setText(userData.email.toString())
                        view.etPhone.setText(userData.phone.toString())

                        disableFields(false);
                        if (userData.photo != null) {
                            Glide.with(this@ProfileActivity)
                                .load(ServiceBuilder.loadImagePath() + userData.photo)
                                .into(view.profileImage);
                        }
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Error Retrieving Data",
                            Toast.LENGTH_SHORT
                        ).show();


                    }
                }
            }
        }

    }

    private fun updateMe() {
        val fullname = view.etFullName.text.toString();
        val phone = view.etPhone.text.toString();
        val email = view.etEmail.text.toString();

        CoroutineScope(Dispatchers.IO).launch {
            var user = User(
                fullName = fullname,
                phone = phone,
                email = email
            );

            try {
                val userRepo = UserRepository();
                val response = userRepo.update(user, LoginActivity.USER_ID!!)
                if (response.success == true) {
                    withContext(Main) {
                        Toast.makeText(this@ProfileActivity, "Profile Updated", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } catch (ex: Exception) {
                withContext(Main) {
                    Toast.makeText(this@ProfileActivity, ex.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
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
                    val response = userRepo.updateImage(id, body)
                    if (response.success == true) {
                        withContext(Main) {
                            Toast.makeText(
                                this@ProfileActivity,
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
                            this@ProfileActivity,
                            ex.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this@ProfileActivity, view.profileImage)
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
                view.profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == requestCameraCode && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                view.profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
            }
            uploadImage(LoginActivity.USER_ID!!);
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

}
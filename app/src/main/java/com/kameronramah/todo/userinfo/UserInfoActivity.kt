package com.kameronramah.todo.userinfo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.kameronramah.todo.BuildConfig
import com.kameronramah.todo.R
import com.kameronramah.todo.network.Api
import com.kameronramah.todo.network.Api.tasksWebService
import com.kameronramah.todo.network.TasksRepository
import com.kameronramah.todo.network.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserInfoActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) openCamera()
                else showExplanationDialog()
            }

    private fun convert(uri: Uri) =
            MultipartBody.Part.createFormData(
                    name = "avatar",
                    filename = "temp.jpeg",
                    body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
            )

    private val photoUri by lazy {
        FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID +".fileprovider",
                File.createTempFile("avatar", ".jpeg", externalCacheDir)

        )
    }

    // register
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            lifecycleScope.launch {
                handleImage(photoUri)
            }
        }
        else Toast.makeText(this, "Erreur ! 😢", Toast.LENGTH_LONG).show()

    }

    // use
   private fun openCamera() = takePicture.launch(photoUri)

    private val pickInGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                lifecycleScope.launch {
                    handleImage(it)
                }
            }

    private fun requestCameraPermission() =
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    private fun askCameraPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> openCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showExplanationDialog()
            else -> requestCameraPermission()
        }
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("On a besoin de la caméra sivouplé ! 🥺")
            setPositiveButton("Bon, ok") { _, _ ->
                requestCameraPermission()
            }
            setCancelable(true)
            show()
        }
    }

    suspend fun updateAvatar(mp: MultipartBody.Part): UserInfo? {
        val response = tasksWebService.updateAvatar(mp)
        return if (response.isSuccessful) response.body() else null
    }

    private suspend fun handleImage(uri: Uri) {
        val mp = convert(uri)
        updateAvatar(mp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        val uploadPictureButton = findViewById<Button>(R.id.upload_image_button)
        uploadPictureButton.setOnClickListener {
            pickInGallery.launch("image/*")
        }

        val takePictureButton = findViewById<Button>(R.id.take_picture_button)
        takePictureButton.setOnClickListener {
            askCameraPermissionAndOpenCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            val imageView = findViewById<ImageView>(R.id.image_view)
            imageView?.load(userInfo.avatar) {
                transformations(CircleCropTransformation())
            }
        }
    }
}
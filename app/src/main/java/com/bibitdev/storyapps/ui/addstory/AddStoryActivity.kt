package com.bibitdev.storyapps.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.api.ApiConfig
import com.bibitdev.storyapps.databinding.ActivityAddStoryBinding
import com.bibitdev.storyapps.repository.UserRepository
import com.bibitdev.storyapps.ui.ViewModelFactory
import com.bibitdev.storyapps.ui.home.HomeActivity
import com.bibitdev.storyapps.utils.PreferencesHelper
import com.bibitdev.storyapps.utils.StorageHelper
import com.google.android.material.snackbar.Snackbar
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var preferencesHelper: PreferencesHelper
    private var selectedFile: File? = null
    private lateinit var cameraIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryIntentLauncher: ActivityResultLauncher<String>
    private lateinit var photoFilePath: String

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory(UserRepository(ApiConfig.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        setupIntents()
        setupActions()
        setupObservers()
    }

    private fun setupIntents() {
        cameraIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                handleCameraResult()
            }
        }

        galleryIntentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleGalleryResult(it) }
        }
    }

    private fun handleCameraResult() {
        val file = File(photoFilePath)
        selectedFile = file
        binding.ivAddStory.setImageURI(Uri.fromFile(file))
    }

    private fun handleGalleryResult(uri: Uri) {
        val storage = StorageHelper.getFileFromUri(uri, this)
        selectedFile = storage
        binding.ivAddStory.setImageURI(uri)
    }

    private fun setupActions() {
        binding.btnOpenCamera.setOnClickListener { onCameraClick() }
        binding.btnOpenGallery.setOnClickListener { galleryIntentLauncher.launch("image/*") }
        binding.btnAdd.setOnClickListener { onAddClick() }
    }

    private fun onCameraClick() {
        if (checkCameraPermission()) {
            launchCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun onAddClick() {
        val description = binding.edtDeskripsi.text.toString()

        if (description.isEmpty()) {
            binding.edtDeskripsi.error = getString(R.string.descriptionrequired)
            return
        }

        selectedFile?.let {
            uploadStory(description)
        } ?: run {
            Snackbar.make(binding.root, getString(R.string.filerequired), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun uploadStory(description: String) {
        val storageHelper = StorageHelper.generateTempFile(selectedFile!!)
        val descriptionBody = description.toRequestBody()
        val filePart = MultipartBody.Part.createFormData("photo", storageHelper.name, storageHelper.asRequestBody())

        addStoryViewModel.uploadStory(
            preferencesHelper.loadUser()?.token.orEmpty(),
            filePart,
            descriptionBody
        )
    }

    private fun setupObservers() {
        addStoryViewModel.uploadResult.observe(this) { response ->
            if (!response.error) {
                showSuccessMessage()
            } else {
                showErrorMessage(response.message)
            }
        }
    }

    private fun showSuccessMessage() {
        Snackbar.make(binding.root, getString(R.string.uploadsuccess), Snackbar.LENGTH_LONG).show()
        navigateToHome()
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
            finish()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
        }
    }

    private fun showPermissionRationaleDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Izin Kamera Dibutuhkan")
            .setMessage("Aplikasi ini membutuhkan izin untuk mengakses kamera.")
            .setPositiveButton("Berikan") { _, _ -> ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION) }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val storage = StorageHelper.createTempFile(this)
        photoFilePath = storage.absolutePath
        val uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", storage)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        cameraIntentLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(binding.root, "Izin kamera diberikan!", Snackbar.LENGTH_SHORT).show()
                launchCamera()
            } else {
                Snackbar.make(binding.root, "Izin kamera ditolak.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 10
    }
}

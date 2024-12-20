package com.dicoding.picodiploma.loginwithanimation.view.addStory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.utils.reduceFileImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import android.Manifest.permission.ACCESS_FINE_LOCATION


class AddStoryFragment : Fragment() {

    private val viewModel: AddStoryViewModel by viewModels()
    private lateinit var previewPhoto: ImageView
    private lateinit var inputDescription: EditText
    private lateinit var btnUpload: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedPhotoUri: Uri? = null
    private var capturedPhotoBitmap: Bitmap? = null

    private var latitude: Double? = null
    private var longitude: Double? = null

    companion object {
        fun newInstance(): AddStoryFragment {
            return AddStoryFragment()
        }
    }

    private val openCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                capturedPhotoBitmap = bitmap
                previewPhoto.setImageBitmap(bitmap)
            } else {
                Log.d("AddStoryFragment", "Camera action cancelled.")
            }
        }

    private val openGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedPhotoUri = uri
                previewPhoto.setImageURI(uri)
            } else {
                Log.d("AddStoryFragment", "Gallery action cancelled.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_story, container, false)

        previewPhoto = view.findViewById(R.id.previewPhoto)
        inputDescription = view.findViewById(R.id.inputDescription)
        btnUpload = view.findViewById(R.id.btnUpload)

        val btnCamera: Button = view.findViewById(R.id.btnCamera)
        val btnGallery: Button = view.findViewById(R.id.btnGallery)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val CAMERA_PERMISSION_REQUEST_CODE = 100

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                openCameraLauncher.launch(intent)
            }
        }

        btnGallery.setOnClickListener {
            openGalleryLauncher.launch("image/*")
        }

        btnUpload.setOnClickListener {
            getLocationAndUploadStory()
        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    openCameraLauncher.launch(intent)
                } else {
                    Toast.makeText(requireContext(), "Izin kamera ditolak.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocationAndUploadStory() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    Log.d("AddStoryFragment", "Latitude: $latitude, Longitude: $longitude")
                    uploadStory()
                } ?: run {
                    Toast.makeText(requireContext(), "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                101
            )
        }
    }

    private fun uploadStory() {
        val descriptionText = inputDescription.text.toString()
        if (descriptionText.isBlank()) {
            Toast.makeText(requireContext(), "Deskripsi tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        val file = capturedPhotoBitmap?.let { bitmapToFile(it, requireContext()) }
            ?: selectedPhotoUri?.let { uriToFile(it, requireContext()) }

        if (file == null) {
            Toast.makeText(requireContext(), "Foto belum dipilih.", Toast.LENGTH_SHORT).show()
            return
        }

        val reducedFile = file.reduceFileImage()

        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), descriptionText)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("photo", reducedFile.name, requestFile)

        val lat = latitude?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
        val lon = longitude?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }

        // Log data yang akan di-upload
        Log.d("AddStoryFragment", "Uploading story with data:")
        Log.d("AddStoryFragment", "Description: $descriptionText")
        Log.d("AddStoryFragment", "Latitude: $latitude, Longitude: $longitude")
        Log.d("AddStoryFragment", "Image file: ${file.name}, Size: ${file.length()} bytes")

        lifecycleScope.launch {
            val userRepository = UserRepository.getInstance(requireContext())
            userRepository.getSession().collect { userModel ->
                if (userModel.token.isNotEmpty()) {
                    viewModel.uploadStory(userModel.token, description, filePart, lat, lon)
                } else {
                    Toast.makeText(requireContext(), "Token tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), response.message ?: "Upload berhasil!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToFile(selectedUri: Uri, context: Context): File? {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("story_", ".jpg", context.cacheDir)
        contentResolver.openInputStream(selectedUri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val tempFile = File.createTempFile("story_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return tempFile
    }
}

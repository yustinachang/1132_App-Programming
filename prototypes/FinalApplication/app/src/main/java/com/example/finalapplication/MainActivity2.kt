package com.example.finalapplication

import android.content.Intent
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity2 : AppCompatActivity() {
    // TODO: 後端Flash伺服器位置
    private val serverUrl = "http://10.0.2.2:5000/predict"

    private var photoUri: Uri? = null
    private lateinit var imgPreview: ImageView

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            photoUri?.let { uri ->
                imgPreview.setImageURI(uri)
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imgPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //自訂義View
        val drawView = findViewById<DrawView>(R.id.drawView)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val btnFinish = findViewById<Button>(R.id.btnFinish)
        btnClear.setOnClickListener {
            Toast.makeText(this, "清空畫布", Toast.LENGTH_SHORT).show()
            drawView.clearPoints()
        }
        btnFinish.setOnClickListener {
            val points = drawView.getPoints()
            val shapeNumber = spinner.selectedItem.toString().toInt()

            // 轉成 JSON/ArrayList 格式
            val shapesData: ArrayList<ArrayList<Double>> = ArrayList()
            for (point in points) {
                shapesData.add(arrayListOf(point.x.toDouble(), point.y.toDouble()))
            }

            // 建立 Intent 傳給 MainActivity3
            val intent = Intent(this, MainActivity3::class.java)
            intent.putExtra("shapes_num", shapeNumber)
            intent.putExtra("shapes_data", shapesData)
            startActivity(intent)
        }
        //照片權限
        imgPreview = findViewById(R.id.imgPreview)
        val btnTakePhoto = findViewById<Button>(R.id.btnTakePhoto)
        val btnOpenGallery = findViewById<Button>(R.id.btnOpenGallery)
        btnTakePhoto.setOnClickListener {
            checkCameraPermission {
                takePhotoAndSaveToGallery()
            }
        }
        btnOpenGallery.setOnClickListener {
            pickImageFromGallery()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun takePhotoAndSaveToGallery() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }

        photoUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        photoUri?.let { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun checkCameraPermission(onGranted: () -> Unit) {
        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, cameraPermission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(cameraPermission),
                1001
            )
        }
    }
}
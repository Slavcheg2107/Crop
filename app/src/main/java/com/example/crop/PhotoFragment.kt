package com.example.crop

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import android.app.Activity.RESULT_OK
import com.yalantis.ucrop.UCrop
import android.os.Build
import androidx.annotation.Nullable
import kotlinx.android.synthetic.main.photo_fragment.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlin.coroutines.CoroutineContext


class PhotoFragment : Fragment(), CoroutineScope {
    private val job = Job()

    private val scope: CoroutineContext = job + Dispatchers.Main
    override val coroutineContext: CoroutineContext
        get() = scope

    val PERMISSION_REQUEST_CODE = 11
    val REQUEST_IMAGE_CAPTURE = 1
    private var mCurrentPhotoPath: String? = null
    val webApi = RetrofitSettings.createApi()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.photo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkPersmission()) {
            openCamera()
        } else requestPermission()
        take_pic_btn.setOnClickListener{
            if (checkPersmission()) {
                openCamera()
            } else requestPermission()
        }
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this.context!!, CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this.context!!,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this.context!!,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
       requestPermissions(
            arrayOf(READ_EXTERNAL_STORAGE, CAMERA, WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val uri: Uri = Uri.parse(mCurrentPhotoPath)
            openCropActivity(uri, uri)
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val uri: Uri? = data?.let { UCrop.getOutput(it) }
            val filePath = uri?.path
            val imageFile = File(filePath)
            val name = imageFile.name
//            val job = webApi.sendPhoto(MultipartBody.Part.createFormData(imageFile.name, imageFile.name, RequestBody.create(MediaType.parse("text/plain"),imageFile)))
//            CoroutineScope(this.coroutineContext).launch {
//                val response = job.await()
//                if(response.isSuccessful){
//                    Toast.makeText(this@PhotoFragment.context, response.body(), Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(this@PhotoFragment.context, response.errorBody()?.string(), Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    openCamera()

                } else {
                    Toast.makeText(this.context!!, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun openCropActivity(sourceUri: Uri, destinationUri: Uri) {
        UCrop.of(sourceUri, destinationUri)
            .withMaxResultSize(800, 600)
            .withAspectRatio(5f, 5f)
            .start(this.context!!, this)
    }

    private fun openCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = getImageFile()
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

            FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".provider", file)
        else
            Uri.fromFile(file)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
    }


    private fun getImageFile(): File {
        val imageFileName = "JPEG_" + System.currentTimeMillis() + "_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), "Camera"
        )
        val file = File.createTempFile(
            imageFileName, ".jpg", storageDir
        )
        mCurrentPhotoPath = "file:" + file.absolutePath
        return file
    }
}
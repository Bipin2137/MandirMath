package com.antriksha.maulidairy

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import java.io.File


class ImageViewActivity : AppCompatActivity() {

    private var iv_photo: PhotoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        iv_photo = findViewById(R.id.iv_photo)
        val path = intent.getStringExtra("filePath")
        val file = File(path)
        if (file.exists()) {
            val src = BitmapFactory.decodeFile(file.path)
            iv_photo?.setImageBitmap(src)
        }
    }

    companion object {
        fun openImage(mainActivity: Context, it: String) {
            val intent = Intent(mainActivity, ImageViewActivity::class.java)
            intent.putExtra("filePath", it)
            mainActivity.startActivity(intent)
        }
    }
}
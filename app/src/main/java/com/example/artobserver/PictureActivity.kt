package com.example.artobserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class PictureActivity : AppCompatActivity() {
    companion object{
        lateinit var PIC_OBJECT: ObjectPicture;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)

        val imageView: ImageView = findViewById(R.id.picView);
        Glide.with(this).load(PIC_OBJECT.imageLink).into(imageView)

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.back_btn) {
            val mainIntent = Intent(this, MainActivity::class.java)
            setResult(RESULT_OK, mainIntent)
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)

    }
}
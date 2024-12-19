package com.example.artobserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


interface ICellClickListener{
    fun onCellClickListener(obj: ObjectPicture)
}
class MainActivity : AppCompatActivity(), ICellClickListener {
    private lateinit var RView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gridLayoutManager = GridLayoutManager(this, 2)

        RView = findViewById<RecyclerView>(R.id.rView);

        RView.setHasFixedSize(true)
        RView.layoutManager = gridLayoutManager

        CoroutineScope(Dispatchers.IO).launch {
            val wrapper: Wrapper = loadImages()
            for (id in wrapper.ids) {
                val picture: ObjectPicture = loadSingleImage(id)
                wrapper.objects.add(picture)
            }

            withContext(Dispatchers.Main) {
                if (RView.context != null) {
                    RView.adapter = ImageAdapter(wrapper.objects,
                        this@MainActivity);
                }
            }
        }
    }

    private fun loadSingleImage(id: Int): ObjectPicture {
        val link = "https://collectionapi.metmuseum.org/public/collection/v1/objects/${id}"

        var text = ""
        val url = URL(link)
        val urlConn: HttpURLConnection = url.openConnection() as HttpURLConnection
        text = urlConn.inputStream.bufferedReader().readText()

        val gson = Gson()
        val objectPicture: ObjectPicture = gson.fromJson(text, ObjectPicture::class.java)

        return objectPicture
    }

    fun loadImages(): Wrapper{
        val link = "https://collectionapi.metmuseum.org/public/collection/v1/" +
                "search?isHighlight=true&q=sunflowers&hasImages=true"

        var text = ""
        val url = URL(link)
        val urlConn: HttpURLConnection = url.openConnection() as HttpURLConnection
        text = urlConn.inputStream.bufferedReader().readText()

        val gson = Gson()
        val wrapper: Wrapper = gson.fromJson(text, Wrapper::class.java)

        return wrapper
    }

    override fun onCellClickListener(obj: ObjectPicture) {
        val newIntent = Intent(this, PictureActivity::class.java)
        PictureActivity.PIC_OBJECT = obj
        startActivityForResult(newIntent, 1)
    }
}

data class ObjectPicture(
    @SerializedName("primaryImage") val imageLink : String,
    @SerializedName("creditLine") val credits : String
)

data class Wrapper(
    @SerializedName("objectIDs") var ids: ArrayList<Int>,
    var objects: ArrayList<ObjectPicture>
)
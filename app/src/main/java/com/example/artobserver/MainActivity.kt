package com.example.artobserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


interface ICellClickListener{
    fun onCellClickListener(link: String)
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
            val wrapper = loadImages()

            withContext(Dispatchers.Main) {
                if (RView.context != null) {
                    RView.adapter = ImageAdapter(wrapper.photos.photo,
                        this@MainActivity);
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            val picLink = data?.getStringExtra("picLink")
            val snackbar: Snackbar = Snackbar.make(findViewById(R.id.main),
                "Добавлено в избранные", Snackbar.LENGTH_LONG)
            snackbar.setAction("OPEN", View.OnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(picLink))
                startActivity(browserIntent)
            })
            snackbar.show()
        }

    }

    fun loadImages(): Wrapper{
        val link = "https://api.flickr.com/services/rest/" +
                "?method=flickr.photos.search&api_key" +
                "=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"

        var text = ""
        val url = URL(link)
        val urlConn: HttpURLConnection = url.openConnection() as HttpURLConnection
        text = urlConn.inputStream.bufferedReader().readText()

        val gson = Gson()
        val wrapper: Wrapper = gson.fromJson(text, Wrapper::class.java)

        return wrapper
    }

    override fun onCellClickListener(link: String) {
        val newIntent = Intent(this, PictureActivity::class.java)
        PictureActivity.PIC_LINK = link
        startActivityForResult(newIntent, 1)
    }
}


data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val ispublic: Int,
    val isfriend: Int,
    val isfamily: Int
) {
    fun getUri(): Uri {
        val address = "https://farm${this.farm}.staticflickr.com" +
                "/${this.server}/${this.id}_${this.secret}_z.jpg"

        val uri: Uri = Uri.parse(address)

        return uri;
    }
}

data class PhotoPage(
    val photo: ArrayList<Photo>,
    val page: Int,
    val pages: Int,
    val total: Int,
    val perpage: Int
) {

}

data class Wrapper(val photos: PhotoPage) {

}
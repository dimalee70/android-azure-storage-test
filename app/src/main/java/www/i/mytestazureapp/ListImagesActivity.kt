package www.i.mytestazureapp

import android.app.ListActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import java.lang.Exception

class ListImagesActivity: ListActivity(){
    var images: Array<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler = Handler()
        var th = Thread{
            try {

                var images = this.listImages()
                handler.post{
                    this.images = images
                    var adapter = ArrayAdapter<String>(this, R.layout.text_view_item, images)
                    setListAdapter(adapter)
                }
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }.start()
    }
}
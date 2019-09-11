package www.i.mytestazureapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.microsoft.azure.storage.StorageCredentials
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature
import com.microsoft.azure.storage.StorageUri
import com.microsoft.azure.storage.blob.CloudBlobContainer
import com.microsoft.azure.storage.blob.CloudBlockBlob
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.net.URI
import android.provider.MediaStore








class MainActivity : AppCompatActivity() {

    private var imageUri: Uri? = null

    companion object{
        const val SELECT_IMAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectBtn.setOnClickListener {
            selectImageFromGallery()
        }
        uploadBtn.setOnClickListener{
            uploadImage()
        }
        uploadBtn.isEnabled = false

        showBtn.setOnClickListener{
            listImages()
        }
    }

    private fun selectImageFromGallery() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE)
    }

    private fun uploadImage() {
        try {

//            var SAS = StorageCredentialsSharedAccessSignature(Constants.SAS_TOKEN)
//            var container =
//                CloudBlobContainer(SAS.transformUri(imageUri as StorageUri))

//                var imageName = this.randomstring(10)
            //    val imageBlob = container.getBlockBlobReference(imageName)
            //    imageBlob.upload(this, imageLength.toLong())


            val imageStream = contentResolver.openInputStream(this.imageUri!!)
            val imageName = imageStream!!.randomstring(10)
            val blobUri = Constants.SAS_URL + imageName
            val SAS = StorageCredentialsSharedAccessSignature(Constants.SAS_TOKEN)
            val cloudBlockBlob = CloudBlockBlob(URI.create(blobUri),SAS)
            var imageLength = imageStream.available()
////            cloudBlockBlob.upload(imageStream, imageLength.toLong())
//            val path = getImagePath(imageUri!!)
//            cloudBlockBlob.uploadFromFile(path)

            val handler = Handler()
            val th = Thread{
                try {

                    val path = getImagePath(imageUri!!)
                    cloudBlockBlob.uploadFromFile(path)


//                    val imageStream = contentResolver.openInputStream(this.imageUri!!)
//                    val imageName = imageStream!!.randomstring(10)
//                    val blobUri = Constants.SAS_URL + imageName
//                    val SAS = StorageCredentialsSharedAccessSignature(Constants.SAS_TOKEN)
//                    val cloudBlockBlob = CloudBlockBlob(URI.create(blobUri),SAS)
//                    var imageLength = imageStream.available()
////            cloudBlockBlob.upload(imageStream, imageLength.toLong())
//                    val path = getImagePath(imageUri!!)
//                    cloudBlockBlob.uploadFromFile(path)

//                    val imageName = imageStream.uploadImage(imageLength)

                    handler.post{
                        Toast.makeText(this, "Image Uploaded Successfully. Name = " + imageName,
                            Toast.LENGTH_SHORT).show()
                    }
                }catch (ex: Exception){
                    Toast.makeText(this, ex.message,
                        Toast.LENGTH_SHORT).show()
                }
            }.start()

        }catch (ex: Exception){
            ex.printStackTrace()
            Toast.makeText(this, ex.message,
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun listImages() {
        val intent = Intent(baseContext, ListImagesActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SELECT_IMAGE -> {
                if(resultCode == Activity.RESULT_OK){
                    imageUri = data!!.data
                    imageView.setImageURI(imageUri)
                    uploadBtn.isEnabled = true
                }
            }
        }
    }
//
//    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
//        var cursor: Cursor? = null
//        try {
//            val proj = arrayOf(MediaStore.Images.Media.DATA)
//            cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
//            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            return cursor.getString(column_index)
//        } catch (e: Exception) {
//            return ""
//        } finally {
//            if (cursor != null) {
//                cursor.close()
//            }
//        }
//    }

    fun getImagePath(uri: Uri): String {
        var cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(document_id),
            null
        )
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }
}

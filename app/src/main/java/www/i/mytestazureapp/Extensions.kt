package www.i.mytestazureapp

import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature
import com.microsoft.azure.storage.blob.CloudBlob
import com.microsoft.azure.storage.blob.CloudBlobClient
import com.microsoft.azure.storage.blob.CloudBlobContainer
import com.microsoft.azure.storage.blob.CloudBlockBlob
import java.io.InputStream
import java.lang.StringBuilder
import java.net.URI
import java.security.SecureRandom
import java.util.*

//inline fun InputStream.uploadImage(imageLength: Int): String{

//    val blobUri =

//    var SAS = StorageCredentialsSharedAccessSignature(Constants.SAS_TOKEN)
//    var container = CloudBlobContainer(SAS.transformUri(URI("https://images.pexels.com/photos/326055/pexels-photo-326055.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")))
//    var container = this.getContainer()
//    container.createIfNotExists()
//    var imageName = this.randomstring(10)
//    val imageBlob = container.getBlockBlobReference(imageName)
//    imageBlob.upload(this, imageLength.toLong())
//    return imageName
//}

inline fun InputStream.randomstring(length: Int): String{
    val validChars = "abcdefghijklmnopqrstuvwxyz"
    val rnd = SecureRandom()
    val sb = StringBuilder(length)
    for (i in 1..length-1){
        sb.append(validChars[rnd.nextInt(validChars.length)])
    }
    return sb.toString()
}

inline fun InputStream.getContainer(): CloudBlobContainer{
    val storageAccount = CloudStorageAccount
        .parse(Constants.storageConnectionString)
    val blobClient = storageAccount.createCloudBlobClient()
    val container = blobClient.getContainerReference(Constants.IMAGES)
    return container
}

inline fun ListImagesActivity.getContainer(): CloudBlobContainer{
    val storageAccount = CloudStorageAccount
        .parse(Constants.storageConnectionString)
    val blobClient = storageAccount.createCloudBlobClient()
    val container = blobClient.getContainerReference(Constants.IMAGES)
    return container
}

inline fun InputStream.upLoadImage(path: String){
    val imageName = this.randomstring(10)
    val blobUri = Constants.SAS_URL + imageName
    val SAS = StorageCredentialsSharedAccessSignature(Constants.SAS_TOKEN)
    val cloudBlockBlob = CloudBlockBlob(URI.create(blobUri),SAS)
    cloudBlockBlob.uploadFromFile(path)
}

inline fun ListImagesActivity.listImages(): Array<String> {
    val SAS = StorageCredentialsSharedAccessSignature(Constants.SAS_TOKEN)
    val cloudBlockBlob = CloudBlockBlob(URI.create(Constants.SAS_URL),SAS)
    val container = cloudBlockBlob.container
    val blobs = container.listBlobs()
    val blobNames = LinkedList<String>()
    for (blob in blobs){
        blobNames.add((blob as CloudBlob).name.toString())
    }
    return blobNames.toTypedArray()
}
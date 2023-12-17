package com.fititu.logoquizitu.Model

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class FileManagement {

    fun delete_file(dirPath: File, fileName : String, appContext : Context) : Boolean{ //dirEmpty : Boolean,
        val path : String = dirPath.absolutePath + "/" + getFileNameAndType(fileName)

        var fileR : File = File(path)/* "/data/user/0/com.fititu.logoquizitu/files/f1.jpg"
        if(dirEmpty){

            Log.d("File path", "path dir empty ${fileName}")
            fileR = File(fileName)
        }
        else {
            val fName = getFileNameAndType(fileName)
            Log.d("File path", "path with dir ${dirPath} ${fName} ${fileName}")
            val fileUri = FileProvider.getUriForFile(appContext, "com.fititu.logoquizitu.", fileR)
            if (fName != null)
                fileR = File(dirPath, fName)
        }*/
        try {
           // fileR = File("/data/user/0/com.fititu.logoquizitu/files/f1.jpg")
            if (fileR.exists()) {
                if (fileR.delete()) {
                    Log.d("Test out", "File deleted")
                    return true
                } else {
                    Log.d("Test out", "File not deleted")
                    return false
                }
            } else {
                Log.d("Test out", "File not exist abs path ${fileR.absolutePath} canonical path ${fileR.canonicalPath} ")
                //Toast.makeText(appContext, "Error file not exist the image", Toast.LENGTH_SHORT).show()
            }
        } catch (e : Exception){
            Log.e("ERR", "Error deleting the image")
            //Toast.makeText(appContext, "Error deleting the image", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    fun getFileNameAndType(wholePath: String?): String? {
        if (wholePath == null) return null

        val uri = Uri.parse(wholePath)

        // Get the path from the URI
        val path = uri.path ?: return null

        // Use File to get the file name
        val file = File(path)

        // Get the file name
        val fileName = file.name

        // Find the last dot in the file name
        val lastDotIndex = fileName.lastIndexOf(".")

        // Check if a dot is found and it's not the last character in the string
        return if (lastDotIndex != -1 && lastDotIndex < fileName.length - 1) {
            // The substring before the last dot is the file name
            val name = fileName.substring(0, lastDotIndex)
            // The substring after the last dot is the file extension
            val extension = fileName.substring(lastDotIndex + 1)
            name + "." + extension
        } else {
            null
        }
    }

}
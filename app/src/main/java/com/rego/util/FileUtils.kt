package com.rego.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    
    /**
     * Converts a content:// or file:// URI to a File that can be uploaded
     * Creates a temporary file in the cache directory
     */
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            when (uri.scheme) {
                "content" -> getFileFromContentUri(context, uri)
                "file" -> uri.path?.let { File(it) }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Handles content:// URIs by copying to temp file
     */
    private fun getFileFromContentUri(context: Context, uri: Uri): File? {
        return try {
            // Get the original filename
            val fileName = getFileName(context, uri) ?: "rego_image_${System.currentTimeMillis()}.jpg"
            
            // Create temp file in cache directory
            val tempFile = File(context.cacheDir, fileName)
            
            // Copy content to temp file
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets the original filename from a content URI
     */
    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        }
        
        if (fileName == null) {
            fileName = uri.path?.substringAfterLast('/') ?: "rego_image_${System.currentTimeMillis()}.jpg"
        }
        
        return fileName
    }

    /**
     * Gets file extension from URI
     */
    fun getFileExtension(context: Context, uri: Uri): String {
        return when (uri.scheme) {
            "content" -> {
                val type = context.contentResolver.getType(uri)
                when (type) {
                    "image/jpeg", "image/jpg" -> "jpg"
                    "image/png" -> "png"
                    "image/gif" -> "gif"
                    "image/webp" -> "webp"
                    else -> "jpg"
                }
            }
            "file" -> {
                uri.path?.substringAfterLast('.', "jpg") ?: "jpg"
            }
            else -> "jpg"
        }
    }

    /**
     * Deletes a file safely
     */
    fun deleteFile(file: File?): Boolean {
        return try {
            file?.delete() ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Cleans up temporary files after upload
     */
    fun cleanupTempFiles(files: List<File>) {
        files.forEach { file ->
            try {
                if (file.exists() && file.path.contains("cache")) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
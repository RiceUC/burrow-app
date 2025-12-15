package com.clarice.burrow.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object FileDownloadHelper {
    
    /**
     * Simpan audio file ke local storage
     * Path: /storage/emulated/0/Music/Burrow/
     */
    fun saveAudioFile(
        context: Context,
        filename: String,
        fileBytes: ByteArray
    ): File? {
        return try {
            // Buat folder jika belum ada
            val musicDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                "Burrow"
            )
            
            if (!musicDir.exists()) {
                musicDir.mkdirs()
            }
            
            // Simpan file
            val audioFile = File(musicDir, filename)
            FileOutputStream(audioFile).use { fos ->
                fos.write(fileBytes)
            }
            
            audioFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get path folder musik Burrow
     */
    fun getBurrowMusicPath(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "Burrow"
        )
    }
    
    /**
     * Delete audio file
     */
    fun deleteAudioFile(filename: String): Boolean {
        return try {
            val file = File(getBurrowMusicPath(), filename)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check file size (formatting)
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes <= 0 -> "0 B"
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}

package com.blczy.maltiprac.listening

import android.content.Context
import android.util.Log
import com.blczy.maltiprac.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

val indicesMap = mapOf(Category.Shopping to listOf(PsmDescriptor(R.string.psm_0_description, 0),
    PsmDescriptor(R.string.psm_3_description, 3)))

class PsmDescriptor(val descriptionStringIndex: Int, val index: Int) {}

@Serializable
data class Stop(
    val sentence: String,
    val audio_stop: Double
)

class Psm(private val context: Context, val index: Int) {
    companion object {
        private const val BASE_FILE_URL =
            "https://raw.githubusercontent.com/BL-CZY/malti_practice_files/master/"
        private const val TAG = "PsmManager"


        /**
         * Recursively deletes a directory and all its contents.
         *
         * @param directory The directory to delete
         * @return true if the deletion was successful, false otherwise
         */
        private fun deleteDirectoryContents(directory: File): Boolean {
            if (!directory.exists()) {
                return true
            }

            var result = true

            // Get all files and subdirectories
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    // Recursively delete contents of subdirectory
                    val subDirResult = deleteDirectoryContents(file)
                    // Delete the now-empty subdirectory
                    val deleted = file.delete()
                    result = result && subDirResult && deleted
                } else {
                    // Delete file
                    val deleted = file.delete()
                    result = result && deleted
                }
            }

            return result
        }

        /**
         * Removes all content under the psms/ directory in the app's files directory.
         *
         * @param context The Android application context
         * @return true if the cleanup was successful, false otherwise
         */
        fun cleanupPsmDirectory(context: Context): Boolean {
            try {
                val psmBaseDirectory = File(context.filesDir, "psms")

                // Check if the directory exists
                if (!psmBaseDirectory.exists()) {
                    // Nothing to delete, consider it a success
                    return true
                }

                // Delete all contents recursively
                return deleteDirectoryContents(psmBaseDirectory)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }

    /**
     * Gets the PSM data for the specified index.
     * Checks local storage first and downloads files if needed.
     *
     * @return Pair of stops list and path to audio file
     */
    suspend fun getPsm(): Pair<List<Stop>, String> = withContext(Dispatchers.IO) {
        val psmDirectory = File(context.filesDir, "psms/$index")
        if (!psmDirectory.exists()) {
            psmDirectory.mkdirs()
        }

        val stopsFile = File(psmDirectory, "stops.json")
        val audioFile = File(psmDirectory, "audio.wav")

        // Check if files exist locally
        val stopsFileExists = stopsFile.exists() && stopsFile.length() > 0
        val audioFileExists = audioFile.exists() && audioFile.length() > 0

        // Download files if they don't exist
        if (!stopsFileExists) {
            downloadFile("$BASE_FILE_URL/assets/psms/$index/stops.json", stopsFile)
        }

        if (!audioFileExists) {
            downloadFile("$BASE_FILE_URL/assets/psms/$index/audio.wav", audioFile)
        }

        // Parse stops.json
        val stopsJson = stopsFile.readText()
        val stops = Json.decodeFromString<List<Stop>>(stopsJson)

        return@withContext Pair(stops, audioFile.absolutePath)
    }

    /**
     * Downloads a file from the given URL and saves it to the specified file
     *
     * @param url The URL of the file to download
     * @param destinationFile The file to save the downloaded content to
     */
    private suspend fun downloadFile(url: String, destinationFile: File) = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Failed to download file: ${response.code} ${response.message}")
                }

                val inputStream = response.body?.byteStream()
                if (inputStream == null) {
                    throw Exception("Response body is null")
                }

                FileOutputStream(destinationFile).use { outputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }
            }

            Log.d(TAG, "File downloaded successfully: ${destinationFile.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading file", e)
            throw e
        }
    }
}
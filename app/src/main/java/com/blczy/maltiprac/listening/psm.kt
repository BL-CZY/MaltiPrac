package com.blczy.maltiprac.listening

import com.blczy.maltiprac.R
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

val indicesMap = mapOf("shopping" to listOf(PsmDescriptor(R.string.psm_0_description, 0)))

class PsmDescriptor(val descriptionStringIndex: Int, val index: Int) {}

@Serializable
data class Stop(
    val sentence: String,
    val audio_stop: Double
)

class Psm(private val context: Context, val index: Int) {
    companion object {
        private const val BASE_FILE_URL = "https://raw.githubusercontent.com/BL-CZY/malti_practice_files/master/"
        private const val TAG = "PsmManager"
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
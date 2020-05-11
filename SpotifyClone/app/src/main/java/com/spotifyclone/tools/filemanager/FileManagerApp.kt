package com.spotifyclone.tools.filemanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.spotifyclone.data.model.Music
import java.io.*
import java.util.*
import kotlin.system.exitProcess
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.util.Log
import androidx.constraintlayout.widget.Constraints.TAG


class FileManagerApp {

    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyAvailableStorage(context: Context, filesDir: File) {
        val storageManager = context.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)

        if (availableBytes >= NUM_BYTES_NEEDED_FOR_APP) {
            storageManager.allocateBytes(appSpecificInternalDirUuid,
                NUM_BYTES_NEEDED_FOR_APP
            )
        } else {
            val storageIntent = Intent().apply {
                action = ACTION_MANAGE_STORAGE
            }

            context.startActivity(storageIntent)
        }

    }

    companion object {
        private const val NUM_BYTES_NEEDED_FOR_APP = 1024 * 1024 * 10L
        private const val PATH_MUSIC_LIST_DIRECTORY = "musics"

        fun createFile(context: Context, filename: String, contents: String, parentPath: String = "") {
            val storageLocalPath =
                getStorageLocationPath(
                    context
                )
            val directoryName = "$storageLocalPath/$parentPath"
            val newDirectory = File(directoryName)

            if (!newDirectory.exists()) {
                newDirectory.mkdirs()
            }

            val appFile = File(newDirectory, filename)


            try{
                val fw = FileWriter(appFile.absoluteFile)
                val bw = BufferedWriter(fw)

                bw.write(contents)
                bw.close()
            } catch (e: IOException) {
                e.printStackTrace()
                exitProcess(-1)
            }

        }

        @SuppressLint("Recycle")
        fun getMusicMediaStorage(context: Context) {
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_ADDED

            )
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, null
            )

            if (cursor != null) {
                val uris = mutableListOf<String>()
                while (cursor.moveToNext()) {
                    val r = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                    if (r != - 1) {
                        uris.add(cursor.getString(r))
                    }

                }

                print(uris)
            }
        }

        fun getMusicList(context: Context): MutableList<Music> {
            getMusicMediaStorage(context)


            val directoryDefault = File(
                getDirectoryNameDefault(
                    context
                )
            )

            if (!directoryDefault.exists()) {
                directoryDefault.mkdirs()
            }
            val list = directoryDefault.listFiles { _, name -> name.toLowerCase().endsWith(".txt") }

            val musics = mutableListOf<Music>()

            for (file in list!!) {
                try {
                    val fr = FileReader(file.absoluteFile)
                    val br = BufferedReader(fr)

                    val musicName = br.readLine()
                    val musicAuthor = br.readLine()
                    val musicAlbum = br.readLine()

                    musics.add(Music(musicName?:"", musicAuthor?:"", musicAlbum?:""))

                    br.close()

                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            return musics
        }

        private fun getDirectoryNameDefault(context: Context) =
            "${getStorageLocationPath(
                context
            )}/$PATH_MUSIC_LIST_DIRECTORY"


        private fun getStorageLocationPath(applicationContext: Context): String? {
            val isWritable =
                isExternalStorageWritable()

            if (isWritable) {
                val externalStorageVolumes: Array<out File> =
                    ContextCompat.getExternalFilesDirs(applicationContext, null)

                return externalStorageVolumes[0].path
            }

            return null
        }

        // Checks if a volume containing external storage is available
        // for read and write.
        private fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }
    }
}
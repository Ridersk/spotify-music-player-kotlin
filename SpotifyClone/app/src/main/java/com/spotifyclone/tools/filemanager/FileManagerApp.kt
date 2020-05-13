package com.spotifyclone.tools.filemanager

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
import android.annotation.SuppressLint
import androidx.loader.content.CursorLoader
import kotlin.collections.HashMap


class FileManagerApp {

    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyAvailableStorage(context: Context, filesDir: File, requiredStorageBytes: Long) {
        val storageManager = context.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)

        if (availableBytes >= requiredStorageBytes) {
            storageManager.allocateBytes(appSpecificInternalDirUuid,
                requiredStorageBytes
            )
        } else {
            val storageIntent = Intent().apply {
                action = ACTION_MANAGE_STORAGE
            }

            context.startActivity(storageIntent)
        }

    }

    companion object {
        private const val PATH_MUSIC_LIST_DIRECTORY = "musics"
        const val SEARCH_ALL_DIRECTORIES = "all_dir"
        const val SEARCH_SCOPED_DIRECTORY = "scope_dir"

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

        fun getMusicList(context: Context, searchRange: String = SEARCH_ALL_DIRECTORIES): MutableList<Music> {
            return when (searchRange) {
                SEARCH_ALL_DIRECTORIES -> getMusicMediaStorage(context)
                SEARCH_SCOPED_DIRECTORY -> getMusicsScoped(context)
                else -> getMusicsScoped(context)
            }
        }

        @SuppressLint("Recycle")
        private fun getMusicMediaStorage(context: Context): MutableList<Music> {
            val projection = arrayOf(
                "COUNT(" + MediaStore.Files.FileColumns.DATA + ") AS totalFiles",
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME
            )

            val selection =
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                        ") GROUP BY (" + MediaStore.Files.FileColumns.PARENT

            val sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " ASC"

            val cursorLoader = CursorLoader(
                context,
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                sortOrder
            )

            val musics = mutableListOf<Music>()
            val cursor = cursorLoader.loadInBackground()
            if (cursor != null) {
                val uris = mutableListOf<String>()
                while (cursor.moveToNext()) {
                    val name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    if (name != - 1) {
                        uris.add(cursor.getString(name))

                        musics.add(Music(cursor.getString(name)))
                    }
                }
            }

            return musics
        }

        private fun getMusicsScoped(context: Context): MutableList<Music> {
            val directoryDefault = File(getDirectoryNameDefault(context))

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
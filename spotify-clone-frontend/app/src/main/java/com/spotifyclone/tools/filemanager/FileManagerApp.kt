package com.spotifyclone.tools.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
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


class FileManagerApp {

    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyAvailableStorage(context: Context, filesDir: File, requiredStorageBytes: Long) {
        val storageManager = context.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long = storageManager.getAllocatableBytes(appSpecificInternalDirUuid)

        if (availableBytes >= requiredStorageBytes) {
            storageManager.allocateBytes(
                appSpecificInternalDirUuid,
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
        private const val SEARCH_SCOPED_DIRECTORY = "scope_dir"
        private const val SEARCH_MEDIA_AUDIO = "media_audio_dir"

        fun createFile(
            context: Context,
            filename: String,
            contents: String,
            parentPath: String = ""
        ) {

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


            try {
                val fw = FileWriter(appFile.absoluteFile)
                val bw = BufferedWriter(fw)

                bw.write(contents)
                bw.close()
            } catch (e: IOException) {
                e.printStackTrace()
                exitProcess(-1)
            }

        }

        fun getMusicList(
            context: Context,
            searchRange: String = SEARCH_MEDIA_AUDIO
        ): MutableList<Music> {
            return when (searchRange) {
                SEARCH_MEDIA_AUDIO -> getMusicsFromMediaAudioDirectories(context)
                SEARCH_SCOPED_DIRECTORY -> getMusicsScoped(context)
                else -> getMusicsScoped(context)
            }
        }

        @SuppressLint("Recycle")
        private fun getMusicsFromMediaAudioDirectories(context: Context): MutableList<Music> {
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM
            )

            val order = MediaStore.Files.FileColumns.DISPLAY_NAME + " ASC"

            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                order
            )

            val musics = mutableListOf<Music>()
            if (cursor != null) {
                val id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val album = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val author = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

                val mediaRetriever = MediaMetadataRetriever()


                while (cursor.moveToNext()) {

                    musics.add(
                        Music(
                            contentUriId = cursor.getLong(id),
                            title = if (cursor.getString(name).contains("<unknown>")) "" else cursor.getString(
                                name
                            ),
                            artist = if (cursor.getString(author).contains("<unknown>")) "" else cursor.getString(
                                author
                            ),
                            album = if (cursor.getString(album).contains("<unknown>")) "" else cursor.getString(
                                album
                            )
                        )
                    )

                }
            }
            return musics
        }

        private fun getMusicsScoped(context: Context): MutableList<Music> {
            val directoryDefault = File(getDirectoryNameDefault(context))

            if (!directoryDefault.exists()) {
                directoryDefault.mkdirs()
            }
            val list = directoryDefault.listFiles { _, name ->
                name.toLowerCase(Locale.ROOT).endsWith(".txt")
            }

            val musics = mutableListOf<Music>()

            for (file in list!!) {
                try {
                    val fr = FileReader(file.absoluteFile)
                    val br = BufferedReader(fr)

                    val musicName = br.readLine()
                    val musicAuthor = br.readLine()
                    val musicAlbum = br.readLine()

                    musics.add(Music(musicName ?: "", musicAuthor ?: "", musicAlbum ?: ""))

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
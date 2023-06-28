package com.example.filedemo

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.filedemo.dao.UserDao
import com.example.filedemo.databaseconfig.AppDatabase
import com.example.filedemo.databinding.ActivityMainBinding
import com.example.filedemo.enetity.User
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var userDao:UserDao

    private var Id:Long=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDao=AppDatabase.getDatabase(this.applicationContext).userDao()
        binding.savetofile.setOnClickListener {
            savefile()
        }
        binding.getfile.setOnClickListener {
            getFile()
        }
    }

    fun savefile(){
        val bitmap=binding.imageview1.drawable.toBitmap(
            binding.imageview1.width,
            binding.imageview1.height,
            null
        )
        val contentValues=ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,System.currentTimeMillis())
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/png")
        val path=String.format("${Environment.DIRECTORY_PICTURES}/filedemo")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,path)

        val uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
        if (uri!=null){
            try {
                val outputStream=contentResolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
                if (outputStream != null) {
                    outputStream.close()
                }
                val user= uri.path?.let { User(it) }
                user?.let {
                   thread {
                       user.id=userDao.insertUser(it)
                       Id= user.id
                   }
                }
                Log.d("MainAc","User="+user)
                Toast.makeText(this,"添加图片成功",Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("Range")
    fun getFile(){
        val cursor=contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED+" desc"
        )
         var user:User=User("ceshi")
        thread {
            user= userDao.loadUser(Id)
        }
        Log.d("MainAc",user.toString())
        if (cursor!=null){
            while (cursor.moveToNext()){
                val id=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val displayName=
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                val uri=
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)
                Log.d("MainAc",user.id.toString())
                Log.d("MainActivity","displayname"+displayName)
                Log.d("MainAc","uri:"+uri)
                Log.d("MainAc","user.displayname= "+user.displayName)
                if (user.displayName==uri.path){
                    Log.d("MainAc","成果查找到")
                    val bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                    binding.imageview4.setImageBitmap(bitmap)
                }
            }
            cursor.close()
        }
    }
}
package com.example.filedemo.enetity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(var displayName:String){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}

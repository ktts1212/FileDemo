package com.example.filedemo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.filedemo.enetity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User):Long

    @Query("select * from User where id=:id")
    fun loadUser(id:Long):User

    @Query("select * from User")
    fun loadAllUsers():List<User>
}

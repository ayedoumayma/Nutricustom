package com.example.nutricustomapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutricustomapp.models.User

@Dao
interface UserDAO {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    suspend fun updatePassword(email: String, newPassword: String)
}
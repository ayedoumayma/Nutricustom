package com.example.nutricustomapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String, // Format: "dd/MM/yyyy"
    val email: String,
    val password: String
)
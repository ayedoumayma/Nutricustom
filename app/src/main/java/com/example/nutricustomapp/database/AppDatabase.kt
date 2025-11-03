package com.example.nutricustomapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import com.example.nutricustomapp.models.Game
import com.example.nutricustomapp.models.User

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO
    //abstract fun gameDAO(): GameDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamer_app_database"
                )
                    .fallbackToDestructiveMigration() // Handle version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
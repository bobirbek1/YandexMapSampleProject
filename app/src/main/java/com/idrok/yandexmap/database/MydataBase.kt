package com.idrok.yandexmap.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.idrok.yandexmap.models.PlaceModel

@Database(entities = [PlaceModel::class], version = 1, exportSchema = false)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun dao(): MyDao

    companion object {
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context): MyRoomDatabase {

            if (INSTANCE != null) {
                return INSTANCE!!
            }

            synchronized(this) {
                INSTANCE = Room.databaseBuilder(context, MyRoomDatabase::class.java, "example")
                    .build()
                return INSTANCE!!
            }
        }
    }

}
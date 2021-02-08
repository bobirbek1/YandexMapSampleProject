package com.idrok.yandexmap.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.idrok.yandexmap.models.PlaceModel

@Dao
interface MyDao {

    @Query("Select * from placeModel order by time DESC")
    fun getPlaces(): LiveData<List<PlaceModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(placeModel: PlaceModel)

}
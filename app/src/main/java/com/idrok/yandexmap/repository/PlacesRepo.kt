package com.idrok.yandexmap.repository

import androidx.lifecycle.LiveData
import com.idrok.yandexmap.database.MyDao
import com.idrok.yandexmap.models.PlaceModel

class PlacesRepo(private val dataSource: MyDao) {

    fun getAllPlaces(): LiveData<List<PlaceModel>> {
        return dataSource.getPlaces()
    }

    suspend fun insertPlace(placeModel: PlaceModel) {
        dataSource.insertPlaces(placeModel)
    }

}
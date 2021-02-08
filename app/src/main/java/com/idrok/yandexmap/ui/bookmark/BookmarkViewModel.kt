package com.idrok.yandexmap.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.idrok.yandexmap.database.MyDao
import com.idrok.yandexmap.models.PlaceModel
import com.idrok.yandexmap.repository.PlacesRepo

class BookmarkViewModel(dataSource: MyDao) : ViewModel() {

    private val placesRepo = PlacesRepo(dataSource)

    fun getAllPlaces(): LiveData<List<PlaceModel>> {
        return placesRepo.getAllPlaces()
    }

}
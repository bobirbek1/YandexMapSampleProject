package com.idrok.yandexmap.ui.saveDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idrok.yandexmap.database.MyDao
import com.idrok.yandexmap.models.PlaceModel
import com.idrok.yandexmap.repository.PlacesRepo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaveDialogViewModel(dataSource: MyDao) : ViewModel() {

    private val placesRepo = PlacesRepo(dataSource)

    fun insertPLace(placeModel: PlaceModel) {
        viewModelScope.launch {
            withContext(IO) {
                placesRepo.insertPlace(placeModel)
            }
        }
    }

}
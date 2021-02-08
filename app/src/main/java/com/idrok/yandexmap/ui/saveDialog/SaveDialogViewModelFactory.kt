package com.idrok.yandexmap.ui.saveDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.idrok.yandexmap.database.MyDao

class SaveDialogViewModelFactory(private val dataSource: MyDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveDialogViewModel::class.java)) {
            return SaveDialogViewModel(dataSource) as T
        } else {
            throw IllegalArgumentException("SaveDialogViewModel class not found")
        }
    }
}
package com.idrok.yandexmap.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.idrok.yandexmap.database.MyDao

class BookmarkViewModelFactory(private val dataSource:MyDao):ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarkViewModel::class.java)){
            return  BookmarkViewModel(dataSource) as T
        }else{
            throw IllegalArgumentException("BookmarkViewModel class not found")
        }
    }
}
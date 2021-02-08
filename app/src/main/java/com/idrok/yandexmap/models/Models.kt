package com.idrok.yandexmap.models

import android.text.SpannableString
import androidx.room.Entity
import androidx.room.PrimaryKey


data class Place(
    val displayName: SpannableString,
    val distance: String = "",
    val address: String = "",
    val uri: String = ""
)

@Entity(tableName = "placeModel")
data class PlaceModel(
    var displayName: String = "",
    var address: String = "",
    var rating: Float = -1f,
    var review: String = "",
    var workingHours: String = "",
    @PrimaryKey
    var time: Long = 0,
    var lat: Double = 0.0,
    var longitude: Double = 0.0
)

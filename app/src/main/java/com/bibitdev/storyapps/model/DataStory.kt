package com.bibitdev.storyapps.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataStory (
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
) : Parcelable

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<DataStory>
)
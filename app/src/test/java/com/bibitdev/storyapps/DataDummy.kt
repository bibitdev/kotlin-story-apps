package com.bibitdev.storyapps

import com.bibitdev.storyapps.model.DataStory

object DataDummy {
    fun generateDummyStoryResponse(): List<DataStory> {
        val items = mutableListOf<DataStory>()
        for (i in 1..10) {
            val story = DataStory(
                photoUrl = "http://photo$i.com",
                createdAt = "2023-12-16T10:00:00Z",
                name = "Story $i",
                description = "Description of Story $i",
                lon = 100.0 + i,
                id = i.toString(),
                lat = -10.0 - i
            )
            items.add(story)
        }
        return items
    }
}
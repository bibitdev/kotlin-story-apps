package com.bibitdev.storyapps.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bibitdev.storyapps.api.ApiService
import com.bibitdev.storyapps.model.DataStory

class PagingStory (private val apiService: ApiService, private val token: String) : PagingSource<Int, DataStory>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val PAGE_SIZE = 3
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataStory> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStoriesWithLocation(
                token = "Bearer $token",
                page = page,
                size = PAGE_SIZE,
                location = 0
            )

            LoadResult.Page(
                data = response.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (response.listStory.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
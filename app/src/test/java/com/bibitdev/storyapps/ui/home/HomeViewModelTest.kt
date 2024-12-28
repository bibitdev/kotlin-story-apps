package com.bibitdev.storyapps.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.bibitdev.storyapps.DataDummy
import com.bibitdev.storyapps.MainDispatcherRule
import com.bibitdev.storyapps.adapter.StoryAdapter
import com.bibitdev.storyapps.getOrAwaitValue
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule val  mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var userRepository: UserRepository

    @Test fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data = PagingData.from(dummyStories)

        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTkyYlBQZDZ3emZhaFFIRTIiLCJpYXQiOjE3MzUxNTM1MTN9.bqU5JNqQLlppdWv8ppQug-xVAuzaI3IznitHSV9Y8QE"
        Mockito.`when`(userRepository.getStoriesPaging(token)).thenReturn(flowOf(data))

        val homeViewModel = HomeViewModel(userRepository)
        val actualStories = homeViewModel.getStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryComparator,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data = PagingData.from(emptyList<DataStory>())

        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTkyYlBQZDZ3emZhaFFIRTIiLCJpYXQiOjE3MzUxNTM1MTN9.bqU5JNqQLlppdWv8ppQug-xVAuzaI3IznitHSV9Y8QE"
        Mockito.`when`(userRepository.getStoriesPaging(token)).thenReturn(flowOf(data))

        val homeViewModel = HomeViewModel(userRepository)
        val actualStories = homeViewModel.getStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryComparator,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
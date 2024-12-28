package com.bibitdev.storyapps.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.adapter.LoadingAdapter
import com.bibitdev.storyapps.adapter.StoryAdapter
import com.bibitdev.storyapps.api.ApiConfig
import com.bibitdev.storyapps.databinding.ActivityHomeBinding
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.repository.UserRepository
import com.bibitdev.storyapps.ui.ViewModelFactory
import com.bibitdev.storyapps.ui.addstory.AddStoryActivity
import com.bibitdev.storyapps.ui.authentication.login.LoginActivity
import com.bibitdev.storyapps.ui.detail.DetailActivity
import com.bibitdev.storyapps.ui.map.MapsActivity
import com.bibitdev.storyapps.utils.PreferencesHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var preferencesHelper: PreferencesHelper
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(UserRepository(ApiConfig.apiService))
    }

    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        val token = preferencesHelper.loadUser()?.token
        if (token.isNullOrEmpty()) {
            navigateToLogin()
            return
        }

        initializeUI()
        initializeObserver(token)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                true
            }

            R.id.action_maps -> {
                openMapsActivity()
                true
            }

            else -> {
                false
            }
        }
    }

    private fun openMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun logoutUser() {
        preferencesHelper.clear()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun initializeUI() {
        setSupportActionBar(binding.toolbar)
        adapter = StoryAdapter()

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefresh.isRefreshing = false

        }


        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataStory, view: View) {
                val intent = Intent(this@HomeActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.STORY_KEY, data)
                }
                startActivity(intent)
            }
        })

        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingAdapter()

        )
    }

    private fun initializeObserver(token: String) {
        lifecycleScope.launch {
            homeViewModel.getStories(token).collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        adapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            val isEmpty = loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
        }
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}

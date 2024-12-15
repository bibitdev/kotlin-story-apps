package com.bibitdev.storyapps.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.adapter.StoryAdapter
import com.bibitdev.storyapps.api.ApiConfig
import com.bibitdev.storyapps.databinding.ActivityHomeBinding
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.repository.UserRepository
import com.bibitdev.storyapps.ui.ViewModelFactory
import com.bibitdev.storyapps.ui.addstory.AddStoryActivity
import com.bibitdev.storyapps.ui.authentication.login.LoginActivity
import com.bibitdev.storyapps.ui.detail.DetailActivity
import com.bibitdev.storyapps.utils.PreferencesHelper
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var preferencesHelper: PreferencesHelper
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(UserRepository(ApiConfig.apiService))
    }

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
        initializeObserver()

        homeViewModel.fetchStories(token)
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
            else -> {
                false
            }
        }
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

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            val token = preferencesHelper.loadUser()?.token.orEmpty()
            homeViewModel.fetchStories(token)
        }
    }

    private fun initializeObserver() {
        homeViewModel.stories.observe(this) { response ->
            binding.swipeRefresh.isRefreshing = false
            if (!response.error) {
                val adapter = StoryAdapter(response.listStory)
                binding.rvStories.layoutManager = LinearLayoutManager(this)
                binding.rvStories.adapter = adapter

                adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: DataStory, view: View) {
                        val intent = Intent(this@HomeActivity, DetailActivity::class.java).apply {
                            putExtra("story", data)
                        }
                        startActivity(intent)
                    }
                })
            } else {
                Snackbar.make(binding.root, getString(R.string.gagalmemuatstory), Snackbar.LENGTH_LONG).show()
            }
        }

        homeViewModel.error.observe(this) { errorMessage ->
            binding.swipeRefresh.isRefreshing = false
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
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

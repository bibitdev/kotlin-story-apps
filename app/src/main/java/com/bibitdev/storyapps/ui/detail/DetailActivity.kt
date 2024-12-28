package com.bibitdev.storyapps.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.databinding.ActivityDetailBinding
import com.bibitdev.storyapps.model.DataStory
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story: DataStory? = intent.getParcelableExtra("story")
        story?.let { prepareDetail(it) } ?: finish()
    }

    private fun prepareDetail(story: DataStory) {
        binding.apply {
            tvNameDetail.text = story.name
            tvDeskripsiDetail.text = story.description
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .placeholder(R.drawable.hello)
                .error(R.drawable.error)
                .into(ivDetail)
        }
    }

    companion object {
        const val STORY_KEY = "story"
    }
}


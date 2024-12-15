package com.bibitdev.storyapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.databinding.StoryItemBinding
import com.bumptech.glide.Glide

class StoryAdapter(private val listStory: List<DataStory>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(data: DataStory, view: View)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class StoryViewHolder(private val binding: StoryItemBinding ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: DataStory) {
            binding.apply {
                name.text = story.name
                tvItemDescription.text = story.description

                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.hello)
                    .error(R.drawable.error)
                    .into(photo)

                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(story, photo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size
}
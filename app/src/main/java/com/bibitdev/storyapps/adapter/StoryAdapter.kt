package com.bibitdev.storyapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibitdev.storyapps.R
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.databinding.StoryItemBinding
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide

class StoryAdapter :
    PagingDataAdapter<DataStory, StoryAdapter.StoryViewHolder>(StoryComparator) {

    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(data: DataStory, view: View)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class StoryViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: DataStory?) {
            if (story != null) {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    object StoryComparator : DiffUtil.ItemCallback<DataStory>() {
        override fun areItemsTheSame(oldItem: DataStory, newItem: DataStory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataStory, newItem: DataStory): Boolean {
            return oldItem == newItem
        }
    }
}

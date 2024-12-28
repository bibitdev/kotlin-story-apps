package com.bibitdev.storyapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibitdev.storyapps.databinding.ActivityLoadingStateAdapterBinding

class LoadingAdapter : LoadStateAdapter<LoadingAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ActivityLoadingStateAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadingStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(private val binding: ActivityLoadingStateAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.progressBar.visibility =
                if (loadState is LoadState.Loading) View.VISIBLE else View.GONE

            binding.btnRetry.visibility =
                if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.errorMessage.visibility =
                if (loadState is LoadState.Error) View.VISIBLE else View.GONE

            if (loadState is LoadState.Error) {
                binding.errorMessage.text = loadState.error.localizedMessage
            }

            binding.btnRetry.setOnClickListener {
            }
        }
    }
}

package com.khoerulih.storyapp.ui.pages

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khoerulih.storyapp.data.remote.responses.ListStoryItem
import com.khoerulih.storyapp.databinding.ItemRowStoryBinding
import com.khoerulih.storyapp.ui.pages.detailstory.DetailStoryActivity
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class ListStoryAdapter() :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if(data != null) {
            holder.bind(data)
        }
    }

    class ListViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataStory: ListStoryItem) {
            Glide.with(itemView.context)
                .load(dataStory.photoUrl)
                .into(binding.ivItemThumbnail)
            binding.tvItemName.text = dataStory.name
            itemView.setOnClickListener {
                val goToDetailActivity = Intent(itemView.context, DetailStoryActivity::class.java)
                goToDetailActivity.putExtra(DetailStoryActivity.EXTRA_STORY, dataStory)

                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivItemThumbnail, "detail_thumbnail"),
                    Pair(binding.tvItemName, "detail_name")
                )
                itemView.context.startActivity(goToDetailActivity, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
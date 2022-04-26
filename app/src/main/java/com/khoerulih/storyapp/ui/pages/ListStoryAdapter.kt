package com.khoerulih.storyapp.ui.pages

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khoerulih.storyapp.data.ListStoryItem
import com.khoerulih.storyapp.databinding.ItemRowStoryBinding
import com.khoerulih.storyapp.ui.pages.detailstory.DetailStoryActivity
import androidx.core.util.Pair

class ListStoryAdapter(private val listStory: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (photoUrl, createdAt, name, description, long, id, lat) = listStory[position]

        val dataStory = ListStoryItem(
            photoUrl, createdAt, name, description, long, id, lat
        )

        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .into(holder.binding.ivItemThumbnail)
        holder.binding.tvItemName.text = name
        holder.itemView.setOnClickListener {
            val goToDetailActivity = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            goToDetailActivity.putExtra(DetailStoryActivity.EXTRA_STORY, dataStory)

            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                Pair(holder.binding.ivItemThumbnail, "detail_thumbnail"),
                Pair(holder.binding.tvItemName, "detail_name")
            )
            holder.itemView.context.startActivity(goToDetailActivity, optionsCompat.toBundle())
        }
    }

    override fun getItemCount(): Int = listStory.size

    class ListViewHolder(var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)
}
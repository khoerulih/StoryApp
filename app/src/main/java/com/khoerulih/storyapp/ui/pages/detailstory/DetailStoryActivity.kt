package com.khoerulih.storyapp.ui.pages.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.khoerulih.storyapp.data.ListStoryItem
import com.khoerulih.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val dataStory = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        binding?.let {
            Glide.with(this)
                .load(dataStory?.photoUrl)
                .into(it.ivDetailThumbnail)
            it.tvDetailName.text = dataStory?.name
            it.tvDetailDescription.text = dataStory?.description
        }
        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
package com.khoerulih.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.khoerulih.storyapp.data.remote.responses.ListStoryItem
import com.khoerulih.storyapp.data.remote.retrofit.ApiService

//class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
//    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
//        return try {
//            val position = params.key ?: INITIAL_PAGE_INDEX
//            val client = apiService.getAllStories()
//        }
//    }
//
//    private companion object {
//        const val INITIAL_PAGE_INDEX = 1
//    }
//}
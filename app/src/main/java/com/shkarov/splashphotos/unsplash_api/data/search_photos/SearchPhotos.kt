package com.shkarov.splashphotos.unsplash_api.data.search_photos

import com.shkarov.splashphotos.unsplash_api.data.list_photos.PhotoUnsplash
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchPhotos(
    @Json(name = "total")
    val total: Long,
    @Json(name = "total_pages")
    val totalPages: Long,
    @Json(name = "results")
    val results: List<PhotoUnsplash>
)

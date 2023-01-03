package com.shkarov.splashphotos.unsplash_api.data.collections

import com.shkarov.splashphotos.unsplash_api.data.list_photos.PhotoUnsplash
import com.shkarov.splashphotos.unsplash_api.data.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Tag

@JsonClass(generateAdapter = true)
data class CollectionUnsplash(
    @Json(name = "id")
    val id : String,
    @Json(name = "title")
    val title : String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "total_photos")
    val totalPhotos: Long,
    @Json(name = "cover_photo")
    val coverPhoto: PhotoUnsplash,
    @Json(name = "user")
    val user: User,
    val tags: List<CollectionTag>,
)


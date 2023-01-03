package com.shkarov.splashphotos.unsplash_api.data.detail_photo

import com.shkarov.splashphotos.unsplash_api.data.Location
import com.shkarov.splashphotos.unsplash_api.data.User
import com.shkarov.splashphotos.unsplash_api.data.collections.CollectionUnsplash
import com.shkarov.splashphotos.unsplash_api.data.list_photos.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailPhoto(
    @Json(name = "id")
    val id : String,
    @Json(name = "created_at")
    val createdAt : String,
    @Json(name = "updated_at")
    val updatedAt : String,
    @Json(name = "width")
    val width : Int,
    @Json(name = "height")
    val height : Int,
    @Json(name = "color")
    val color : String,
    @Json(name = "blur_hash")
    val blurHash : String,
    @Json(name = "downloads")
    val downloads : Long,
    @Json(name = "likes")
    val likes : Long,
    @Json(name = "liked_by_user")
    var likedByUser: Boolean,
    @Json(name = "public_domain")
    val publicDomain: Boolean,
    @Json(name = "description")
    val description: String?,
    @Json(name = "exif")
    val exif: Exif?,
    @Json(name = "location")
    val location: Location?,
    @Json(name = "tags")
    val tags: List<TagTitle>?,
//    @Json(name = "current_user_collections")
//    val currentUserCollectionUnsplash: List<CollectionUnsplash?>,
    @Json(name = "urls")
    val urls: Urls,
    @Json(name = "links")
    val links: Links,
    @Json(name = "user")
    val user: User
)


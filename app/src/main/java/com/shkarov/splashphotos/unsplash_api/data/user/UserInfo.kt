package com.shkarov.splashphotos.unsplash_api.data.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "id")
    val id : String,
    @Json(name = "updated_at")
    val updatedAt : String,
    @Json(name = "name")
    val name : String,
    @Json(name = "username")
    val userName : String,
    @Json(name = "first_name")
    val firstName : String,
    @Json(name = "last_name")
    val lastName : String?,
    @Json(name = "twitter_username")
    val twitterUsername : String?,
    @Json(name = "bio")
    val bio : String?,
    @Json(name = "location")
    val location : String?,
    @Json(name = "instagram_username")
    val instagramUsername : String?,
    @Json(name = "total_collections")
    val totalCollections : Long,
    @Json(name = "total_likes")
    val totalLikes : Long,
    @Json(name = "total_photos")
    val totalPhotos : Long,
    @Json(name = "downloads")
    val downloads : Long,
    @Json(name = "email")
    val email : String?,
    @Json(name = "profile_image")
    val profileImage : profileImages,
)

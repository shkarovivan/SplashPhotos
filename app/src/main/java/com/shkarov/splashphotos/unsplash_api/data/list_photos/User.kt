package com.shkarov.splashphotos.unsplash_api.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
	@Json(name = "id")
	val id: String,
	@Json(name = "updated_at")
	val updatedAt: String,
	@Json(name = "username")
	val userName: String,
	@Json(name = "name")
	val name: String,
	@Json(name = "portfolio_url")
	val portfolioUrl: String?,
	@Json(name = "bio")
	val bio: String?,
	@Json(name = "location")
	val location: String?,
	@Json(name = "total_likes")
	val totalLikes: Int,
	@Json(name = "total_photos")
	val totalPhotos: Int,
	@Json(name = "total_collections")
	val totalCollections: Int,
	@Json(name = "links")
	val links: UserLinks,
	@Json(name = "profile_image")
	val profileImage: ProfileImages
)

@JsonClass(generateAdapter = true)
data class UserLinks(
	@Json(name = "self")
	val self: String,
	@Json(name = "html")
	val html: String,
	@Json(name = "photos")
	val photos: String,
	@Json(name = "likes")
	val likes: String,
	@Json(name = "portfolio")
	val portfolio: String,
)

@JsonClass(generateAdapter = true)
data class ProfileImages(
	@Json(name = "small")
	val small: String,
	@Json(name = "medium")
	val medium: String,
	@Json(name = "large")
	val large: String
)

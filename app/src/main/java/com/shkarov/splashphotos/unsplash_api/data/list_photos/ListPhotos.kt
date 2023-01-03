package com.shkarov.splashphotos.unsplash_api.data.list_photos
import com.shkarov.splashphotos.unsplash_api.data.SponsorShip
import com.shkarov.splashphotos.unsplash_api.data.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListPhotos(
	val listPhoto: List<PhotoUnsplash>
)

@JsonClass(generateAdapter = true)
data class PhotoUnsplash(
	@Json(name = "id")
	val id : String,
	@Json(name = "created_at")
	val createdAt : String,
	@Json(name = "updated_at")
	val updatedAt : String,
	@Json(name = "promoted_at")
	val promotedAt : String?,
	@Json(name = "width")
	val width : Int,
	@Json(name = "height")
	val height : Int,
	@Json(name = "color")
	val color : String?,
	@Json(name = "blur_hash")
	val blurHash : String?,
	@Json(name = "description")
	val description : String?,
	@Json(name = "alt_description")
	val altDescription : String?,
	@Json(name = "urls")
	val urls : Urls,
	@Json(name = "links")
	val links : Links,
	@Json(name = "categories")
	val categories : List<Category>?,
	@Json(name = "likes")
	val likes : Long,
	@Json(name = "liked_by_user")
	val likedByUser : Boolean,
//	@Json(name = "current_user_collections")
//	val currentUserCollectionUnsplash : List<CollectionUnsplash?>,
	@Json(name = "sponsorship")
	val sponsorShip : SponsorShip?,
	@Json(name = "user")
	val user : User,
)

package com.shkarov.splashphotos.unsplash_api.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass( generateAdapter = true)
data class SponsorShip(
	@Json(name = "impression_urls")
	val impressionUrls: List<String>,
	@Json(name = "tagline")
	val tagLine: String?,
	@Json(name = "tagline_url")
	val taglineUrl: String?,
	@Json(name = "sponsor")
	val sponsor: Sponsor?,
)

@JsonClass( generateAdapter = true)
data class Sponsor(
	@Json(name = "id")
	val id: String?,
	@Json(name = "updated_at")
	val updatedAt: String?,
	@Json(name = "username")
	val userName: String?,
	@Json(name = "name")
	val name: String?,
	@Json(name = "first_name")
	val firstName: String?,
	@Json(name = "last_name")
	val lastName: String?,
	@Json(name = "twitter_username")
	val twitterUsername: String?,
	@Json(name = "portfolio_url")
	val portfolioUrl: String?,
	@Json(name = "bio")
	val bio: String?,
	@Json(name = "location")
	val location: String?,
	@Json(name = "links")
	val links: SponsorLinks?,
	@Json(name = "profile_image")
	val profileImage: ProfileImage?,
	@Json(name = "instagram_username")
	val instagramUsername: String?,
	@Json(name = "total_collections")
	val totalCollections: Int,
	@Json(name = "total_likes")
	val totalLikes: Int,
	@Json(name = "total_photos")
	val totalPhotos: Int,
	@Json(name = "accepted_tos")
	val acceptedTos: Boolean,
	@Json(name = "for_hire")
	val forHire: Boolean,
	@Json(name = "social")
	val social: Social?,
	)

@JsonClass( generateAdapter = true)
data class SponsorLinks(
	@Json(name = "self")
	val self: String?,
	@Json(name = "html")
	val html: String?,
	@Json(name = "photos")
	val photos: String?,
	@Json(name = "portfolio")
	val portfolio: String?,
	@Json(name = "following")
	val following: String?,
	@Json(name = "followers")
	val followers: String?,
)

@JsonClass( generateAdapter = true)
data class ProfileImage(
	@Json(name = "small")
	val small: String?,
	@Json(name = "medium")
	val medium: String?,
	@Json(name = "large")
	val large: String?,
)

@JsonClass( generateAdapter = true)
data class Social(
	@Json(name = "instagram_username")
	val instagramUsername: String?,
	@Json(name = "portfolio_url")
	val portfolioUrl: String?,
	@Json(name = "twitter_username")
	val twitterUsername: String?,
	@Json(name = "paypal_email")
	val paypalEmail: String?,
)



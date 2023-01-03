package com.shkarov.splashphotos.unsplash_api.data.list_photos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Links(
	@Json(name = "self")
	val self: String,
	@Json(name = "html")
	val html: String,
	@Json(name = "download")
	val download: String,
	@Json(name = "download_location")
	val downloadLocation: String,
)

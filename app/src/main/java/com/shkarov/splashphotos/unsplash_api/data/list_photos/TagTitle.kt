package com.shkarov.splashphotos.unsplash_api.data.list_photos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TagTitle(
	@Json(name = "type")
	val type: String,
	@Json(name = "title")
	val title: String
)
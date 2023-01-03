package com.shkarov.splashphotos.unsplash_api.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location (
	@Json(name = "name")
	val name: String?,
	@Json(name = "city")
	val city: String?,
	@Json(name = "country")
	val country: String?,
	@Json(name = "position")
	val position: Position?
)

@JsonClass(generateAdapter = true)
data class Position(
	@Json(name = "latitude")
	val latitude: Double?,
	@Json(name = "longitude")
	val longitude: Double?
)

package com.shkarov.splashphotos.unsplash_api.data.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class profileImages(
    @Json(name = "small")
    val small: String,
    @Json(name = "medium")
    val medium: String,
    @Json(name = "large")
    val large: String,
)

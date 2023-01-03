package com.shkarov.splashphotos.unsplash_api

import com.shkarov.splashphotos.unsplash_api.data.list_photos.PhotoUnsplash
import com.shkarov.splashphotos.unsplash_api.data.collections.CollectionUnsplash
import com.shkarov.splashphotos.unsplash_api.data.detail_photo.DetailPhoto
import com.shkarov.splashphotos.unsplash_api.data.list_photos.OrderBy
import com.shkarov.splashphotos.unsplash_api.data.search_photos.SearchPhotos
import com.shkarov.splashphotos.unsplash_api.data.user.UserInfo
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface UnsplashApi {
	@GET("https://api.unsplash.com/photos")
	suspend fun getListPhotos(
		@Query("page") page: Int = 1,
		@Query("per_page") perPage: Int = 10,
		@Query("order_by") orderBy: String = OrderBy.POPULAR.type
	): List<PhotoUnsplash>

	@GET("https://api.unsplash.com/collections")
	suspend fun getListCollections(
		@Query("page") page: Int = 1,
		@Query("per_page") perPage: Int = 10
	): List<CollectionUnsplash>

	@GET("https://api.unsplash.com/collections/{id}/photos")
	suspend fun getCollectionPhotos(
		@Path("id") id: String,
		@Query("page") page: Int = 1,
		@Query("per_page") perPage: Int = 10
	): List<PhotoUnsplash>

	@GET("https://api.unsplash.com/collections/{id}")
	suspend fun getCollectionInfo(
		@Path("id") id: String,
	): CollectionUnsplash

	@GET("https://api.unsplash.com/search/photos")
	suspend fun searchPhotos(
		@Query("query") query: String,
		@Query("page") page: Int = 1,
		@Query("per_page") perPage: Int = 10
	): SearchPhotos

	@GET("https://api.unsplash.com/me")
	suspend fun getUserInfo(): UserInfo

	@GET("https://api.unsplash.com/users/{username}/photos")
	suspend fun getUserPhotos(
		@Path("username") userName: String,
		@Query("page") page: Int = 1,
	@Query("per_page") perPage: Int = 10
	): List<PhotoUnsplash>

	@GET("https://api.unsplash.com/users/{username}/likes")
	suspend fun getUserLikedPhotos(
		@Path("username") userName: String,
		@Query("page") page: Int = 1,
		@Query("per_page") perPage: Int = 10
	): List<PhotoUnsplash>

	@GET("https://api.unsplash.com/users/{username}/collections")
	suspend fun getUserCollections(
		@Path("username") userName: String,
		@Query("page") page: Int = 1,
		@Query("per_page") perPage: Int = 10
	): List<CollectionUnsplash>

	@GET("https://api.unsplash.com/photos/{id}")
	suspend fun getPhotoDetailInfo(
		@Path("id") id: String
	): DetailPhoto

	@POST ("https://api.unsplash.com/photos/{id}/like")
	suspend fun setLike(
		@Path("id") id: String
	)

	@DELETE("https://api.unsplash.com/photos/{id}/like")
	suspend fun deleteLike(
		@Path("id") id: String
	)

	@GET
	suspend fun getFile(
		@Url url: String
	): ResponseBody
}
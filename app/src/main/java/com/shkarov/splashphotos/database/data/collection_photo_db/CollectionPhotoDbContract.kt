package com.shkarov.splashphotos.database.data.collection_photo_db

object CollectionPhotoDbContract {
    const val TABLE_NAME = "collection_photos_database"

    object Columns {
        const val ID = "id"
        const val COLLECTION_ID = "collection_id"
        const val PHOTO_ID = "photo_id"
        const val PHOTO_PATH  = "uri_regular"
        const val USER_NAME = "user_name"
        const val USER_AVATAR = "user_avatar"
        const val PROFILE_IMAGE_PATH = "profile_image_path"
        const val TOTAL_LIKES= "total_likes"
        const val LIKED_BY_USER= "liked_by_user"
    }
}

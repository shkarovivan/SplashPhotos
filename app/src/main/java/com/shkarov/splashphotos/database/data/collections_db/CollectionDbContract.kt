package com.shkarov.splashphotos.database.data.collections_db

object CollectionDbContract {
    const val TABLE_NAME = "collection_database"

    object Columns {
        const val ID = "id"
        const val TITLE = "title"
        const val TOTAL_PHOTOS  = "total_photos"
        const val USER_NAME = "user_name"
        const val USER_AVATAR = "user_avatar"
        const val PROFILE_IMAGE_PATH = "profile_image_path"
        const val COVER_IMAGE_PATH= "cover_image_path"
    }
}

package com.shkarov.splashphotos.database.data.photo_db

object PhotoDbContract {
    const val TABLE_NAME = "photo_database"

    object Columns {
        const val ID = "id"
        const val PHOTO_PATH  = "uri_regular"
        const val USER_NAME = "user_name"
        const val USER_AVATAR = "user_avatar"
        const val PROFILE_IMAGE_PATH = "profile_image_path"
        const val TOTAL_LIKES= "total_likes"
        const val LIKED_BY_USER= "liked_by_user"
    }
}



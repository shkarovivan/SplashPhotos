package com.shkarov.splashphotos.database.data.collection_photo_db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.shkarov.splashphotos.database.data.photo_db.PhotoDbContract


@Parcelize
@Entity(tableName = CollectionPhotoDbContract.TABLE_NAME)
data class CollectionPhotoDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.ID)
    val id: Long,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.COLLECTION_ID)
    val collectionId: String,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.PHOTO_ID)
    val photoId: String,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.PHOTO_PATH)
    var photoPath: String,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.USER_NAME)
    val userName: String,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.USER_AVATAR)
    val userAvatar: String,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.PROFILE_IMAGE_PATH)
    var profileImagePath: String,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.TOTAL_LIKES)
    val totalLikes: Long,
    @ColumnInfo(name = CollectionPhotoDbContract.Columns.LIKED_BY_USER)
    val likedByUser: Boolean
):Parcelable


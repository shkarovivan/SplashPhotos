package com.shkarov.splashphotos.database.data.photo_db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = PhotoDbContract.TABLE_NAME)

data class PhotoDb(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = PhotoDbContract.Columns.ID)
    val id: String,
    @ColumnInfo(name = PhotoDbContract.Columns.PHOTO_PATH)
    var photoPath: String,
    @ColumnInfo(name = PhotoDbContract.Columns.USER_NAME)
    val userName: String,
    @ColumnInfo(name = PhotoDbContract.Columns.USER_AVATAR)
    val userAvatar: String,
    @ColumnInfo(name = PhotoDbContract.Columns.PROFILE_IMAGE_PATH)
    var profileImagePath: String,
    @ColumnInfo(name = PhotoDbContract.Columns.TOTAL_LIKES)
    val totalLikes: Long,
    @ColumnInfo(name = PhotoDbContract.Columns.LIKED_BY_USER)
    val likedByUser: Boolean
) : Parcelable

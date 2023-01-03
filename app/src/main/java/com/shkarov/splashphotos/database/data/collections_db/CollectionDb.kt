package com.shkarov.splashphotos.database.data.collections_db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = CollectionDbContract.TABLE_NAME)
data class CollectionDb(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = CollectionDbContract.Columns.ID)
    val id: String,
    @ColumnInfo(name = CollectionDbContract.Columns.TITLE)
    val title: String,
    @ColumnInfo(name = CollectionDbContract.Columns.TOTAL_PHOTOS)
    val totalPhotos: Long,
    @ColumnInfo(name = CollectionDbContract.Columns.USER_NAME)
    val userName: String,
    @ColumnInfo(name = CollectionDbContract.Columns.USER_AVATAR)
    val userAvatar: String,
    @ColumnInfo(name = CollectionDbContract.Columns.PROFILE_IMAGE_PATH)
    var profileImagePath: String,
    @ColumnInfo(name = CollectionDbContract.Columns.COVER_IMAGE_PATH)
    var coverImagePath: String,
): Parcelable

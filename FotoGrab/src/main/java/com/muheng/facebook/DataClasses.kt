package com.muheng.facebook

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Muheng Li on 2018/7/11.
 */

interface Page<T> {
    val data: List<T>
    val paging: Paging
}

data class Albums(
        override val data: List<Album> = ArrayList<Album>(),
        override val paging: Paging = Paging()
) : Page<Album>

data class Album(
        val name: String = "",
        val photo_count: Int = 0,
        val photos: Photos = Photos(),
        val picture: Picture = Picture(),
        val id: String = "'"
)

data class Photos(
        override val data: List<Photo> = ArrayList<Photo>(),
        override val paging: Paging = Paging(),
        val id: String = "",
        val picture: String = "",
        val link: String = ""
) : Page<Photo>

@Parcelize
data class Photo(
        val id: String = "",
        val link: String = "",
        val picture: String = "",
        val name: String = "",
        val images: List<Images> = ArrayList<Images>(),
        val webp_images: List<Images> = ArrayList<Images>()
) : Parcelable

@Parcelize
data class Images(
        val height: Int = 0,
        val source: String = "",
        val width: Int = 0
) : Parcelable

data class Picture(
        val data: PicData = PicData()
)

data class PicData(
        val url: String = "",
        val width: Int = 0,
        val height: Int = 0,
        val is_silhouette: Boolean = false
)

data class Paging(
        val cursors: Cursors = Cursors(),
        val next: String = "",
        val previous: String = ""
)

data class Cursors(
        val before: String = "",
        val after: String = ""
)
package com.muheng.rxjava

import com.muheng.facebook.Album
import com.muheng.facebook.Photo

/**
 * Created by Muheng Li on 2018/7/12.
 */
class Event {

    class AlbumClicked(val album: Album)
    class PhotoClicked(val photo: Photo)

    class FabClicked(val id: Int)
    class DownloadCompleted(val path: String)
    class DownloadFailed(val msg: String? = null)
    class PermissionGranted(val permission: String)

}
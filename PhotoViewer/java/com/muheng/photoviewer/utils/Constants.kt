package com.muheng.photoviewer.utils

class Constants {
    companion object {
        val ACT_VIEW_ALBUM = "com.muheng.simplegallery.VIEW_ALBUM"
        val ACT_VIEW_PHOTO = "com.muheng.simplegallery.VIEW_PHOTO"

        val SPAN_COUNT_NORMAL: Int = 2
        val SPAN_COUNT_LARGE : Int = 5
        val SPAN_COUNT_XLARGE : Int = 8
        val ALBUMS_PER_PAGE : Int = 10
        val PHOTOS_PER_PAGE : Int = 10

        /**
         *  xlarge screens are at least 960dp x 720dp
         *  large screens are at least 640dp x 480dp
         *  normal screens are at least 470dp x 320dp
         *  small screens are at least 426dp x 320dp
         **/
        val SW_SMALL   = 420
        val SW_NORMAL  = 480
        val SW_LARGE   = 640
        val SW_XLARGE  = 960

        val MSG_UPDATE_UI        = 1
        val MSG_LOADING          = 2
        val MSG_LOADING_DONE     = 3
//        val MSG_LOAD_NEXT        = 4
//        val MSG_LOAD_NEXT_DONE   = 5
//        val MSG_LOAD_PREV        = 6
//        val MSG_LOAD_PREV_DONE   = 7

        val PUBLIC_PROFILE = "public_profile"
        val USER_PHOTOS = "user_photos"

        val ID : String = "id"
        val NAME : String = "name"
        val LINK : String = "link"
        val COVER : String = "cover"
        val SOURCE : String = "source"
        val PICTURE : String = "picture"
        val DATA : String = "data"
        val URL : String = "url"
        val ALBUMS : String = "albums"
        val COVER_PHOTO : String = "cover_photo"
        val PAGING : String = "paging"
        val PREVIOUS : String = "previous"
        val NEXT : String = "next"
        val PHOTOS : String = "photos"
        val WEB_IMAGES : String = "webp_images"

        val EXTRA_ALBUM_ID = "album_id"
        val EXTRA_PHOTO_ID = "photo_id"

        val FRAG_ALBUM_LIST : Int = 0
        val FRAG_ALBUM_PHOTOS : Int = 1

        val ZERO_F = 0.0f
        val SWIPE_THRESHOLD : Float = 300.0f
    }
}
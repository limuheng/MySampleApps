package com.muheng.photoviewer.utils

class Constants {

    companion object {

        // Extra key for intent fields
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PHOTO = "extra_photo"

        // Span count used for item counts of a row
        const val SPAN_COUNT_NORMAL: Int = 2
        const val SPAN_COUNT_LARGE: Int = 5
        const val SPAN_COUNT_XLARGE: Int = 8

        /**
         *  xlarge screens are at least 960dp x 720dp
         *  large screens are at least 640dp x 480dp
         *  normal screens are at least 470dp x 320dp
         *  small screens are at least 426dp x 320dp
         **/
        val SW_SMALL = 420
        val SW_NORMAL = 480
        val SW_LARGE = 640
        val SW_XLARGE = 960

        // FB User permissions
        const val PUBLIC_PROFILE = "public_profile"
        const val USER_PHOTOS = "user_photos"

        // FB Request field constants
        const val FIELDS = "fields"
        const val ID: String = "id"
        const val NAME: String = "name"
        const val SOURCE: String = "source"
        const val PICTURE: String = "picture"
        const val DATA: String = "pages"
        const val URL: String = "url"
        const val ALBUMS: String = "albums"
        const val COVER_PHOTO: String = "cover_photo"
        const val PAGING: String = "paging"
        const val PREVIOUS: String = "previous"
        const val NEXT: String = "loadMore"
        const val PHOTOS: String = "photos"
        const val WEB_IMAGES: String = "webp_images"
        const val IMAGES: String = "images"
    }

}
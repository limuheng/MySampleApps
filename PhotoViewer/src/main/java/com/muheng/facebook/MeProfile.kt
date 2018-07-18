package com.muheng.facebook

/**
 *  /me?fields=id, name, link, cover, picture
 *  {
 *      "id": "1432779547",
 *      "name": "Muheng Li",
 *      "picture": {
 *          "pages": {
 *              "height": 50,
 *              "is_silhouette": false,
 *              "url": "https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/25660200_10210653853009152_8016073926312315695_n.jpg?_nc_cat=0&oh=f5c845721c5607ef0d90e090a6107fb3&oe=5BD5DB50",
 *              "width": 50
 *          }
 *      }
 *  }
 */
data class MeProfile(
        val id: String = "",
        val name: String = "",
        val picture: Picture = Picture()
)

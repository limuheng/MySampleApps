package com.muheng.retrofit

class Parameter : Comparable<Parameter> {
    var key : String
    var value : String? = null

    constructor(key : String) {
        this.key = key
    }

    constructor(key : String, value : String) {
        this.key = key
        this.value = value
    }

    override fun compareTo(other: Parameter): Int {
        return key.compareTo(other.key)
    }
}
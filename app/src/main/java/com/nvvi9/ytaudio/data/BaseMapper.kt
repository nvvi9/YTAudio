package com.nvvi9.ytaudio.data

interface BaseMapper<in A, out B> {
    fun map(type: A): B?
}
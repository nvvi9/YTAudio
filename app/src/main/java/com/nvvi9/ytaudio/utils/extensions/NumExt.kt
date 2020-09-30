package com.nvvi9.ytaudio.utils.extensions


fun Float.fixPercentBounds() =
    if (this < 0F) 0F else if (this > 100F) 100F else this

fun Long.fixToStep(step: Long) =
    this / step * step


fun Long.fixToPercent(total: Long) =
    (this * 100) / total.toFloat()

fun Float.percentToMillis(total: Long) =
    (this * total / 100).toLong()
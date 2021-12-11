package com.drbrosdev.qrscannerfromlib.ui.gratitude

import kotlin.random.Random

fun generateGratitudeMessage(): String {
    return when(Random.nextInt(100)) {
        in 1..30 -> "You're the best."
        in 31..60 -> "Keep supporting open-source!"
        in 61..100 -> "beep boop"
        else -> "beep boop"
    }
}
package dev.androidbroadcast.quizapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SidebarData(
    var isAnswered: Boolean = false,
    var isActive: Boolean = false
) : Parcelable

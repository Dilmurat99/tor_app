package com.uyghar.torapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rendered(
    val rendered: String?
): Parcelable

@Parcelize
data class Post(
    val id: Int?,
    val title: Rendered?,
    val content: Rendered?
): Parcelable {
    override fun toString(): String {
        return title?.rendered ?: ""
    }
}
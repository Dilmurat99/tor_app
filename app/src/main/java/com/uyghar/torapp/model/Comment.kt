package com.uyghar.torapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Comment(
    val id: Int?,
    val author_name: String?,
    val date: String?,
    val content: Rendered?
)
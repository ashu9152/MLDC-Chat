package com.example.mldcchat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val name: String, val imageUrl: String, val id: String): Parcelable {
  constructor() : this("", "", "")
}
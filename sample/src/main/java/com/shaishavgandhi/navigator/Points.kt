package com.shaishavgandhi.navigator

import android.os.Parcel
import android.os.Parcelable

data class Points(val value: Int): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Points> {
        override fun createFromParcel(parcel: Parcel): Points {
            return Points(parcel)
        }

        override fun newArray(size: Int): Array<Points?> {
            return arrayOfNulls(size)
        }
    }
}
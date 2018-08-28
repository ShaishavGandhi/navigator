package com.shaishavgandhi.navigator.sample

import android.os.Parcel
import android.os.Parcelable

data class KTParcelable(
    val firstProperty: String,
    val secondProperty: Int
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(firstProperty)
        writeInt(secondProperty)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<KTParcelable> = object : Parcelable.Creator<KTParcelable> {
            override fun createFromParcel(source: Parcel): KTParcelable = KTParcelable(source)
            override fun newArray(size: Int): Array<KTParcelable?> = arrayOfNulls(size)
        }
    }
}

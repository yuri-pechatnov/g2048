package com.example.pechatnov.g2048

import android.os.Parcel
import android.os.Parcelable

class ParcelableMutableList<E> :  ArrayList<E>(), Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    @JvmField
    val CREATOR: Parcelable.Creator<ParcelableMutableList<E>> = object : Parcelable.Creator<ParcelableMutableList<E>> {
        override fun createFromParcel(input: Parcel): ParcelableMutableList<E> {
            assert(false)
            return ParcelableMutableList<E>()
        }
        override fun newArray(size: Int): Array<ParcelableMutableList<E>?> {
            return arrayOfNulls<ParcelableMutableList<E>>(size)
        }
    }
}
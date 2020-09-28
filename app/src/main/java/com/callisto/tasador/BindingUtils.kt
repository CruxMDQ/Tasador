package com.callisto.tasador

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.callisto.tasador.domain.Parcel

@BindingAdapter("dimensionOne")
fun TextView.setDimensionOne(item: Parcel)
{
    text = item.front.toString()
}

@BindingAdapter("dimensionTwo")
fun TextView.setDimensionTwo(item: Parcel)
{
    text = item.front.toString()
}
package com.callisto.tasador

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.callisto.tasador.domain.RealEstate

@BindingAdapter("parcelFront")
fun TextView.setFront(item: RealEstate)
{
    text = item.front.toString()
}

@BindingAdapter("parcelSide")
fun TextView.setSide(item: RealEstate)
{
    text = item.side.toString()
}

@BindingAdapter("estateAddress")
fun TextView.setEstateAddress(item: RealEstate)
{
    text = item.address
}

@BindingAdapter("estateFinalPrice")
fun TextView.setEstateFinalPrice(item: RealEstate)
{
    text = item.priceFinal.toString()
}

@BindingAdapter("estateId")
fun TextView.setEstateId(item: RealEstate)
{
    text = item.id.toString()
}
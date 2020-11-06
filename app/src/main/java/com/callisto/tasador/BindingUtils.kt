package com.callisto.tasador

import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.callisto.tasador.domain.RealEstate

/**
 * UNIVERSAL BINDINGS APPLYING TO ALL KINDS OF REAL ESTATE
 */

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

@BindingAdapter("estateQuotedPrice")
fun TextView.setEstateQuotedPrice(item: RealEstate)
{
    text = item.priceQuoted.toString()
}

@BindingAdapter("estateId")
fun TextView.setEstateId(item: RealEstate)
{
    text = item.id.toString()
}

/**
 * PARCEL BINDINGS
 */

@BindingAdapter("parcelArea")
fun TextView.setArea(item: RealEstate)
{
    text = item.area.toString()
}

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

@BindingAdapter("parcelCataster")
fun TextView.setCataster(item: RealEstate)
{
    text = item.cataster.toString()
}

@BindingAdapter("parcelZonification")
fun TextView.setZonification(item: RealEstate)
{
    text = item.zonification.toString()
}

@BindingAdapter("taxNumber")
fun TextView.setTaxNumber(item: RealEstate)
{
    text = item.tax_number.toString()
}

@BindingAdapter("roadType")
fun TextView.setRoadType(item: RealEstate)
{
    text = item.road_type.toString()
}

@BindingAdapter("hasPower")
fun CheckBox.setHasPower(item: RealEstate)
{
    val result = item.utility_power?.toBoolean() ?: false

    isChecked = result
}
package com.callisto.tasador.domain

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 *
 * IMPORTANT BIT: DATA CLASS SHOULD ***NOT*** BE EXTENDED!
 * https://stackoverflow.com/a/26467380/742145
 */

/**
 * 9/23/20: Resorted to using an interface instead of an abstract class to avoid issues when
 * building constructors.
 */
interface Property {
    var id: Int
    var address: String
    var owner: String
    var priceFinal: Long
    var priceQuoted: Long
}

data class Chamber(
    var id: Int?,
    var front: Float?,
    var side: Float?
)

data class House (
    override var id: Int = -1,
    override var address: String = "",
    override var owner: String = "",
    override var priceFinal: Long = 0,
    override var priceQuoted: Long = 0,
    var chambers: List<Chamber> = ArrayList(),
    var parcels: List<Parcel> = ArrayList()
) : Property

data class Parcel(
    override var id: Int = -1,
    override var address: String = "",
    override var owner: String = "",
    override var priceFinal: Long = 0,
    override var priceQuoted: Long = 0,
    var front: Float?,
    var side: Float?,
    var area: Float?,
    var district: String?,
    var section: String?,
    var block: String?,
    var plot: String?
) : Property

/**
 * Sample project has a shortDescription method.
 *
 * TODO to use this well I need to incorporate strings accepting parameters.
 */
//{
//
//    /**
//     * Short description is used for displaying truncated descriptions in the UI
//     */
//    val shortDescription: String
//        get() = description.smartTruncate(200)
//}

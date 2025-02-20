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
interface BaseProperty {
    var id: Int
    var type: String
    var address: String
    var owner: String
    var priceFinal: Long
    var priceQuoted: Long
}

data class Chamber(
    var id: Int?,
    var parent_id: Int?,
    var front: Float?,
    var side: Float?,
    var area: Float?
)

data class RealEstate(
    override var id: Int = -1,
    override var type: String = "",
    override var address: String = "",
    override var owner: String = "",
    override var priceFinal: Long = 0,
    override var priceQuoted: Long = 0,
    var parent_id: Int?,
    var front: Float?,
    var side: Float?,
    var area: Float?,
    var cataster: String?,
    var zonification: String?,
    var subunits: List<RealEstate>? = null,
    var chambers: List<Chamber>? = null
) : BaseProperty
{
    fun hasParent() : Boolean
    {
        if (parent_id == null)
        {
            return false
        }
        return true
    }
}

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

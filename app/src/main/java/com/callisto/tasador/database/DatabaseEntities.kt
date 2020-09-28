package com.callisto.tasador.database

import androidx.room.*
import com.callisto.tasador.domain.Parcel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//const val TABLE_PROPERTIES = "properties"
const val TABLE_PARCELS = "parcels"
const val TABLE_HOUSES = "houses"
const val TABLE_CHAMBERS = "chambers"

const val PREFIX_PARCEL = "parcel_"
const val PREFIX_HOUSE = "house_"

const val COL_ID = "id"
const val COL_PARCEL_ID = PREFIX_PARCEL + COL_ID
const val COL_HOUSE_ID = PREFIX_HOUSE + COL_ID
const val COL_CHAMBER_ID = "chamber_id"

const val COL_ADDRESS = "address"
const val COL_OWNER = "owner"
const val COL_PRICE_FINAL = "price_final"
const val COL_PRICE_QUOTED = "price_quoted"

const val COL_CHAMBER_FRONT = "front"
const val COL_CHAMBER_SIDE = "side"
const val COL_PARCEL_FRONT = "front"
const val COL_PARCEL_SIDE = "side"
const val COL_AREA = "area"

const val COL_DISTRICT = "district"
const val COL_SECTION = "section"
const val COL_BLOCK = "block"
const val COL_PLOT = "plot"

const val FK_COL_ID_HOUSE = "id_house"
/**
 * Master entity class. All property types must extend this.
 * 9/23/20: OR EMBED IT! DAMN IT!
 */
open class DatabaseProperty {
    @ColumnInfo(name = COL_ADDRESS)
    @SerializedName(COL_ADDRESS)
    @Expose
    var address: String? = null

    @ColumnInfo(name = COL_OWNER)
    @SerializedName(COL_OWNER)
    @Expose
    var owner: String? = null

    @ColumnInfo(name = COL_PRICE_FINAL)
    @SerializedName(COL_PRICE_FINAL)
    @Expose
    var priceFinal: Long? = null

    @ColumnInfo(name = COL_PRICE_QUOTED)
    @SerializedName(COL_PRICE_QUOTED)
    @Expose
    var priceQuoted: Long? = null

    /**
     * No args constructor for use in serialization
     */
    constructor()

    @Ignore
    constructor
    (
        street: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long
    ) : super() {
        this.address = street
        this.owner = owner
        this.priceFinal = priceFinal
        this.priceQuoted = priceQuoted
    }
}

/**
 * Class representing a plot of land.
 *
 * Houses and buildings may have multiple plots.
 *
 * No action on delete: parcels persist whether there is a house on them or not.
 */
@Entity
    (
    tableName = TABLE_PARCELS,
    indices =
    [
        Index(value = [COL_PARCEL_ID], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = DatabaseHouse::class,
            parentColumns = [COL_HOUSE_ID],
            childColumns = [FK_COL_ID_HOUSE],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseParcel
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_PARCEL_ID)
    var id: Int? = null,

    @Embedded(prefix = PREFIX_PARCEL)
    var property: DatabaseProperty? = null,

    @ColumnInfo(name = COL_PARCEL_FRONT)
    @SerializedName(COL_PARCEL_FRONT)
    @Expose
    var front: Float? = null,

    @ColumnInfo(name = COL_PARCEL_SIDE)
    @SerializedName(COL_PARCEL_SIDE)
    @Expose
    var side: Float? = null,

    @ColumnInfo(name = COL_AREA)
    @SerializedName(COL_AREA)
    @Expose
    var area: Float? = null,

    @ColumnInfo(name = COL_DISTRICT)
    @SerializedName(COL_DISTRICT)
    @Expose
    var district: String? = null,

    @ColumnInfo(name = COL_SECTION)
    @SerializedName(COL_SECTION)
    @Expose
    var section: String? = null,

    @ColumnInfo(name = COL_BLOCK)
    @SerializedName(COL_BLOCK)
    @Expose
    var block: String? = null,

    @ColumnInfo(name = COL_PLOT)
    @SerializedName(COL_PLOT)
    @Expose
    var plot: String? = null,

    @ColumnInfo(name = FK_COL_ID_HOUSE)
    @SerializedName(FK_COL_ID_HOUSE)
    @Expose
    val id_house: Int? = null
) {

    @Ignore
    constructor
    (
        id: Int,
        address: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long,
        front: Float,
        side: Float,
        area: Float,
        district: String,
        section: String,
        block: String,
        plot: String
    ) : this() {
        this.id = id
        this.property = DatabaseProperty(address, owner, priceFinal, priceQuoted)
        this.front = front
        this.side = side
        this.area = area
        this.district = district
        this.section = section
        this.block = block
        this.plot = plot
    }
}

fun List<DatabaseParcel>.asDomainModel(): List<Parcel> {
    return map { databaseParcel ->
        Parcel(
            id = databaseParcel.id!!,
            address = databaseParcel.property!!.address!!,
            owner = databaseParcel.property!!.owner!!,
            priceFinal = databaseParcel.property!!.priceFinal!!,
            priceQuoted = databaseParcel.property!!.priceQuoted!!,
            front = databaseParcel.front,
            side = databaseParcel.side,
            area = databaseParcel.area,
            district = databaseParcel.district,
            section = databaseParcel.section,
            block = databaseParcel.block,
            plot = databaseParcel.plot
        )
    }
}

@Entity
    (
    tableName = TABLE_HOUSES,
    indices =
    [
        Index(value = [COL_HOUSE_ID], unique = true)
    ]
)
data class DatabaseHouse
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_HOUSE_ID)
    var id: Int? = null,

    @Embedded(prefix = PREFIX_HOUSE)
    var property: DatabaseProperty? = null,

    @Ignore
    private var chambers: List<DatabaseChamber>? = null,

    @Ignore
    private var parcels: List<DatabaseParcel>? = null
)
{
    @Ignore
    constructor
    (
        id: Int,
        address: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long,
        chambers: List<DatabaseChamber>,
        parcels: List<DatabaseParcel>
    ) : this() {
        this.id = id
        this.property = DatabaseProperty(address, owner, priceFinal, priceQuoted)
        this.chambers = chambers
        this.parcels = parcels
    }
}

@Entity
    (
    tableName = TABLE_CHAMBERS,
    indices =
    [
        Index(value = [COL_CHAMBER_ID], unique = true)
    ]
)
data class DatabaseChamber
    (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_CHAMBER_ID)
    var id: Int? = null,

    @ColumnInfo(name = COL_CHAMBER_FRONT)
    @SerializedName(COL_CHAMBER_FRONT)
    @Expose
    var front: Float? = null,

    @ColumnInfo(name = COL_CHAMBER_SIDE)
    @SerializedName(COL_CHAMBER_SIDE)
    @Expose
    var side: Float? = null
)

///**
// * Class representing a plot of land.
// *
// * Houses and buildings may have multiple plots.
// *
// * No action on delete: parcels persist whether there is a house on them or not.
// */
//@Entity
//    (
//    tableName = TABLE_PARCELS,
//    indices =
//    [
//        Index(value = [COL_ID], unique = true)
//    ],
//    foreignKeys = [
//        ForeignKey(
//            entity = DatabaseHouse::class,
//            parentColumns = [COL_ID],
//            childColumns = [FK_COL_ID_HOUSE],
//            onDelete = ForeignKey.NO_ACTION
//        )
//    ]
//)
//data class DatabaseParcel
//(
//    @ColumnInfo(name = COL_D1)
//    @SerializedName(COL_D1)
//    @Expose
//    var d1: Float? = null,
//
//    @ColumnInfo(name = COL_D2)
//    @SerializedName(COL_D2)
//    @Expose
//    var d2: Float? = null,
//
//    @ColumnInfo(name = COL_AREA)
//    @SerializedName(COL_AREA)
//    @Expose
//    var area: Float? = null,
//
//    @ColumnInfo(name = COL_DISTRICT)
//    @SerializedName(COL_DISTRICT)
//    @Expose
//    var district: String? = null,
//
//    @ColumnInfo(name = COL_SECTION)
//    @SerializedName(COL_SECTION)
//    @Expose
//    var section: String? = null,
//
//    @ColumnInfo(name = COL_BLOCK)
//    @SerializedName(COL_BLOCK)
//    @Expose
//    var block: String? = null,
//
//    @ColumnInfo(name = COL_PLOT)
//    @SerializedName(COL_PLOT)
//    @Expose
//    var plot: String? = null,
//
//    @ColumnInfo(name = FK_COL_ID_HOUSE)
//    @SerializedName(FK_COL_ID_HOUSE)
//    @Expose
//    val id_house: Int? = null
//) : DatabaseProperty() {
//
//    @Ignore
//    constructor
//    (
//        id: Int,
//        address: String,
//        owner: String,
//        priceFinal: Long,
//        priceQuoted: Long,
//        d1: Float,
//        d2: Float,
//        area: Float,
//        district: String,
//        section: String,
//        block: String,
//        plot: String
//    )
//    : this(d1, d2, area, district, section, block, plot) {
//        this.id = id
//        this.address = address
//        this.owner = owner
//        this.priceFinal = priceFinal
//        this.priceQuoted = priceQuoted
//    }
//}
//
//fun List<DatabaseParcel>.asDomainModel(): List<Parcel> {
//    return map {
//        Parcel(
//            id = databaseParcel.id,
//            address = databaseParcel.address,
//            owner = databaseParcel.owner,
//            priceFinal = databaseParcel.priceFinal,
//            priceQuoted = databaseParcel.priceQuoted,
//            d1 = databaseParcel.d1,
//            d2 = databaseParcel.d2,
//            area = databaseParcel.area,
//            district = databaseParcel.district,
//            section = databaseParcel.section,
//            block = databaseParcel.block,
//            plot = databaseParcel.plot
//        )
//    }
//}

//@Entity
//    (
//    tableName = TABLE_HOUSES,
//    indices =
//    [
//        Index(value = [COL_ID], unique = true)
//    ]
//)
//data class DatabaseHouse
//(
//    @Ignore
//    private val chambers: List<DatabaseChamber>,
//
//    @Ignore
//    private val parcels: List<DatabaseParcel>
//) : DatabaseProperty()
//{
//
//    /**
//     * Constructor initializing empty array for chambers
//     */
////    @Ignore
//    constructor
//    (
//        id: Int,
//        address: String,
//        owner: String,
//        priceFinal: Long,
//        priceQuoted: Long,
//    ) : this(ArrayList(), ArrayList()) {
//        this.id = id
//        this.address = address
//        this.owner = owner
//        this.priceFinal = priceFinal
//        this.priceQuoted = priceQuoted
//    }
//
//    /**
//     * Constructor passing lists of chambers and parcels already populated
//     */
//    @Ignore
//    constructor
//    (
//        id: Int,
//        address: String,
//        owner: String,
//        priceFinal: Long,
//        priceQuoted: Long,
//        chambers: List<DatabaseChamber>,
//        parcels: List<DatabaseParcel>
//    ) : this(chambers, parcels) {
//        this.id = id
//        this.address = address
//        this.owner = owner
//        this.priceFinal = priceFinal
//        this.priceQuoted = priceQuoted
//    }
//}


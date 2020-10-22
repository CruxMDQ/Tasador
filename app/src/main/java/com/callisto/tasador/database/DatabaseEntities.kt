package com.callisto.tasador.database

import androidx.room.*
import com.callisto.tasador.domain.Chamber
import com.callisto.tasador.domain.Plot
import com.callisto.tasador.domain.RealEstate
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val TABLE_RE = "RealEstate"
const val TABLE_CHAMBERS = "chambers"

const val PARCEL = "parcel"
const val HOUSE = "house"
const val RE = "re"

const val SUFFIX_ID = "id"
const val COL_CHAMBER_ID = "chamber_id"

const val COL_RE_ID = RE + "_" + SUFFIX_ID
const val COL_PARENT_ID = "parent_id"

const val COL_TYPE = "type"
const val COL_ADDRESS = "address"
const val COL_OWNER = "owner"
const val COL_PRICE_FINAL = "price_final"
const val COL_PRICE_QUOTED = "price_quoted"

const val COL_CHAMBER_FRONT = "front"
const val COL_CHAMBER_SIDE = "side"
const val COL_PARCEL_FRONT = "front"
const val COL_PARCEL_SIDE = "side"
const val COL_AREA = "area"

const val COL_CATASTER = "cataster"
const val COL_ZONIFICATION = "zonification"

/**
 * Master entity class. All property types must extend this.
 * 9/23/20: OR EMBED IT! DAMN IT!
 */
open class DatabaseProperty
{
    @ColumnInfo(name = COL_TYPE)
    @SerializedName(COL_TYPE)
    @Expose
    var type: String? = null

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
        type: String,
        address: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long
    ) : super() {
        this.type = type
        this.address = address
        this.owner = owner
        this.priceFinal = priceFinal
        this.priceQuoted = priceQuoted
    }
}

@Entity
    (
    tableName = TABLE_CHAMBERS,
    indices =
    [
        Index(value = [COL_CHAMBER_ID], unique = true),
        Index(value = [COL_PARENT_ID], unique = false)
    ],
    foreignKeys =
    [
        ForeignKey
            (
            entity = DatabaseRealEstate::class,
            parentColumns = arrayOf(COL_RE_ID),
            childColumns = arrayOf(COL_PARENT_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseChamber
    (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_CHAMBER_ID)
    var id: Int? = null,

    @ColumnInfo(name = COL_PARENT_ID)
    var parent_id: Int? = null,

    @ColumnInfo(name = COL_CHAMBER_FRONT)
    @SerializedName(COL_CHAMBER_FRONT)
    @Expose
    var front: Float? = null,

    @ColumnInfo(name = COL_CHAMBER_SIDE)
    @SerializedName(COL_CHAMBER_SIDE)
    @Expose
    var side: Float? = null,

    @ColumnInfo(name = COL_AREA)
    @SerializedName(COL_AREA)
    @Expose
    var area: Float? = null
)

@Entity
(
    tableName = TABLE_RE,
    indices =
    [
        Index(value = [COL_RE_ID], unique = true)
    ],
    foreignKeys =
    [
        ForeignKey
        (
            entity = DatabaseRealEstate::class,
            parentColumns = arrayOf(COL_RE_ID),
            childColumns = arrayOf(COL_PARENT_ID),
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseRealEstate
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_RE_ID)
    var id: Int? = null,

    @ColumnInfo(name = COL_PARENT_ID)
    var parent_id: Int? = null,

    @Embedded(prefix = RE)
    var property: DatabaseProperty? = null,

    @ColumnInfo(name = COL_PARCEL_FRONT)        // Plot front
    @SerializedName(COL_PARCEL_FRONT)
    @Expose
    var front: Float? = null,

    @ColumnInfo(name = COL_PARCEL_SIDE)         // Plot side
    @SerializedName(COL_PARCEL_SIDE)
    @Expose
    var side: Float? = null,

    @ColumnInfo(name = COL_AREA)                // Plot area
    @SerializedName(COL_AREA)
    @Expose
    var area: Float? = null,

    @ColumnInfo(name = COL_CATASTER)
    @SerializedName(COL_CATASTER)
    @Expose
    var cataster: String? = null,

    @ColumnInfo(name = COL_ZONIFICATION)
    @SerializedName(COL_ZONIFICATION)
    @Expose
    var zonification: String? = null,
)

/**
 * 10/9/2020: Problem here: I need to learn how to handle recursive relations, if at all possible.
 */
//fun List<RealEstateWithChildren>.asREWCDomainModel(): List<RealEstate>
//{
//    val result = map { obj ->
////        val stub = if (obj.subunits != null) obj.subunits else null
//
////        val stub = obj.subunits ?: ArrayList()
//
//        val output = RealEstate(
//            id = obj.re!!.id!!,
//            address = obj.re!!.property!!.address!!,
//            owner = obj.re!!.property!!.owner!!,
//            priceFinal = obj.re!!.property!!.priceFinal!!,
//            priceQuoted = obj.re!!.property!!.priceQuoted!!,
//            parent_id = obj.re!!.parent_id,
//            front = obj.re!!.front,
//            side = obj.re!!.side,
//            area = obj.re!!.area,
//            cataster = obj.re!!.cataster,
//            zonification = obj.re!!.zonification
////            chambers = obj.chambers!!.asChamberDomainModel(),
////            subunits = obj.subunits!!.asREWCDomainModel()
////            subunits = stub.asREWCDomainModel()
//        )
//
//        output.chambers = obj.chambers!!.asChamberDomainModel()
//
//        if (obj.subunits != null && obj.subunits!!.isNotEmpty())
//        {
//            output.subunits = obj.subunits!!.asREWCDomainModel()
//        }
//        else
//        {
//            output.subunits = ArrayList()
//        }
//
//        output
//    }
//
//    return result
//}

/**
 * 10/9/2020: There was a problem here with the compiler complaining about being unable to find
 * 'chamber_id'. The solution was simple enough - the second relation should reference A BLOODY DAMN
 * DIFFERENT CLASS.
 */
data class RealEstateWithSubunits
(
    @Embedded
    val re: DatabaseRealEstate? = null,

    @Relation
    (
        parentColumn = COL_RE_ID,
        entityColumn = COL_PARENT_ID,
        entity = DatabaseRealEstate::class
    )
    var subunits: List<DatabaseRealEstate>? = null,

    @Relation
    (
        parentColumn = COL_RE_ID,
        entityColumn = COL_PARENT_ID,
        entity = DatabaseChamber::class
    )
    var chambers: List<DatabaseChamber>? = null
)

fun List<RealEstateWithSubunits>.asRESUBDomainModel() : List<RealEstate>
{
    return map { obj ->
        RealEstate(
            id = obj.re!!.id!!,
            type = obj.re.property!!.type!!,
            address = obj.re.property!!.address!!,
            owner = obj.re.property!!.owner!!,
            priceFinal = obj.re.property!!.priceFinal!!,
            priceQuoted = obj.re.property!!.priceQuoted!!,
            parent_id = obj.re.parent_id,
            front = obj.re.front,
            side = obj.re.side,
            area = obj.re.area,
            cataster = obj.re.cataster,
            zonification = obj.re.zonification,
            chambers = obj.chambers!!.asChamberDomainModel(),
            subunits = obj.subunits!!.asREDomainModel()
        )
    }
}

fun List<DatabaseChamber>.asChamberDomainModel(): List<Chamber>
{
    return map {
        Chamber(
            id = it.id,
            parent_id = it.parent_id,
            front = it.front,
            side = it.side,
            area = it.area
        )
    }
}

fun List<DatabaseRealEstate>.asREDomainModel(): List<RealEstate>
{
    return map { obj ->
        RealEstate(
            id = obj.id!!,
            type = obj.property!!.type!!,
            address = obj.property!!.address!!,
            owner = obj.property!!.owner!!,
            priceFinal = obj.property!!.priceFinal!!,
            priceQuoted = obj.property!!.priceQuoted!!,
            parent_id = obj.parent_id,
            front = obj.front,
            side = obj.side,
            area = obj.area,
            cataster = obj.cataster,
            zonification = obj.zonification,
            chambers = ArrayList(),
            subunits = ArrayList()
        )
    }
}


package com.callisto.tasador.database

import androidx.room.*
import com.callisto.tasador.*
import com.callisto.tasador.domain.Chamber
import com.callisto.tasador.domain.RealEstate
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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

    @ColumnInfo(name = COL_TAX_NUMBER)
    @SerializedName(COL_TAX_NUMBER)
    @Expose
    var tax_number: String? = null,

    @ColumnInfo(name = COL_UTILITY_WATER)
    @SerializedName(COL_UTILITY_WATER)
    @Expose
    var utility_water: String? = null,

    @ColumnInfo(name = COL_UTILITY_POWER)
    @SerializedName(COL_UTILITY_POWER)
    @Expose
    var utility_power: String? = null,

    @ColumnInfo(name = COL_UTILITY_SEWERS)
    @SerializedName(COL_UTILITY_SEWERS)
    @Expose
    var utility_sewers: String? = null,

    @ColumnInfo(name = COL_UTILITY_NATGAS)
    @SerializedName(COL_UTILITY_NATGAS)
    @Expose
    var utility_natgas: String? = null,

    @ColumnInfo(name = COL_UTILITY_DRAINS)
    @SerializedName(COL_UTILITY_DRAINS)
    @Expose
    var utility_drains: String? = null,

    @ColumnInfo(name = COL_UTILITY_INET)
    @SerializedName(COL_UTILITY_INET)
    @Expose
    var utility_internet: String? = null,

    @ColumnInfo(name = COL_ROAD_TYPE)
    @SerializedName(COL_ROAD_TYPE)
    @Expose
    var road_type: String? = null
)

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
            tax_number = obj.re.tax_number,
            utility_power = obj.re.utility_power,
            utility_water = obj.re.utility_water,
            utility_drains = obj.re.utility_drains,
            utility_natgas = obj.re.utility_natgas,
            utility_sewers = obj.re.utility_sewers,
            utility_internet = obj.re.utility_internet,
            road_type = obj.re.road_type,
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
            tax_number = obj.tax_number,
            utility_power = obj.utility_power,
            utility_water = obj.utility_water,
            utility_drains = obj.utility_drains,
            utility_natgas = obj.utility_natgas,
            utility_sewers = obj.utility_sewers,
            utility_internet = obj.utility_internet,
            road_type = obj.road_type,
            chambers = ArrayList(),
            subunits = ArrayList()
        )
    }
}


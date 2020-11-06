package com.callisto.tasador.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.callisto.tasador.TYPE_HOUSE
import com.callisto.tasador.TYPE_PARCEL
import com.callisto.tasador.UNINITIALIZED_ID
import com.callisto.tasador.database.*
import com.callisto.tasador.domain.RealEstate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    private val dao: RealtorDao

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(database: RealtorDatabase) {
        this.dao = database.RealtorDao

        this.properties = Transformations.map(database.RealtorDao.getRealEstateWithChildren())
        {
            it.asRESUBDomainModel()
        }
    }

    val properties: LiveData<List<RealEstate>>

//    fun findById(id: Int): Plot?
//    {
//        val parcelsContent = parcels.value!!
//
//        val result = parcelsContent.find {
//            it.id == id
//        }
//
//        return result
//    }

    /**
     * Upserts a plot of land on the database.
     *
     * @param estateId ID of the plot of land. Defaults to -1 if new.
     * @param parentId ID of the parent of this plot, if it has one. Defaults to -1 if it does not.
     * @param front Front of the parcel.
     * @param side Side of the parcel.
     * @param cataster
     * @param zonification
     */
    suspend fun addParcel
    (
        estateId: Int,
        parentId: Int,
        front: String,
        side: String,
        area: String,
        cataster: String,
        zonification: String,
        roadType: String,
        taxId: String,
        hasPower: Boolean,
        hasWater: Boolean,
        hasDrains: Boolean,
        hasNatgas: Boolean,
        hasSewers: Boolean,
        hasInternet: Boolean,
        address: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long
    )
    {
        withContext(Dispatchers.IO)
        {
            val realEstateId: Int = if (estateId == UNINITIALIZED_ID)
            {
                dao.getLastRealEstateId()
            }
            else
            {
                estateId
            }

            val realEstate = DatabaseRealEstate()

            realEstate.id = realEstateId

            if (parentId != UNINITIALIZED_ID)
            {
                realEstate.parent_id = parentId
            }

            realEstate.front = front.toFloat()
            realEstate.side = side.toFloat()
            realEstate.area = area.toFloat()
            realEstate.cataster = cataster
            realEstate.zonification = zonification
            realEstate.tax_number = taxId
            realEstate.road_type = roadType
            realEstate.utility_power = hasPower.toString()
            realEstate.utility_water = hasWater.toString()
            realEstate.utility_drains = hasDrains.toString()
            realEstate.utility_natgas = hasNatgas.toString()
            realEstate.utility_sewers = hasSewers.toString()
            realEstate.utility_internet = hasInternet.toString()

            realEstate.property = DatabaseProperty(
                TYPE_PARCEL,
                address,
                owner,
                priceFinal,
                priceQuoted
            )

            val result = dao.insertRealEstate(realEstate)

            Log.v("Add Real Estate", "Operation result: $result")
        }
    }

    suspend fun addHouse(
        estateId: Int,
        address: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long
    ) : Int
    {
        @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
        var realEstateId = UNINITIALIZED_ID

        withContext(Dispatchers.IO)
        {
            realEstateId = if (estateId == UNINITIALIZED_ID)
            {
                dao.getLastRealEstateId()
            }
            else
            {
                estateId
            }

            val house = DatabaseRealEstate()

            house.id = realEstateId

            house.property = DatabaseProperty(TYPE_HOUSE, address, owner, priceFinal, priceQuoted)

            val result = dao.insertRealEstate(house)

            Log.v("Add Real Estate", "Operation result: $result")

            realEstateId
        }

        return realEstateId
    }
}
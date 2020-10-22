package com.callisto.tasador.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.callisto.tasador.database.*
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.viewmodels.TYPE_HOUSE
import com.callisto.tasador.viewmodels.TYPE_PARCEL
import com.callisto.tasador.viewmodels.UNINITIALIZED_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    val dao: RealtorDao

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
     */
    suspend fun addParcel(estateId: Int, parentId: Int, front: String, side: String)
    {
        withContext(Dispatchers.IO)
        {
            var realEstateId = UNINITIALIZED_ID

            if (estateId == UNINITIALIZED_ID)
            {
                realEstateId = dao.getLastRealEstateId()
            }
            else
            {
                realEstateId = estateId
            }

            val realEstate = DatabaseRealEstate()

            val reFront = front.toFloat()
            val reSide = side.toFloat()

            realEstate.id = realEstateId

            if (parentId != UNINITIALIZED_ID)
            {
                realEstate.parent_id = parentId
            }

            realEstate.front = reFront
            realEstate.side = reSide
            realEstate.area = reFront * reSide

            realEstate.property = DatabaseProperty(TYPE_PARCEL, "", "", 0, 0)

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
        var realEstateId = UNINITIALIZED_ID

        withContext(Dispatchers.IO)
        {
            if (estateId == UNINITIALIZED_ID)
            {
                realEstateId = dao.getLastRealEstateId()
            }
            else
            {
                realEstateId = estateId
            }

            val house = DatabaseRealEstate()

            house.id = realEstateId
//            house.parent_id = UNINITIALIZED_ID

            house.property = DatabaseProperty(TYPE_HOUSE, address, owner, priceFinal, priceQuoted)

            val result = dao.insertRealEstate(house)

            Log.v("Add Real Estate", "Operation result: $result")

            realEstateId
        }

        return realEstateId
    }
}
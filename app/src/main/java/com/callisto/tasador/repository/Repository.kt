package com.callisto.tasador.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.callisto.tasador.database.*
import com.callisto.tasador.domain.Parcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    val dao: RealtorDao

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(database: RealtorDatabase) {
        this.dao = database.RealtorDao
        this.parcels = Transformations.map(database.RealtorDao.getParcels())
        {
            it.asDomainModel()
        }
    }

    val parcels: LiveData<List<Parcel>>

    fun findById(id: Int): Parcel?
    {
        val parcelsContent = parcels.value!!

        val result = parcelsContent.find {
            it.id == id
        }

        return result
    }

    /**
     * Function for updating existing parcels.
     *
     * @param plot The parcel to update.
     */
    suspend fun addParcel(plot: Parcel)
    {
        withContext(Dispatchers.IO)
        {
            val parcel = DatabaseParcel()

            parcel.id = plot.id
            parcel.front = plot.front
            parcel.side = plot.side

            parcel.property = DatabaseProperty(
                plot.address,
                plot.owner,
                plot.priceFinal,
                plot.priceQuoted
            )

            val result = dao.insertParcel(parcel)

            Log.v("Update Parcel", "Operation result: $result")
        }
    }

    /**
     * Upserts a plot of land on the database.
     *
     * @param d1 Dimension 1 of the parcel.
     * @param d2 Dimension 2 of the parcel.
     */
    suspend fun addParcel(d1: String, d2: String) {
        withContext(Dispatchers.IO)
        {
            val parcelId = dao.getLastParcelId()

            val parcel = DatabaseParcel()

            parcel.id = parcelId
            parcel.front = d1.toFloat()
            parcel.side = d2.toFloat()

            parcel.property = DatabaseProperty("", "", 0, 0)

            val result = dao.insertParcel(parcel)

            Log.v("Add Parcel", "Operation result: $result")
        }
    }
}
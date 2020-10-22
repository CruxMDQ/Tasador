package com.callisto.tasador.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

private const val VERSION = 4

@Dao
interface RealtorDao {
//    @Query("SELECT * FROM $TABLE_PARCELS WHERE $COL_PARCEL_ID = :key")
//    fun getParcelWithId(key: Int): LiveData<DatabasePlot>
//
//    @Query("SELECT * FROM $TABLE_PARCELS")
//    fun getParcels(): LiveData<List<DatabasePlot>>
//
//    @Query("SELECT COUNT(*) FROM $TABLE_PARCELS")
//    fun getLastParcelId(): Int
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(databasePlots: List<DatabasePlot>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertParcel(plot: DatabasePlot): Long

    @Query("SELECT * FROM $TABLE_RE")
    @Transaction
    fun getRealEstateWithChildren(): LiveData<List<RealEstateWithSubunits>>

    @Query("SELECT COUNT(*) FROM $TABLE_RE")
    fun getLastRealEstateId(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRealEstate(obj: DatabaseRealEstate)
}

@Database
(
    entities =
    [
        DatabaseChamber::class,
        DatabaseRealEstate::class
    ],
    version = VERSION
)
abstract class RealtorDatabase : RoomDatabase() {
    abstract val RealtorDao: RealtorDao
}

private lateinit var INSTANCE: RealtorDatabase

fun getDatabase(context: Context): RealtorDatabase {
    synchronized(RealtorDatabase::class.java)
    {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                //
                context.applicationContext,
                RealtorDatabase::class.java,
                "redb"
            )
                .build()
        }
        return INSTANCE
    }
}
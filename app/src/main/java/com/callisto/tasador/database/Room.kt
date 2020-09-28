package com.callisto.tasador.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.callisto.tasador.domain.Parcel

private const val VERSION = 1

@Dao
interface RealtorDao {
    @Query("SELECT * FROM $TABLE_PARCELS WHERE $COL_PARCEL_ID = :key")
    fun getParcelWithId(key: Int): LiveData<DatabaseParcel>

    @Query("SELECT * FROM $TABLE_PARCELS")
    fun getParcels(): LiveData<List<DatabaseParcel>>

    @Query("SELECT COUNT(*) FROM $TABLE_PARCELS")
    fun getLastParcelId(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(databaseParcels: List<DatabaseParcel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParcel(parcel: DatabaseParcel): Long
}

@Database
(
    entities =
    [
        DatabaseParcel::class,
        DatabaseHouse::class,
        DatabaseChamber::class
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
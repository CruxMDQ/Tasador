package com.callisto.tasador.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.callisto.tasador.*

private const val VERSION = 5

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
abstract class RealtorDatabase : RoomDatabase()
{
    abstract val RealtorDao: RealtorDao
}

private lateinit var INSTANCE: RealtorDatabase

fun getDatabase(context: Context): RealtorDatabase
{
    // Sample migration, works simply by adding new columns
    val MIGRATION_4_5 = object : Migration(4, 5)
    {
        override fun migrate(database: SupportSQLiteDatabase)
        {
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_TAX_NUMBER TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_UTILITY_POWER TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_UTILITY_WATER TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_UTILITY_DRAINS TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_UTILITY_NATGAS TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_UTILITY_SEWERS TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_UTILITY_INET TEXT")
            database.execSQL("ALTER TABLE $TABLE_RE ADD COLUMN $COL_ROAD_TYPE TEXT")
        }
    }

    synchronized(RealtorDatabase::class.java)
    {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                //
                context.applicationContext,
                RealtorDatabase::class.java,
                "redb"
            )
                .addMigrations(MIGRATION_4_5)
                .build()
        }
        return INSTANCE
    }
}
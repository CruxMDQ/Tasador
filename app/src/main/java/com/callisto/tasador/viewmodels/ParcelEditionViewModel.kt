package com.callisto.tasador.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.tasador.database.DatabaseParcel
import com.callisto.tasador.database.DatabaseProperty
import com.callisto.tasador.database.RealtorDao
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.domain.Parcel
import com.callisto.tasador.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

const val UNINITIALIZED_ID = -1

class ParcelEditionViewModel : BaseViewModel
{
    /**
     * Data source this [ViewModel] will use. Relies on repository pattern.
     *
     * *ALL* database operations *MUST* be handled through this layer.
     */
    val repository = Repository(getDatabase(getApplication()))

    val database: RealtorDao

    var parcelKey: Int = UNINITIALIZED_ID

    private var _activeParcel = MutableLiveData<Parcel>()

    val activeParcel: LiveData<Parcel>
        get() = _activeParcel

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(parcelKey: Int, dataSource: RealtorDao, application: Application) : super(application)
    {
        this.parcelKey = parcelKey

        this.database = dataSource

//        if (parcelKey != UNINITIALIZED_ID) {
//            _activeParcel.value = repository.findById(parcelKey)
//        }
    }

    /**
     * Factory for constructing [ParcelEditionViewModel] with parameters.
     */
    class Factory(val parcelKey: Int, val dataSource: RealtorDao, val app: Application) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T
        {
            if (modelClass.isAssignableFrom(ParcelEditionViewModel::class.java))
            {
                @Suppress("UNCHECKED_CAST")
                return ParcelEditionViewModel(parcelKey, dataSource, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

    /**
     * Takes in parameters from UI and passes it to the repository for insert/update.
     *
     * Never called explicitly, only via data binding.
     *
     * @param d1 Dimension 1 of the parcel.
     * @param d2 Dimension 2 of the parcel.
     */
    fun launchAddParcel(d1: String, d2: String)
    {
        Log.v("addParcel", "$d1, $d2")

        scope.launch {
            repository.addParcel(d1, d2)
        }
    }

    fun setActiveParcel(parcel: Parcel?) {
        _activeParcel.value = parcel
    }
}
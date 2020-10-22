package com.callisto.tasador.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.tasador.database.RealtorDao
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.repository.Repository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PlotEditionViewModel : BaseViewModel
{

    /**
     * Data source this [ViewModel] will use. Relies on repository pattern.
     *
     * *ALL* database operations *MUST* be handled through this layer.
     */
    val repository = Repository(getDatabase(getApplication()))

    val database: RealtorDao

    private var _activeParcel = MutableLiveData<RealEstate>()

    val activeParcel: LiveData<RealEstate>
        get() = _activeParcel

    private var estateId: Int = UNINITIALIZED_ID

    var parentId: Int = UNINITIALIZED_ID

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(parcelKey: Int, dataSource: RealtorDao, application: Application) : super(application)
    {
        this.estateId = parcelKey

        this.database = dataSource
    }

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(parcelKey: Int, parentId: Int, dataSource: RealtorDao, application: Application) :
            super(application)
    {
        this.estateId = parcelKey

        this.parentId = parentId

        this.database = dataSource
    }

    /**
     * Takes in parameters from UI and passes it to the repository for insert/update.
     *
     * Never called explicitly, only via data binding.
     *
     * @param front Front of the parcel.
     * @param side Side of the parcel.
     */
    fun launchAddParcel(front: String, side: String)
    {
        scope.launch {
            repository.addParcel(estateId, parentId, front, side)
        }
    }

    fun setActiveParcel(parcel: RealEstate?) {
        _activeParcel.value = parcel
    }

    /**
     * Factory for constructing [PlotEditionViewModel] with parameters.
     */
    class Factory
    (
        private val parcelKey: Int,
        private val dataSource: RealtorDao,
        val app: Application
    ) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>) : T
        {
            if (modelClass.isAssignableFrom(PlotEditionViewModel::class.java))
            {
                @Suppress("UNCHECKED_CAST")
                return PlotEditionViewModel(parcelKey, dataSource, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

    class FactoryWithParent
    (
        private val parcelKey: Int,
        private val parentId: Int,
        private val dataSource: RealtorDao,
        val app: Application
    )
        : ViewModelProvider.Factory
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>) : T
        {
            if (modelClass.isAssignableFrom(PlotEditionViewModel::class.java))
            {
                @Suppress("UNCHECKED_CAST")
                return PlotEditionViewModel(parcelKey, parentId, dataSource, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}
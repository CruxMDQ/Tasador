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

class HouseEditionViewModel : BaseViewModel
{
    val repository = Repository(getDatabase(getApplication()))

    val database: RealtorDao

    var estateId: Int = UNINITIALIZED_ID

    private var _activeEstate = MutableLiveData<RealEstate>()
    val activeEstate: LiveData<RealEstate>
        get() = _activeEstate

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(estateKey: Int, dataSource: RealtorDao, application: Application) : super(application)
    {
        this.estateId = estateKey

        this.database = dataSource
    }

    class Factory
    (
        private val estateKey: Int,
        private val dataSource: RealtorDao,
        val app: Application
    ) : ViewModelProvider.Factory
    {
        override fun <T: ViewModel?> create(modelClass: Class<T>) : T
        {
            if (modelClass.isAssignableFrom(HouseEditionViewModel::class.java))
            {
                @Suppress("UNCHECKED_CAST")
                return HouseEditionViewModel(estateKey, dataSource, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

    fun launchAddEstate(address: String, owner: String, priceQuoted: Long, priceFinal: Long)
    {
        scope.launch {
        //
            val result = repository.addHouse(estateId, address, owner, priceFinal, priceQuoted)

            // This value SHOULD be used by the Plot Edition screen to set the plot's parent
            _navigateToNewPlot.value = result
        }
    }

    fun setActiveEstate(estate: RealEstate?)
    {
        _activeEstate.value = estate
    }

    private val _navigateToNewPlot = MutableLiveData<Int>()
    val navigateToNewPlot: LiveData<Int>
        get() = _navigateToNewPlot

    fun onFabClicked(address: String, owner: String, priceQuoted: Long, priceFinal: Long)
    {
        launchAddEstate(address, owner, priceQuoted, priceFinal)
    }

    fun onNavigatedToNewPlot()
    {
        _navigateToNewPlot.value = null
    }

    private val _navigateToPlotDetails = MutableLiveData<Int>()
    val navigateToPlotDetails: LiveData<Int>
        get() = _navigateToPlotDetails

    fun onItemClicked(plotId: Int)
    {
        _navigateToPlotDetails.value = plotId
    }
}

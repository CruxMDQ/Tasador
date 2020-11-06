package com.callisto.tasador.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.repository.Repository
import java.lang.IllegalArgumentException

@Suppress("ConvertSecondaryConstructorToPrimary")
class PropertyListViewModel : BaseViewModel
{
    constructor(application: Application) : super(application)

    /**
     * Data source this [ViewModel] will fetch results from. Relies on repository pattern.
     */
    val repository = Repository(getDatabase(getApplication()))

    /**
     * Navigation-related flag and functions for creating a new property.
     */
    private val _navigateToNewEstate = MutableLiveData<String>()
    val navigateToNewEstate: LiveData<String>
        get() = _navigateToNewEstate

    fun onFabClicked()
    {
        setIsSelectingType(true)
    }

    fun onNavigatedToNewEstate()
    {
        _navigateToNewEstate.value = null
    }

    /**
     * Navigation-related flag and functions for editing a property's details.
     */
    private val _navigateToEstateDetails = MutableLiveData<RealEstate>()
    val navigateToEstateDetails: LiveData<RealEstate>
        get() = _navigateToEstateDetails

    fun onNavigatedToEstateDetails()
    {
        _navigateToEstateDetails.value = null
    }

    fun onItemClicked(realEstate: RealEstate)
    {
        _navigateToEstateDetails.value = realEstate
    }

    private var _isSelectingType = MutableLiveData<Boolean>()
    val isSelectingType: LiveData<Boolean>
        get() = _isSelectingType

    fun setIsSelectingType(isSelecting: Boolean)
    {
        _isSelectingType.value = isSelecting
    }

    fun onEstateTypePicked(estateType: String?)
    {
        _navigateToNewEstate.value = estateType
    }

    /**
     * Factory for constructing [PropertyListViewModel] with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T
        {
            if (modelClass.isAssignableFrom(PropertyListViewModel::class.java))
            {
                @Suppress("UNCHECKED_CAST")
                return PropertyListViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}
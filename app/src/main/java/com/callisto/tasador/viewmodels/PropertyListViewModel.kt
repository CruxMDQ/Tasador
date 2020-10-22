package com.callisto.tasador.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.tasador.database.getDatabase
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
    private val _navigateToNewProperty = MutableLiveData<Boolean>()
    val navigateToNewProperty: LiveData<Boolean>
        get() = _navigateToNewProperty

    fun onFabClicked()
    {
        setIsSelectingType(true)
        _navigateToNewProperty.value = true
    }

    fun onNavigatedToNewProperty()
    {
        _navigateToNewProperty.value = null
    }

    /**
     * Navigation-related flag and functions for editing a property's details.
     */
    private val _navigateToPropertyDetails = MutableLiveData<Int>()
    val navigateToPropertyDetails: LiveData<Int>
        get() = _navigateToPropertyDetails

    fun onNavigatedToPropertyDetails()
    {
        _navigateToPropertyDetails.value = null
    }

    fun onItemClicked(id: Int)
    {
        _navigateToPropertyDetails.value = id
    }

    private var _isSelectingType = MutableLiveData<Boolean>()
    val isSelectingType: LiveData<Boolean>
        get() = _isSelectingType

    fun setIsSelectingType(isSelecting: Boolean)
    {
        _isSelectingType.value = isSelecting
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
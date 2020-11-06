package com.callisto.tasador.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callisto.tasador.CODE_EXTERNAL_STORAGE
import com.callisto.tasador.UNINITIALIZED_ID
import com.callisto.tasador.database.RealtorDao
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.repository.Repository
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

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

    private var _isSavingData = MutableLiveData<Boolean>()
    val isSavingData: LiveData<Boolean>
        get() = _isSavingData

    private var _isExportingData = MutableLiveData<Boolean>()
    val isExportingData: LiveData<Boolean>
        get() =_isExportingData

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

    fun saveData()
    {
        _isSavingData.value = true
    }

    /**
     * Takes in parameters from UI and passes it to the repository for insert/update.
     *
     * Never called explicitly, only via live data.
     */
    fun launchAddParcel(
        front: String,
        side: String,
        area: String,
        cataster: String,
        zonification: String,
        roadType: String,
        taxId: String,
        hasPower: Boolean,
        hasWater: Boolean,
        hasDrains: Boolean,
        hasNatgas: Boolean,
        hasSewers: Boolean,
        hasInternet: Boolean,
        address: String,
        owner: String,
        priceFinal: Long,
        priceQuoted: Long
    )
    {
        scope.launch {
            repository.addParcel(
                estateId,
                parentId,
                front,
                side,
                area,
                cataster,
                zonification,
                roadType,
                taxId,
                hasPower,
                hasWater,
                hasDrains,
                hasNatgas,
                hasSewers,
                hasInternet,
                address,
                owner,
                priceFinal,
                priceQuoted
            )
        }

        _isSavingData.value = false

        _isExportingData.value = true
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
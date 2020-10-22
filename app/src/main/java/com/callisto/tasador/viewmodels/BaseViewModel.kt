package com.callisto.tasador.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

const val UNINITIALIZED_ID = -1

const val TYPE_HOUSE = "House"
const val TYPE_PARCEL = "Parcel"

@Suppress("JoinDeclarationAndAssignment")
abstract class BaseViewModel : AndroidViewModel
{
    /**
     * Job for all coroutines started by this model.
     *
     * Cancel this -> kill all coroutines started by models extending this.
     */
    private var job: Job

    /**
     * Main scope for all coroutines launched by models extending this.
     *
     * Call viewModelJob.cancel() to cancel all coroutines launched by this scope.
     */
    protected val scope: CoroutineScope

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(application: Application) : super(application)
    {
        this.job = SupervisorJob()
        this.scope = CoroutineScope(job + Dispatchers.Main)
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared()
    {
        super.onCleared()
        job.cancel()
    }
}
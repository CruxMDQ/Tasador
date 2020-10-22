package com.callisto.tasador.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.callisto.tasador.R
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.databinding.FragmentPlotCreationBinding
import com.callisto.tasador.viewmodels.PlotEditionViewModel
import com.callisto.tasador.viewmodels.UNINITIALIZED_ID

class PlotEditionFragment : Fragment()
{
    private var parcelId: Int = UNINITIALIZED_ID

    private var parentId: Int = UNINITIALIZED_ID

    /** To lazily initialize this, it must not be referenced before onActivityCreated.
     */
    private val viewModel: PlotEditionViewModel by lazy {
    //
        val activity = requireNotNull(this.activity)
        {
            "You can only access the ViewModel after onActivityCreated"
        }

        if (parentId == UNINITIALIZED_ID)
        {
            PlotEditionViewModel.Factory(
                parcelId,
                getDatabase(activity.applicationContext).RealtorDao,
                activity.application)
                .create(
                    //
                    PlotEditionViewModel::class.java
                )
        }
        else
        {
            PlotEditionViewModel.FactoryWithParent(
                parcelId,
                parentId,
                getDatabase(activity.applicationContext).RealtorDao,
                activity.application)
                .create(
                    //
                    PlotEditionViewModel::class.java
                )
        }
    }

    /** Instantiates user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI. (That's what binding.root
     * actually is.)
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPlotCreationBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_plot_creation,
            container,
            false
        )

        val arguments = arguments?.let { PlotEditionFragmentArgs.fromBundle(it) }

        parcelId = arguments!!.propertyId

        parentId = arguments.parentId

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModel.repository.properties.observe(viewLifecycleOwner, {
        //
            viewModel.setActiveParcel(it.find { item -> item.id == parcelId })
        })

        viewModel.parentId = parentId

        return binding.root
    }
}
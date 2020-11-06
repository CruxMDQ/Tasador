package com.callisto.tasador.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callisto.tasador.R
import com.callisto.tasador.TYPE_PARCEL
import com.callisto.tasador.UNINITIALIZED_ID
import com.callisto.tasador.adapters.OnEstateClickedListener
import com.callisto.tasador.adapters.RealEstateAdapter
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.databinding.FragmentHouseCreationBinding
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.viewmodels.HouseEditionViewModel

class HouseEditionFragment : Fragment()
{
    private var houseId: Int = -2

    private val viewModel: HouseEditionViewModel by lazy {
    //
        val activity = requireNotNull(this.activity)
        {
            "You can only access the ViewModel after onActivityCreated"
        }

        HouseEditionViewModel.Factory(
            houseId,
            getDatabase(activity.applicationContext).RealtorDao,
            activity.application)
        .create(
        //
            HouseEditionViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHouseCreationBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_house_creation,
            container,
            false
        )

        val arguments = arguments?.let { HouseEditionFragmentArgs.fromBundle(it)}

        houseId = arguments!!.propertyId

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        val plotsAdapter = RealEstateAdapter(OnEstateClickedListener {
                parcelId -> viewModel.onItemClicked(parcelId)
        })

        setUpPlotRecyclerViewAdapter(plotsAdapter, binding)

        setUpRepositoryObserver(plotsAdapter)

        setUpNavigation()

        return binding.root
    }

    private fun setUpNavigation()
    {
        viewModel.navigateToNewPlot.observe(viewLifecycleOwner, {
        //
            it?.let {
            //
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.houseEditionFragment)
                {
                    navController.navigate(
                        HouseEditionFragmentDirections.actionHouseEditionFragmentToParcelCreationFragment
                        (
                            UNINITIALIZED_ID,
                            it
                        )
                    )
                }

                viewModel.onNavigatedToNewPlot()
            }
        })

        viewModel.navigateToPlotDetails.observe(viewLifecycleOwner, {
        //
            it.let {
            //
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.houseEditionFragment)
                {
                    navController.navigate(
                        HouseEditionFragmentDirections
                            .actionHouseEditionFragmentToParcelCreationFragment(it)
                    )
                }
            }
        })
    }

    private fun setUpRepositoryObserver(plotsAdapter: RealEstateAdapter)
    {
        /**
         * 10/17/20: I need to filter the data before passing it to the adapter.
         */
        viewModel.repository.properties.observe(viewLifecycleOwner, {
        //
            viewModel.setActiveEstate(it.find { item -> item.id == houseId })

            it?.let {
            //
                plotsAdapter.submitList(filterPlotList(it))
            }
        })
    }

    /**
     * Filters source data. Resulting list must only contain real estate objects of Parcel type
     * whose parent ID matches that of the active house.
     */
    private fun filterPlotList(it: List<RealEstate>): List<RealEstate>
    {
        return it.filter { realEstate ->
        //
            realEstate.parent_id == houseId &&
            realEstate.type == TYPE_PARCEL
        }
    }

    private fun setUpPlotRecyclerViewAdapter(adapter: RealEstateAdapter, binding: FragmentHouseCreationBinding)
    {
        val layoutManager = LinearLayoutManager(context)

        layoutManager.orientation = RecyclerView.VERTICAL

        binding.rvParcels.layoutManager = layoutManager

        binding.rvParcels.adapter = adapter
    }
}
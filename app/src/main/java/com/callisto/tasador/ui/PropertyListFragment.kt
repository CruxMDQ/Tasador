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
import com.callisto.tasador.adapters.OnRealEstateClickListener
import com.callisto.tasador.adapters.RealEstateAdapter
import com.callisto.tasador.databinding.FragmentPropertyListBinding
import com.callisto.tasador.domain.BaseProperty
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.viewmodels.PropertyListViewModel

class PropertyListFragment : Fragment()
{
    /**
     * To lazily initialize this, it must not be referenced before onActivityCreated.
     */
    private val viewModel: PropertyListViewModel by lazy {
        //
        val activity = requireNotNull(this.activity)
        {
            "You can only access the ViewModel after onActivityCreated"
        }

        PropertyListViewModel.Factory(activity.application).create(
        //
            PropertyListViewModel::class.java
        )
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
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val binding: FragmentPropertyListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_property_list,
            container,
            false
        )

        /** Sets lifecycle owner so DataBinding can observe LiveData.
         *
         * - The method just would not show up, so went on and assigned the lifecycle owner by hand
         * - Eventually the method DID show up, after trying to run it and getting an
         * IllegalStateException (Called getViewLifecycleOwner() but onCreateView() returned null)
         * - ISE kept appearing, had to replace vanilla return of parent class method with a line to
         * return binding.root (d'oh) -__-
         */
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        val estateAdapter = RealEstateAdapter(OnRealEstateClickListener {
            realEstateId -> viewModel.onItemClicked(realEstateId)
        })

        setUpEstateRecyclerViewAdapter(binding, estateAdapter)

        setUpRepositoryObserver(estateAdapter)

        setUpNavigation()

        viewModel.isSelectingType.observe(viewLifecycleOwner, {
            if (it)
            {
                // TODO Code picker in alert dialog

                viewModel.setIsSelectingType(false)
            }
        })

        return binding.root
    }

    private fun setUpEstateRecyclerViewAdapter(
        binding: FragmentPropertyListBinding,
        estateAdapter: RealEstateAdapter
    ) {
        val layoutManager = LinearLayoutManager(context)

        layoutManager.orientation = RecyclerView.VERTICAL

        binding.rvProperties.layoutManager = layoutManager

        binding.rvProperties.adapter = estateAdapter
    }

    private fun setUpNavigation() {
        viewModel.navigateToNewProperty.observe(viewLifecycleOwner, {
            it?.let {
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.propertyList) {
                    navController.navigate(PropertyListFragmentDirections.actionPropertyListToHouseEditionFragment())
                }

                viewModel.onNavigatedToNewProperty()
            }
        })

        viewModel.navigateToPropertyDetails.observe(viewLifecycleOwner, {
            it.let {
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.propertyList) {
                    navController.navigate(
                        PropertyListFragmentDirections.actionPropertyListToHouseEditionFragment(
                            it
                        )
                    )
                }
            }
        })
    }

    private fun setUpRepositoryObserver(estateAdapter: RealEstateAdapter)
    {
        viewModel.repository.properties.observe(viewLifecycleOwner, {
            it?.let {
                estateAdapter.submitList(filterSourceData(it))
            }
        })
    }

    /**
     * Filters source data. Resulting list must only contain real estate objects with no parent.
     */
    private fun filterSourceData(it: List<RealEstate>) =
        it.filter { realEstate -> !realEstate.hasParent() }
}

/** Click listener for Properties. Naming the block helps understanding what it actually does.
 */
class PropertyListItemClick(val block: (BaseProperty) -> Unit)
{
    /** Called when a property is clicked
     *
     * @param property the property that was clicked
     */
    fun onClick(property: BaseProperty) = block(property)
}
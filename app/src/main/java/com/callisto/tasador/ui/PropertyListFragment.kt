package com.callisto.tasador.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callisto.tasador.R
import com.callisto.tasador.TYPE_HOUSE
import com.callisto.tasador.TYPE_PARCEL
import com.callisto.tasador.adapters.OnEstateClickedListener
import com.callisto.tasador.adapters.RealEstateAdapter
import com.callisto.tasador.adapters.SpinnerArrayAdapter
import com.callisto.tasador.databinding.FragmentPropertyListBinding
import com.callisto.tasador.domain.BaseProperty
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.viewmodels.PropertyListViewModel

class PropertyListFragment : Fragment()
{
    private lateinit var estateSpinnerAdapter: ArrayAdapter<String>

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
    override fun onCreateView
    (
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

        val estateAdapter = RealEstateAdapter(OnEstateClickedListener {
            realEstateId -> viewModel.onItemClicked(realEstateId)
        })

        setUpEstateRecyclerViewAdapter(binding, estateAdapter)

        setUpRepositoryObserver(estateAdapter)

        setUpNavigation()

        viewModel.isSelectingType.observe(viewLifecycleOwner, {
            if (it)
            {
                estateSpinnerAdapter = SpinnerArrayAdapter(
                    this.requireContext(),
                    arrayListOf(TYPE_HOUSE, TYPE_PARCEL)
                )

                showEstateTypeSelectionDialog()

//                viewModel.setIsSelectingType(false)
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

    private fun setUpNavigation()
    {
        viewModel.navigateToNewEstate.observe(viewLifecycleOwner, {
            it?.let {
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.propertyList)
                {
                    if (it == TYPE_HOUSE)
                    {
                        navController.navigate(PropertyListFragmentDirections.actionPropertyListToHouseEditionFragment())

                        viewModel.onNavigatedToNewEstate()
                    }

                    if (it == TYPE_PARCEL)
                    {
                        navController.navigate(PropertyListFragmentDirections.actionPropertyListToParcelCreationFragment())

                        viewModel.onNavigatedToNewEstate()
                    }
                }
            }
        })

        // DONE Fix editing navigation so that it can send this to the right property edition screen
        viewModel.navigateToEstateDetails.observe(viewLifecycleOwner, {
            it.let {
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.propertyList)
                {
                    if (it.type == TYPE_HOUSE)
                    {
                        navController.navigate(PropertyListFragmentDirections.actionPropertyListToHouseEditionFragment(it.id))

                        viewModel.onNavigatedToEstateDetails()
                    }

                    if (it.type == TYPE_PARCEL)
                    {
                        navController.navigate(PropertyListFragmentDirections.actionPropertyListToParcelCreationFragment(it.id))

                        viewModel.onNavigatedToEstateDetails()
                    }
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

    // https://inducesmile.com/kotlin-source-code/how-to-display-a-spinner-in-alert-dialog-in-kotlin/"
    private fun showEstateTypeSelectionDialog()
    {
        val spinner = Spinner(requireContext())

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        spinner.adapter = estateSpinnerAdapter

        alertDialogBuilder.setTitle(getText(R.string.msg_prop_type))
        alertDialogBuilder.setMessage(getText(R.string.msg_prop_type_hint))

        alertDialogBuilder.setView(spinner)

        val alertDialog = alertDialogBuilder.create()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            )
            {
                // FIXED Find out how to stop this from firing when the adapter is set
                // Source: https://stackoverflow.com/a/37561529
                val estateType: String? = estateSpinnerAdapter.getItem(position)

                if (position != 0)
                {
                    viewModel.onEstateTypePicked(estateType)

                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()

        // TODO Decide whether this goes here or inside the ViewModel class proper
        viewModel.setIsSelectingType(false)

        spinner.setSelection(0, false)
    }

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
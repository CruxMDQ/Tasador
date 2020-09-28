package com.callisto.tasador.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callisto.tasador.R
import com.callisto.tasador.adapters.OnParcelClickListener
import com.callisto.tasador.adapters.ParcelAdapter
import com.callisto.tasador.databinding.FragmentPropertyListBinding
import com.callisto.tasador.databinding.ItemParcelBinding
import com.callisto.tasador.domain.Parcel
import com.callisto.tasador.domain.Property
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

//        ViewModelProvider(this).get(PropertyListViewModel::class.java)
        PropertyListViewModel.Factory(activity.application).create(
        //
            PropertyListViewModel::class.java
        )
    }

    /**
     * Instantiates user interface view.
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

        /**
         * Sets lifecycle owner so DataBinding can observe LiveData.
         *
         * - The method just would not show up, so went on and assigned the lifecycle owner by hand
         * - Eventually the method DID show up, after trying to run it and getting an
         * IllegalStateException (Called getViewLifecycleOwner() but onCreateView() returned null)
         * - ISE kept appearing, had to replace vanilla return of parent class method with a line to
         * return binding.root (d'oh) -__-
         */
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        val adapter = ParcelAdapter(OnParcelClickListener {
            parcelId -> viewModel.onItemClicked(parcelId)
        })

        val layoutManager = LinearLayoutManager(context)

        layoutManager.orientation = RecyclerView.VERTICAL

        binding.rvProperties.layoutManager = layoutManager

        binding.rvProperties.adapter = adapter

        viewModel.repository.parcels.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToNewProperty.observe(viewLifecycleOwner, {
            it?.let {
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.propertyList)
                {
                    navController.navigate(PropertyListFragmentDirections.actionPropertyListToParcelCreationFragment())
                }

                viewModel.onNavigatedToNewProperty()
            }
        })

        viewModel.navigateToPropertyDetails.observe(viewLifecycleOwner, {
            it.let {
                val navController = this.findNavController()

                if (navController.currentDestination?.id == R.id.propertyList)
                {
                    navController.navigate(PropertyListFragmentDirections.actionPropertyListToParcelCreationFragment(it))
                }
            }
        })

        return binding.root
    }
}

/**
 * Click listener for Properties. Naming the block helps understanding what it actually does.
 */
class PropertyListItemClick(val block: (Property) -> Unit)
{
    /**
     * Called when a property is clicked
     *
     * @param property the property that was clicked
     */
    fun onClick(property: Property) = block(property)
}

/**
 * Adapter for RecyclerViews supporting multiple view types.
 * Sources:
 * https://stackoverflow.com/a/56826922/742145
 * https://blog.mindorks.com/recyclerview-multiple-view-types-in-android
 *
 * 9/17/20: those solutions require using inner classes on the Adapter class for
 * ViewHolders, an approach so far NOT used here.
 */
class PropertyListAdapter(
    val callback: PropertyListItemClick,
    private val context: Context,
    list: List<Property>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    companion object
    {
        const val VIEW_TYPE_PARCEL = 1
        const val VIEW_TYPE_HOUSE = 2
    }

    private val data: List<Property> = list

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]

        return when (item)
        {
            is Parcel -> VIEW_TYPE_PARCEL
            else -> VIEW_TYPE_HOUSE
        }
//        /**
//         * Had to go and check out how to do instanceOf here.
//         * Source: https://stackoverflow.com/a/44098842/742145
//         */
//        if (item is Parcel)
//        {
//            return VIEW_TYPE_PARCEL
//        }
//        return VIEW_TYPE_HOUSE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /**
         * Uncomment this, expand and introduce more cases as new property types are added.
         */
//        if (viewType == VIEW_TYPE_PARCEL)
//        {
            val withDataBinding: ItemParcelBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                ParcelViewHolder.LAYOUT,
                parent,
                false
            )
            return ParcelViewHolder(withDataBinding)
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
//        (holder as ParcelViewHolder).bind(position)
    }
}

/**
 * ViewHolder for Parcel items. All work done by data binding.
 */
class ParcelViewHolder(viewDataBinding: ItemParcelBinding)
    : RecyclerView.ViewHolder(viewDataBinding.root)
{
    companion object
    {
        @LayoutRes
        val LAYOUT = R.layout.item_parcel
    }
}
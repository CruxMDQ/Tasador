package com.callisto.tasador.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.callisto.tasador.R
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.databinding.FragmentParcelCreationBinding
import com.callisto.tasador.viewmodels.ParcelEditionViewModel

class ParcelEditionFragment : Fragment() {
    private var parcelId: Int = -2

    /**
     * To lazily initialize this, it must not be referenced before onActivityCreated.
     */
    private val viewModel: ParcelEditionViewModel by lazy {
        //
        val activity = requireNotNull(this.activity)
        {
            "You can only access the ViewModel after onActivityCreated"
        }

        ParcelEditionViewModel.Factory(
            parcelId,
            getDatabase(activity.applicationContext).RealtorDao,
            activity.application)
        .create(
            //
            ParcelEditionViewModel::class.java
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
     * @return Return the View for the fragment's UI. (That's what binding.root
     * actually is.)
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentParcelCreationBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_parcel_creation,
            container,
            false
        )

        val arguments = arguments?.let { ParcelEditionFragmentArgs.fromBundle(it) }

        parcelId = arguments!!.propertyId

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModel.repository.parcels.observe(viewLifecycleOwner, {
        //
            viewModel.setActiveParcel(it.find { item -> item.id == parcelId })
        })

        return binding.root
    }
}
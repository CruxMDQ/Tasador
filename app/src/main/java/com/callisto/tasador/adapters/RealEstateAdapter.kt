package com.callisto.tasador.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.callisto.tasador.databinding.ItemHouseBinding
import com.callisto.tasador.databinding.ItemParcelBinding
import com.callisto.tasador.domain.RealEstate
import com.callisto.tasador.viewmodels.TYPE_PARCEL

const val VIEW_TYPE_PARCEL = 1
const val VIEW_TYPE_HOUSE = 2

/** Adapter for RecyclerViews supporting multiple view types.
 * Sources:
 * https://stackoverflow.com/a/56826922/742145
 * https://blog.mindorks.com/recyclerview-multiple-view-types-in-android
 *
 * 9/17/20: those solutions require using inner classes on the Adapter class for
 * ViewHolders, an approach so far NOT used here.
 */
class RealEstateAdapter (val clickListener: OnRealEstateClickListener) :
    ListAdapter<RealEstate, RecyclerView.ViewHolder>(RealEstateDiffCallback())
{
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        return if (item.type == TYPE_PARCEL)
            VIEW_TYPE_PARCEL
        else
            VIEW_TYPE_HOUSE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        val item = getItem(position)

        if (getItemViewType(position) == VIEW_TYPE_HOUSE)
        {
            (holder as HouseViewHolder).bind(item, clickListener)
        }
        if (getItemViewType(position) == VIEW_TYPE_PARCEL)
        {
            (holder as ParcelViewHolder).bind(item, clickListener)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return if (viewType == VIEW_TYPE_HOUSE)
            HouseViewHolder.from(parent)
        else
            ParcelViewHolder.from(parent)
    }
}

class HouseViewHolder(val binding: ItemHouseBinding) :
    RecyclerView.ViewHolder(binding.root)
{
    companion object
    {
        fun from(parent: ViewGroup) : HouseViewHolder
        {
            val layoutInflater = LayoutInflater.from(parent.context)

            val binding = ItemHouseBinding.inflate(
            //
                layoutInflater,
                parent,
                false
            )

            return HouseViewHolder(binding)
        }
    }

    fun bind(
    //
        item: RealEstate,
        clickListener: OnRealEstateClickListener
    ) {
        binding.clickListener = clickListener
        /**
         * This block uses the bindings defined in BindingUtils.kt
         */
        binding.house = item
        binding.executePendingBindings()
    }
}

class ParcelViewHolder(val binding: ItemParcelBinding) :
    RecyclerView.ViewHolder(binding.root)
{
    companion object
    {
        fun from(parent: ViewGroup) : ParcelViewHolder
        {
            val layoutInflater = LayoutInflater.from(parent.context)

            val binding = ItemParcelBinding.inflate(
                //
                layoutInflater,
                parent,
                false
            )

            return ParcelViewHolder(binding)
        }
    }

    fun bind(
        //
        item: RealEstate,
        clickListener: OnRealEstateClickListener
    ) {
        binding.clickListener = clickListener
        /**
         * This block uses the bindings defined in BindingUtils.kt
         */
        binding.parcel = item
        binding.executePendingBindings()
    }
}

class RealEstateDiffCallback : DiffUtil.ItemCallback<RealEstate>()
{
    override fun areItemsTheSame(old: RealEstate, new: RealEstate) : Boolean
    {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: RealEstate, new: RealEstate) : Boolean
    {
        return old == new
    }
}

class OnRealEstateClickListener(val clickListener: (realEstateId: Int) -> Unit)
{
    fun onClick(re: RealEstate) = clickListener(re.id)
}
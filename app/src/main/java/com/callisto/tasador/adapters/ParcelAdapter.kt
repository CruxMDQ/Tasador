package com.callisto.tasador.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.callisto.tasador.databinding.ItemParcelBinding
import com.callisto.tasador.domain.Parcel

class ParcelAdapter (val clickListener: OnParcelClickListener): ListAdapter<Parcel, ParcelViewHolder>(ParcelListDiffCallback())
{
    override fun onBindViewHolder(holder: ParcelViewHolder, position: Int)
    {
        val item = getItem(position)

        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelViewHolder {
        return ParcelViewHolder.from(parent)
    }
}

class ParcelViewHolder(val binding: ItemParcelBinding) : RecyclerView.ViewHolder(binding.root)
{
    companion object {
        fun from(parent: ViewGroup): ParcelViewHolder {
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
        item: Parcel,
        clickListener: OnParcelClickListener
    ) {
        binding.clickListener = clickListener
        /**
         * This block uses the bindings defined in BindingUtils.kt
         */
        binding.parcel = item
        binding.executePendingBindings()
    }
}

class ParcelListDiffCallback : DiffUtil.ItemCallback<Parcel>() {
    override fun areItemsTheSame(oldItem: Parcel, newItem: Parcel): Boolean
    {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Parcel, newItem: Parcel): Boolean {
        return oldItem == newItem
    }
}

class OnParcelClickListener(val clickListener: (parcelId: Int) -> Unit)
{
    fun onClick(parcel: Parcel) = clickListener(parcel.id)
}
package com.callisto.tasador.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.callisto.tasador.R
import kotlinx.android.synthetic.main.row_item.view.*

class SpinnerArrayAdapter<T>
    : ArrayAdapter<T>
{
    constructor(context: Context, data: List<T>) : super(context, 0, data)
    {
        this.layoutInflater = LayoutInflater.from(context)
    }

    private val layoutInflater: LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View
    {

        val view: View = convertView ?: layoutInflater.inflate(R.layout.row_item, parent, false)

        if (position != 0)
        {
            getItem(position)?.let {
                setItemText(view, it.toString())
            }
        }
        else
        {
            setItemText(view, "Select one")
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val view: View

        if (position == 0)
        {
            view = layoutInflater.inflate(R.layout.row_item, parent, false)

            view.setOnClickListener {
                val root = parent.rootView

                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))

                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
            }
        }
        else
        {
            view = layoutInflater.inflate(R.layout.row_item, parent, false)

            getItem(position)?.let {
                setItemText(view, it.toString())
            }
        }

        return view
    }

    override fun getItem(position: Int) : T? {
        return if (position == 0)
        {
            null
        } else
        {
            super.getItem(position - 1)
        }
    }

    override fun getCount(): Int
    {
        return super.getCount() + 1
    }

    override fun isEnabled(position: Int) = position != 0

    private fun setItemText(view: View, text: String)
    {
        view.lblItemName.text = text
    }
}
package com.fypmoney.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.fypmoney.model.CountryCode

class CountryCodeArrayAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val values: List<CountryCode>
) : ArrayAdapter<CountryCode>(context, layoutResource, values) {

    override fun getItem(position: Int): CountryCode = values[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createViewFromResource(convertView, parent, layoutResource)

        return bindData(getItem(position), view)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createViewFromResource(
            convertView,
            parent,
            android.R.layout.simple_spinner_dropdown_item
        )

        return bindData(getItem(position), view)
    }

    private fun createViewFromResource(
        convertView: View?,
        parent: ViewGroup,
        layoutResource: Int
    ): TextView {
        val context = parent.context
        val view =
            convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        return view as TextView

    }

    private fun bindData(value: CountryCode, view: TextView): TextView {
        view.text = value.dialCode
        return view
    }
}
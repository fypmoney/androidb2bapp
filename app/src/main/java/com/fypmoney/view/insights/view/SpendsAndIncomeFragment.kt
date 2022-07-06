package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R

class SpendsAndIncomeFragment : Fragment() {

    companion object {
        fun newInstance() = SpendsAndIncomeFragment()
    }

    private lateinit var viewModel: SpendsAndIncomeFragmentVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spends_and_income, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SpendsAndIncomeFragmentVM::class.java)
        // TODO: Use the ViewModel
    }

}
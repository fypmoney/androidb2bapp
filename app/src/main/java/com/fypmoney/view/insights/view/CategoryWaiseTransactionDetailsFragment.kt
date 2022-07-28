package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R

class CategoryWaiseTransactionDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = CategoryWaiseTransactionDetailsFragment()
    }

    private lateinit var viewModel: CategoryWaiseTransactionDetailsFragmentVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_category_waise_transaction_details,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(CategoryWaiseTransactionDetailsFragmentVM::class.java)
        // TODO: Use the ViewModel
    }

}
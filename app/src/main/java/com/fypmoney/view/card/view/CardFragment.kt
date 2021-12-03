package com.fypmoney.view.card.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCardBinding
import com.fypmoney.view.card.adapter.CardScreenOptionsAdapter
import com.fypmoney.view.card.model.CardOptionEvent
import com.fypmoney.view.card.viewmodel.CardFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class CardFragment : BaseFragment<FragmentCardBinding, CardFragmentVM>() {

    private  val cardFragmentVM by viewModels<CardFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var _binding: FragmentCardBinding

    private val binding get() = _binding

    companion object {
        fun newInstance() = CardFragment()
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int   = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int =  R.layout.fragment_card


    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): CardFragmentVM = cardFragmentVM

    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState)


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.card_details_title),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding.cardOptionRv){
            adapter = CardScreenOptionsAdapter(viewLifecycleOwner, onCardOptionClicked = {
                when(it.optionEvent){
                    CardOptionEvent.AccountStatement -> {

                    }
                    CardOptionEvent.ActivateCard -> {

                    }
                    CardOptionEvent.CardSettings -> {

                    }
                    CardOptionEvent.OrderCard -> {


                    }
                    CardOptionEvent.TrackOrder -> {

                    }
                }
            })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE);
    }

}
package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenCardBinding
import com.fypmoney.view.adapter.CardListViewAdapter
import com.fypmoney.viewmodel.CardScreenViewModel
import kotlinx.android.synthetic.main.screen_card.*


/**
 * This fragment is used for handling card
 */
class CardScreen : BaseFragment<ScreenCardBinding, CardScreenViewModel>() {
    private lateinit var mViewModel: CardScreenViewModel
    private lateinit var mViewBinding: ScreenCardBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_card
    }

    override fun getViewModel(): CardScreenViewModel {
        mViewModel = ViewModelProvider(this).get(CardScreenViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel

        val textString =
            arrayOf("Card Settings", "Order Card", "Account Statement", "Set up Spending Limit")
        val drawableIds = arrayOf<Int>(
            R.drawable.lock, R.drawable.order, R.drawable.transaction,
            R.drawable.set_up_limit
        )
        val adapter = CardListViewAdapter(requireActivity(), textString, drawableIds)


        list.setAdapter(adapter)
        /*  setToolbarAndTitle(
              context = requireContext(),
              toolbar = toolbar,
              isBackArrowVisible = true, toolbarTitle = getString(R.string.add_money_screen_title)
          )*/


        setObservers()

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        /*  mViewModel.setEdittextLength.observe(viewLifecycleOwner) {
              if (it) {
                  add_money_editext.setSelection(add_money_editext.text.length);
                  mViewModel.setEdittextLength.value = false
              }
          }

          mViewModel.onAddClicked.observe(viewLifecycleOwner) {
              if (it) {
                  callBottomSheet(mViewModel.amountSelected.get()!!, getString(R.string.dummy_amount))
                  mViewModel.onAddClicked.value = false
              }
          }*/
    }

    override fun onTryAgainClicked() {

    }


}

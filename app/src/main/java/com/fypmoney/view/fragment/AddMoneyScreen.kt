package com.fypmoney.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.databinding.ViewAddMoneyBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.AddMoneyViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*


/**
 * This fragment is used for Adding money
 */
class AddMoneyScreen : BaseFragment<ViewAddMoneyBinding, AddMoneyViewModel>(),TransactionFailBottomSheet.OnBottomSheetClickListener {

    private lateinit var mViewModel: AddMoneyViewModel
    private lateinit var mViewBinding: ViewAddMoneyBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_money
    }

    override fun getViewModel(): AddMoneyViewModel {
        mViewModel = ViewModelProvider(this).get(AddMoneyViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = false, toolbarTitle = getString(R.string.add_money_screen_title)
        )



        setObservers()

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mViewModel.setEdittextLength.observe(viewLifecycleOwner) {
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
        }
    }

    override fun onTryAgainClicked() {

    }

    /*
 * This method is used to call leave member
 * */
    private fun callBottomSheet(amount: String, updatedAmount: String) {
        val bottomSheet =
            TransactionFailBottomSheet("",
                amount,this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "TransactionFail")
    }

    override fun onBottomSheetButtonClick(type: String) {

    }


}

package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewFirstScreenBinding
import com.fypmoney.databinding.ViewTransactionHistoryBinding
import com.fypmoney.listener.OnSwipeTouchListener
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.TransactionHistoryViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_first_screen.*


/*
* This class is used to handle transaction history of a particular contact
* */
class TransactionHistoryView : BaseActivity<ViewTransactionHistoryBinding, TransactionHistoryViewModel>() {
    private lateinit var mViewModel: TransactionHistoryViewModel


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_transaction_history
    }

    override fun getViewModel(): TransactionHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(TransactionHistoryViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE))
        setToolbarAndTitle(
            context = this@TransactionHistoryView,
            toolbar = toolbar,
            isBackArrowVisible = true,toolbarTitle = mViewModel.contactName.get()
        )
        setObserver()

    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.onPayOrRequestClicked.observe(this) {
            when (it.id) {
                R.id.pay -> {
                    intentToActivity(
                        contactEntity = mViewModel.contactResult.get(),
                        aClass = EnterAmountForPayRequestView::class.java, AppConstants.PAY
                    )
                }
                R.id.request -> {
                    intentToActivity(
                        contactEntity = mViewModel.contactResult.get(),
                        aClass = EnterAmountForPayRequestView::class.java,""
                    )
                }

            }
        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity?, aClass: Class<*>, action: String) {
        val intent = Intent(this@TransactionHistoryView, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        intent.putExtra(AppConstants.WHICH_ACTION, action)
        startActivity(intent)
    }

}

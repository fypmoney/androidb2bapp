package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewPayRequestProfileBindingImpl
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.CardListViewAdapter
import com.fypmoney.viewmodel.PayRequestProfileViewModel
import kotlinx.android.synthetic.main.screen_card.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_pay_request_profile.view.*

/*
* This class is used to display pay, request for a particular contact
* */
class PayRequestProfileView :
    BaseActivity<ViewPayRequestProfileBindingImpl, PayRequestProfileViewModel>() {
    private lateinit var mViewModel: PayRequestProfileViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_pay_request_profile
    }

    override fun getViewModel(): PayRequestProfileViewModel {
        mViewModel = ViewModelProvider(this).get(PayRequestProfileViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@PayRequestProfileView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        val textString =
            arrayOf("Set up automatic pay money", "Transactions/chat")
        val drawableIds = arrayOf(
            R.drawable.auto, R.drawable.transsactions
        )
        val adapter = CardListViewAdapter(this, textString, drawableIds)
        list.adapter = adapter
        setObserver()
        mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE))
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
                        aClass = EnterAmountForPayRequestView::class.java,AppConstants.PAY
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
    private fun intentToActivity(contactEntity: ContactEntity?, aClass: Class<*>,action:String) {
        val intent = Intent(this@PayRequestProfileView, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        intent.putExtra(AppConstants.WHICH_ACTION, action)
        startActivity(intent)
    }

}

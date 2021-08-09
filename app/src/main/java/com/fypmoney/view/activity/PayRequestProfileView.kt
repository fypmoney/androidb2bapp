package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewPayRequestProfileBinding
import com.fypmoney.databinding.ViewPayRequestProfileBindingImpl
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.CardListViewAdapter
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.viewmodel.PayRequestProfileViewModel
import kotlinx.android.synthetic.main.screen_card.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_pay_request_profile.view.*

/*
* This class is used to display pay, request for a particular contact
* */
class PayRequestProfileView :
    BaseActivity<ViewPayRequestProfileBinding, PayRequestProfileViewModel>(),
    MyProfileListAdapter.OnListItemClickListener {
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
        val textString = ArrayList<String>()
        //textString.add("Set up automatic pay money")
        textString.add(getString(R.string.transaction_chat))
        val drawableIds = ArrayList<Int>()
        drawableIds.add(R.drawable.ic_chat)
        //drawableIds.add(R.drawable.transsactions)

        val myProfileAdapter = MyProfileListAdapter(applicationContext, this)
        list.adapter = myProfileAdapter

        myProfileAdapter.setList(
            iconList1 = drawableIds,
            textString
        )
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
                        aClass = EnterAmountForPayRequestView::class.java, AppConstants.PAY
                    )
                }
                R.id.request -> {
                    intentToActivity(
                        contactEntity = mViewModel.contactResult.get(),
                        aClass = EnterAmountForPayRequestView::class.java, AppConstants.REQUEST
                    )
                }

            }
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity?, aClass: Class<*>, action: String) {
        val intent = Intent(this@PayRequestProfileView, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        intent.putExtra(AppConstants.WHICH_ACTION, action)
        intent.putExtra(AppConstants.FUND_TRANSFER_QR_CODE, "")
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        when (position) {

            0 -> {
                intentToActivity(
                    contactEntity = mViewModel.contactResult.get(),
                    TransactionHistoryView::class.java, ""
                )
            }

        }
    }

}

package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewPayRequestProfileBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KYC_TYPE
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.view.bottomsheet.UpgradeYourKycBottomSheet
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
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
                    if(!checkUpgradeKycStatus()){
                        intentToActivity(
                            contactEntity = mViewModel.contactResult.get(),
                            aClass = EnterAmountForPayRequestView::class.java, AppConstants.PAY
                        )
                    }else{
                        val upgradeYourKycBottomSheet = UpgradeYourKycBottomSheet(onUpgradeClick = {
                            val intent  = Intent(this, UpgradeToKycInfoActivity::class.java).apply {
                                putExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,PayRequestProfileView::class.java.simpleName)

                            }
                            startActivity(intent)
                        })
                        upgradeYourKycBottomSheet.dialog?.window?.setBackgroundDrawable(
                            ColorDrawable(
                                Color.RED)
                        )
                        upgradeYourKycBottomSheet.show(supportFragmentManager, "UpgradeKyc")
                    }

                }
                R.id.request -> {
                    if(!checkUpgradeKycStatus()){
                        intentToActivity(
                            contactEntity = mViewModel.contactResult.get(),
                            aClass = EnterAmountForPayRequestView::class.java, AppConstants.REQUEST
                        )
                    }else{
                        val upgradeYourKycBottomSheet = UpgradeYourKycBottomSheet(onUpgradeClick = {
                            val intent  = Intent(this, UpgradeToKycInfoActivity::class.java).apply {
                                putExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,PayRequestProfileView::class.java.simpleName)

                            }
                            startActivity(intent)
                        })
                        upgradeYourKycBottomSheet.dialog?.window?.setBackgroundDrawable(
                            ColorDrawable(
                                Color.RED)
                        )
                        upgradeYourKycBottomSheet.show(supportFragmentManager, "UpgradeKyc")
                    }

                }

            }
        }

    }

    private fun checkUpgradeKycStatus():Boolean{
        SharedPrefUtils.getString(PockketApplication.instance,SF_KYC_TYPE)?.let {
            return it != AppConstants.MINIMUM
        }?:run {
             return true
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



    override fun onItemClick(position: Int, name: String?) {
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

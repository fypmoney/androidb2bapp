package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewPayRequestProfileBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KYC_TYPE
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.view.bottomsheet.UpgradeYourKycBottomSheet
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.register.fragments.CompleteKYCBottomSheet
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
import com.fypmoney.viewmodel.PayRequestProfileViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to display pay, request for a particular contact
* */
class PayRequestProfileView :
    BaseActivity<ViewPayRequestProfileBinding, PayRequestProfileViewModel>(),
    MyProfileListAdapter.OnListItemClickListener {
    private lateinit var mViewModel: PayRequestProfileViewModel
    private lateinit var binding: ViewPayRequestProfileBinding
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
        binding = getViewDataBinding()
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
        binding.list.adapter = myProfileAdapter

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
                    Utility.getCustomerDataFromPreference()?.let {it1->
                        if(it1.postKycScreenCode.isNullOrEmpty()){
                            val completeKYCBottomSheet = CompleteKYCBottomSheet(completeKycClicked = {
                                val intent = Intent(this@PayRequestProfileView, PanAdhaarSelectionActivity::class.java)
                                startActivity(intent)
                            })
                            completeKYCBottomSheet.dialog?.window?.setBackgroundDrawable(
                                ColorDrawable(
                                    Color.RED)
                            )
                            completeKYCBottomSheet.show(supportFragmentManager, "Completekyc")
                        }else{
                            if(checkUpgradeKycStatus()){
                                intentToActivity(
                                    contactEntity = mViewModel.contactResult.get(),
                                    aClass = EnterAmountForPayRequestView::class.java, AppConstants.PAY
                                )
                            }else{
                                val upgradeYourKycBottomSheet = UpgradeYourKycBottomSheet(onUpgradeClick = {
                                    trackr { it1->
                                        it1.name = TrackrEvent.upgrade_kyc_from_pay_clicked
                                    }
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
                R.id.request -> {
                    Utility.getCustomerDataFromPreference()?.let { it1->
                        if(it1.postKycScreenCode.isNullOrEmpty()){
                            val completeKYCBottomSheet = CompleteKYCBottomSheet(completeKycClicked = {
                                val intent = Intent(this@PayRequestProfileView, PanAdhaarSelectionActivity::class.java)
                                startActivity(intent)
                            })
                            completeKYCBottomSheet.dialog?.window?.setBackgroundDrawable(
                                ColorDrawable(
                                    Color.RED)
                            )
                            completeKYCBottomSheet.show(supportFragmentManager, "Completekyc")
                        }else{

                            if(checkUpgradeKycStatus()){
                                intentToActivity(
                                    contactEntity = mViewModel.contactResult.get(),
                                    aClass = EnterAmountForPayRequestView::class.java, AppConstants.REQUEST
                                )
                            }else{
                                val upgradeYourKycBottomSheet = UpgradeYourKycBottomSheet(onUpgradeClick = {
                                    trackr {it1->
                                        it1.name = TrackrEvent.upgrade_kyc_from_pay_clicked
                                    }
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

package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.PaginationListener
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewTransactionHistoryBinding
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.bottomsheet.UpgradeYourKycBottomSheet
import com.fypmoney.view.upgradetokyc.UpgradeToKycInfoActivity
import com.fypmoney.viewmodel.TransactionHistoryViewModel
import kotlinx.android.synthetic.main.view_first_screen.*
import kotlinx.android.synthetic.main.view_transaction_history.*


/*
* This class is used to handle transaction history of a particular contact
* */
class TransactionHistoryView :
    BaseActivity<ViewTransactionHistoryBinding, TransactionHistoryViewModel>() {
    private lateinit var mViewModel: TransactionHistoryViewModel


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_transaction_history
    }

    var isLoading: Boolean = false

    override fun getViewModel(): TransactionHistoryViewModel {
        mViewModel = ViewModelProvider(this).get(TransactionHistoryViewModel::class.java)
        return mViewModel
    }

    var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE))
        mViewModel.callGetTransactionHistoryApi(0)

        setToolbarAndTitle(
            context = this@TransactionHistoryView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = mViewModel.contactName.get()
        )
        setObserver()
        setRecylerView()


    }

    private fun setRecylerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {


            }

            override fun loadMoreTopItems() {
                loadMore()

            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        mViewModel!!.LoadedList.observe(this, androidx.lifecycle.Observer { list ->
            if (list != null) {


                LoadProgressBar?.visibility = View.GONE

                if (list.isNotEmpty()) {
                    var arraylist = list

                    mViewModel.transactionHistoryAdapter.setList(arraylist)
                } else if (page == 0) {
                    mViewModel.isNoDataFoundVisible.set(true)
                }
                page = page + 1
                isLoading = false

            } else {
                if (page == 0) {
                    mViewModel.isNoDataFoundVisible.set(true)
                }
            }

        })
    }

    private fun loadMore() {

        LoadProgressBar?.visibility = View.VISIBLE

        isLoading = true
        mViewModel.callGetTransactionHistoryApi(page)


    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.onItemClicked.observe(this) {

        intentToPayUActivity(PayUSuccessView::class.java, it)
        }
        mViewModel.profilepic.observe(this) {

            loadImage(
                ivServiceLogo,
                it.profilePicResourceId,
                ContextCompat.getDrawable(this, R.drawable.ic_user),
                true
            )


        }

        mViewModel.onPayOrRequestClicked.observe(this) {
            when (it.id) {
                R.id.pay -> {
                    if(checkUpgradeKycStatus()){
                        intentToActivity(
                            contactEntity = mViewModel.contactResult.get(),
                            aClass = EnterAmountForPayRequestView::class.java, AppConstants.PAY
                        )
                    }else{
                        val upgradeYourKycBottomSheet = UpgradeYourKycBottomSheet(onUpgradeClick = {
                            trackr {
                                it.name = TrackrEvent.upgrade_kyc_from_pay_clicked
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
                R.id.request -> {
                    if(checkUpgradeKycStatus()){
                        intentToActivity(
                            contactEntity = mViewModel.contactResult.get(),
                            aClass = EnterAmountForPayRequestView::class.java, AppConstants.REQUEST
                        )
                    }else{
                        val upgradeYourKycBottomSheet = UpgradeYourKycBottomSheet(onUpgradeClick = {
                            trackr {
                                it.name = TrackrEvent.upgrade_kyc_from_pay_clicked
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
    private fun checkUpgradeKycStatus():Boolean{
        SharedPrefUtils.getString(PockketApplication.instance, SharedPrefUtils.SF_KYC_TYPE)?.let {
            return it != AppConstants.MINIMUM
        }?:run {
            return true
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

    /**
     * Method to navigate to the different activity
     */
    private fun intentToPayUActivity(
        aClass: Class<*>,
        transactionHistoryResponseDetails: TransactionHistoryResponseDetails
    ) {

        val intent = Intent(this@TransactionHistoryView, aClass)
        intent.putExtra(AppConstants.RESPONSE, transactionHistoryResponseDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.TRANSACTION)
        startActivity(intent)

    }

}

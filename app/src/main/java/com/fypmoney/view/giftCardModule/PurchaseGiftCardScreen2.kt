package com.fypmoney.view.giftCardModule

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewPurchaseGiftCard2Binding

import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.fragments.GiftCardNotFyperBottomSheet
import com.fypmoney.view.giftCardModule.fragments.GiftCardPurchasedBottomSheet
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardRequest
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardResponse
import com.fypmoney.view.giftCardModule.model.VoucherDetailsItem
import com.fypmoney.view.giftCardModule.model.VoucherProductItem

import com.fypmoney.view.giftCardModule.viewModel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import java.util.*
import kotlin.collections.ArrayList


class PurchaseGiftCardScreen2 : BaseActivity<ViewPurchaseGiftCard2Binding, PurchaseGiftViewModel>(),
    DialogUtils.OnAlertDialogClickListener,
    Utility.OnAllContactsAddedListener {

    private var giftCardpurchaseBottomSheet: GiftCardNotFyperBottomSheet? = null
    private var giftCardAdapter: GiftProductListAdapter? = null
    private var giftList: List<VoucherProductItem?>? = ArrayList()

    private lateinit var mViewModel: PurchaseGiftViewModel
    private lateinit var mViewBinding: ViewPurchaseGiftCard2Binding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_purchase_gift_card2
    }

    override fun getViewModel(): PurchaseGiftViewModel {
        mViewModel = ViewModelProvider(this).get(PurchaseGiftViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Myntra E Gift Card"
        )


        mViewBinding.payAndPurchase.setOnClickListener {
            if (mViewModel.selectedContactFromList.get() != null && mViewModel.selectedGiftCard.get() != null) {
                var purchase = PurchaseGiftCardRequest()
//                purchase.destinationMobileNo =
//                    mViewModel.selectedContactFromList.get()?.contactNumber
//                purchase.destinationName =
//                    mViewModel.selectedContactFromList.get()?.firstName + " " + mViewModel.selectedContactFromList.get()?.lastName

                if (mViewBinding.myself.isSelected) {
                    purchase.destinationMobileNo = SharedPrefUtils.getString(
                        application,
                        SharedPrefUtils.SF_KEY_USER_MOBILE
                    )


                    purchase.giftedPerson = "FYPUSER"

                    purchase.destinationEmail = SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_EMAIL
                    )

                    var voucher = VoucherDetailsItem()
                    voucher.voucherProductId = mViewModel.selectedGiftCard.get()?.id
                    val supplierNames: List<VoucherDetailsItem> = listOf(voucher)
                    purchase.voucherDetails = supplierNames
                    mViewModel.purchaseGiftCardRequest(purchase)
                } else if (mViewBinding.someone.isSelected) {
                    purchase.destinationMobileNo = mViewBinding.phone.text.toString()
                    purchase.giftedPerson = "NOTFYPUSER"
                    purchase.destinationEmail = mViewBinding.etEmailIdData.text.toString()
                    var voucher = VoucherDetailsItem()
                    voucher.voucherProductId = mViewModel.selectedGiftCard.get()?.id
                    val supplierNames: List<VoucherDetailsItem> = Arrays.asList(voucher)
                    purchase.voucherDetails = supplierNames
                    mViewModel.purchaseGiftCardRequest(purchase)


                }
            }
        }

        setObservers()

        mViewModel.callBrandGiftCards("OLA")
        checkAndAskPermission()
        setUpRecyclerView()
    }


    private fun checkAndAskPermission() {
        when (checkPermission(android.Manifest.permission.READ_CONTACTS)) {
            true -> {
                mViewModel.progressDialog.value = true
                Utility.getAllContactsInList(
                    contentResolver,
                    this,
                    contactRepository = mViewModel.contactRepository
                )

            }
            else -> {
                requestPermission(android.Manifest.permission.READ_CONTACTS)
            }
        }
    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mViewModel?.productList?.observe(this, {

            mViewBinding.messageText.text = it.giftMessage
            (mViewBinding?.rvProducts?.adapter as GiftProductListAdapter).run {
                giftList = it.voucherProduct
                submitList(giftList)
            }

        })
        mViewModel?.giftpurchased?.observe(this, {
            GiftCardPurchased(it)


        })

        mViewModel.onAddClicked.observe(this) {
            if (it) {

                mViewModel.onAddClicked.value = false
            }
        }
        mViewModel.onItemClicked.observe(this) {
            mViewModel.selectedContactFromList.set(it)
            if (mViewModel.selectedGiftCard.get() != null) {
                mViewBinding.payAndPurchase.isEnabled = true

            }

//            val resultIntent = Intent()
//            resultIntent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, it)
//            setResult(13, resultIntent)
//            finish()

        }

        mViewModel.onIsAppUserClicked.observe(this) {
            if (it) {
                if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
                    inviteUser()
                } else {
//                    callInviteBottomSheet()

                }

                mViewModel.onIsAppUserClicked.value = false
            }
        }
        mViewModel.emptyContactListError.observe(this) {
            if (it) {
                DialogUtils.showConfirmationDialog(
                    context = this,
                    title = "",
                    message = getString(R.string.empty_contact_error),
                    positiveButtonText = getString(R.string.cancel_btn_text),
                    negativeButtonText = getString(R.string.cancel_btn_text),
                    uniqueIdentifier = "",
                    onAlertDialogClickListener = this, isNegativeButtonRequired = false
                )
                mViewModel.emptyContactListError.value = false
            }
        }

    }


    private fun GiftCardPurchased(list: PurchaseGiftCardResponse?) {
        giftCardpurchaseBottomSheet?.dismiss()
        var bottomSheetMessage2 =
            GiftCardPurchasedBottomSheet(list)
        bottomSheetMessage2.isCancelable = false
        bottomSheetMessage2?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage2?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun GiftCardNotFyperSheet() {

        giftCardpurchaseBottomSheet =
            GiftCardNotFyperBottomSheet(mViewModel)
        giftCardpurchaseBottomSheet?.isCancelable = true
        giftCardpurchaseBottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        giftCardpurchaseBottomSheet?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun setUpRecyclerView() {
        giftCardAdapter = GiftProductListAdapter(
            lifecycleOwner = this, giftcardClicked = {
                giftList?.forEachIndexed { pos, item ->
                    if (it == pos) {
                        giftList?.get(pos)?.selected = true
                        mViewModel.selectedGiftCard.set(giftList!![pos])
                        if (mViewModel.selectedContactFromList.get() != null) {
                            mViewBinding.payAndPurchase.isEnabled = true

                        }
                    } else {
                        giftList?.get(pos)?.selected = false
                    }

                }
                giftCardAdapter?.submitList(giftList)
                giftCardAdapter?.notifyDataSetChanged()
            }
        )


        with(mViewBinding!!.rvProducts) {
            adapter = giftCardAdapter
            layoutManager = LinearLayoutManager(
                this@PurchaseGiftCardScreen2,
                androidx.recyclerview.widget.RecyclerView.HORIZONTAL,
                false
            )
        }
    }

    override fun onTryAgainClicked() {
        mViewModel.callContactSyncApi()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Utility.showToast(getString(R.string.permission_required))
                requestPermission(android.Manifest.permission.READ_CONTACTS)
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    Utility.getAllContactsInList(
                        contentResolver,
                        this,
                        contactRepository = mViewModel.contactRepository
                    )
                    mViewModel.progressDialog.value = true

                } else {
                    //set to never ask again
                    SharedPrefUtils.putBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_STORAGE_PERMANENTLY_DENY,
                        true
                    )
                }
            }
        }


    }

    override fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>?) {
        mViewModel.callContactSyncApi()
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        finish()
    }


}

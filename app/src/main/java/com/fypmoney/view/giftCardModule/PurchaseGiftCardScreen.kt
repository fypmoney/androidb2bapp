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
import com.fypmoney.databinding.ViewPurchaseGiftCardBinding
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.fragments.GiftCardPurchasedBottomSheet
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardRequest
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardResponse
import com.fypmoney.view.giftCardModule.model.VoucherDetailsItem
import com.fypmoney.view.giftCardModule.model.VoucherProductItem

import com.fypmoney.view.giftCardModule.viewModel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import java.util.*
import kotlin.collections.ArrayList


class PurchaseGiftCardScreen : BaseActivity<ViewPurchaseGiftCardBinding, PurchaseGiftViewModel>(),
    DialogUtils.OnAlertDialogClickListener,
    Utility.OnAllContactsAddedListener {

    private var giftCardAdapter: GiftProductListAdapter? = null
    private var giftList: List<VoucherProductItem?>? = ArrayList()
    private var selectedGiftCard: VoucherProductItem? = null
    private var selectedContact: ContactEntity? = null
    private lateinit var mViewModel: PurchaseGiftViewModel
    private lateinit var mViewBinding: ViewPurchaseGiftCardBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_purchase_gift_card
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


        mViewBinding.payAndPurchase.setOnClickListener(View.OnClickListener {
            if (selectedContact != null && selectedGiftCard != null) {
                var purchase = PurchaseGiftCardRequest()
                purchase.destinationMobileNo = selectedContact?.contactNumber
                purchase.destinationName =
                    selectedContact?.firstName + " " + selectedContact?.lastName
                if (selectedContact?.isAppUser == true)
                    purchase.giftedPerson = "FYPUSER"
                else
                    purchase.giftedPerson = "NOTFYPUSER"

                purchase.destinationEmail = "ranjeet@ranjeet.in"

                var voucher = VoucherDetailsItem()
                voucher.voucherProductId = selectedGiftCard?.id
                val supplierNames: List<VoucherDetailsItem> = Arrays.asList(voucher)
                purchase.voucherDetails = supplierNames
                mViewModel.purchaseGiftCardRequest(purchase)

            }

        })
        setObservers()
        setUpListners()
        mViewModel.callBrandGiftCards("OLA")
        checkAndAskPermission()
        setUpRecyclerView()
    }

    private fun setUpListners() {
        mViewBinding.search.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                mViewBinding.contactsHeading.visibility = View.INVISIBLE
            } else {
                if (mViewBinding.search.isIconified) {
                    mViewBinding.contactsHeading.visibility = View.VISIBLE
                }
            }
        }
        mViewBinding.notFyperSelected.setOnClickListener(View.OnClickListener {

        })
        mViewBinding.search.setOnCloseListener {
            mViewBinding.contactsHeading.visibility = View.VISIBLE
            false
        }


//        val closeButton: ImageView = mViewBinding.search.findViewById(com.fypmoney.R.id.search_close_btn) as ImageView
//        closeButton.setOnClickListener {
//            mViewBinding.contactsHeading.visibility = View.VISIBLE
//            mViewBinding.search.isIconified=false
//
//        }

        mViewBinding.search.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                mViewModel.searchedContact.set(s.toString())
                val list = mViewModel.contactAdapter.newContactList?.filter {
                    s?.let { it1 ->
                        it.firstName!!.contains(
                            it1,
                            ignoreCase = true
                        )
                    } == true || s?.let { it1 ->
                        it.contactNumber?.contains(
                            it1
                        )
                    }!!
                }
                if (list?.size != 0) {
                    mViewModel.contactAdapter.setList(list)
                } else {
                    mViewModel.contactAdapter.newSearchList?.clear()
                    val contactEntity = ContactEntity()
                    contactEntity.contactNumber = mViewModel.searchedContact.get()
                    contactEntity.firstName =
                        PockketApplication.instance.getString(R.string.new_number_text)
                    mViewModel.contactAdapter.newSearchList?.add(contactEntity)
                    mViewModel.contactAdapter.setList(mViewModel.contactAdapter.newSearchList)
                    if (mViewModel.searchedContact.get()?.length == 10) {

                    }
                }
                return false
            }

        })

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
            selectedContact = it
            if (selectedGiftCard != null) {
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

    override fun onBackPressed() {
        if (!mViewBinding.search.isIconified) {
            mViewBinding.search.isIconified = true
            mViewBinding.contactsHeading.visibility = View.VISIBLE
        } else super.onBackPressed()
    }

    private fun GiftCardPurchased(list: PurchaseGiftCardResponse?) {

        var bottomSheetMessage2 =
            GiftCardPurchasedBottomSheet(list)
        bottomSheetMessage2.isCancelable = false
        bottomSheetMessage2?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage2?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun setUpRecyclerView() {
        giftCardAdapter = GiftProductListAdapter(
            lifecycleOwner = this, giftcardClicked = {
                giftList?.forEachIndexed { pos, item ->
                    if (it == pos) {
                        giftList?.get(pos)?.selected = true
                        selectedGiftCard = giftList!![pos]
                        if (selectedContact != null) {
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
                this@PurchaseGiftCardScreen,
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

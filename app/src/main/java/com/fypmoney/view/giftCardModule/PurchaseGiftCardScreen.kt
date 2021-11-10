package com.fypmoney.view.giftCardModule

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewAddMoneyBinding
import com.fypmoney.databinding.ViewPurchaseGiftCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

import com.fypmoney.viewmodel.PurchaseGiftViewModel
import kotlinx.android.synthetic.main.toolbar_gift_card.*


class PurchaseGiftCardScreen : BaseActivity<ViewPurchaseGiftCardBinding, PurchaseGiftViewModel>(),
    DialogUtils.OnAlertDialogClickListener,
    Utility.OnAllContactsAddedListener {

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
                mViewBinding.contactsHeading.visibility = View.VISIBLE
            }
        }
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
                submitList(it.voucherProduct)
            }

        })

        mViewModel.onAddClicked.observe(this) {
            if (it) {

                mViewModel.onAddClicked.value = false
            }
        }
        mViewModel.onItemClicked.observe(this) {

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

    private fun setUpRecyclerView() {
        val giftCardAdapter = GiftProductListAdapter(
            lifecycleOwner = this, onRecentUserClick = {

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

package com.fypmoney.view.giftcard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.FragmentCreateEGiftCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ContactViewAddMember
import com.fypmoney.view.giftcard.adapters.GiftCardPossibleDenominationAmountList
import com.fypmoney.view.giftcard.fragments.GiftCardNotFyperBottomSheet
import com.fypmoney.view.giftcard.fragments.GiftCardPurchasedBottomSheet
import com.fypmoney.view.giftcard.model.*
import com.fypmoney.view.giftcard.viewModel.CreateEGiftCardFragmentVM
import kotlinx.android.synthetic.main.fragment_create_e_gift_card.view.*
import kotlinx.android.synthetic.main.toolbar_gift_card.*
import java.util.*


class CreateEGiftCardFragment : BaseActivity<FragmentCreateEGiftCardBinding, CreateEGiftCardFragmentVM>(),
    DialogUtils.OnAlertDialogClickListener,
    Utility.OnAllContactsAddedListener {


    private var giftCardpurchaseBottomSheet: GiftCardNotFyperBottomSheet? = null
    private var giftCardAdapter: GiftCardPossibleDenominationAmountList? = null
    private var giftList: ArrayList<VoucherProductAmountsItem?>? = ArrayList()

    private lateinit var mViewModel: CreateEGiftCardFragmentVM
    private lateinit var mViewBinding: FragmentCreateEGiftCardBinding
    private var gotBrandDetails: VoucherBrandItem? = null
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_e_gift_card
    }

    override fun getViewModel(): CreateEGiftCardFragmentVM {
        mViewModel = ViewModelProvider(this).get(CreateEGiftCardFragmentVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding?.viewModel = mViewModel

        tnc.setOnClickListener {
            if (gotBrandDetails != null) {

                intentToActivity(gotBrandDetails, GiftTermsAndConditions::class.java)

            }
        }

        mViewBinding.selectContact.setOnClickListener {
            intentToActivity(ContactViewAddMember::class.java)
        }

        gotBrandDetails = intent?.getParcelableExtra(AppConstants.GIFT_BRAND_SELECTED)

        setUpRecyclerView()
        setData(gotBrandDetails)



        setObservers()
        mViewBinding.radiogroup.setOnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton = group.myself as RadioButton
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                // Changes the textview's text to "Checked: example radiobutton text"
                mViewBinding.otheruserLayout.visibility = View.GONE
            } else {
                mViewBinding.otheruserLayout.visibility = View.VISIBLE

            }
        }

        mViewModel.callBrandGiftCards("")


    }

    private fun setData(gotBrandDetails: VoucherBrandItem?) {
        Glide.with(this).load(gotBrandDetails?.detailImage).placeholder(shimmerDrawable())
            .into(mViewBinding.banner)
        mViewModel.selectedGiftCard.set(gotBrandDetails?.voucherProduct?.get(0))
        mViewBinding.maxMinLen.text = gotBrandDetails?.brandTagLine2
        mViewModel.minValue.set(gotBrandDetails?.minPrice)

        mViewModel.maxValue.set(gotBrandDetails?.maxPrice)
        if (gotBrandDetails?.voucherProduct?.get(0)?.isFlexiblePrice == AppConstants.NO) {
            mViewModel.flexibleAmount.set(false)
//            mViewBinding.giftAmount.isEnabled  = false

        } else {
            mViewModel.flexibleAmount.set(true)
            mViewBinding.giftAmount.isEnabled = true
        }

        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle =gotBrandDetails?.displayName,
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )



        gotBrandDetails?.let {

//            it.giftMessage.let {it1->
//                mViewBinding.giftMsg.text = it1
//            }


            (mViewBinding?.rvProducts?.adapter as GiftCardPossibleDenominationAmountList).run {

                val strs = it.possibleDenominationList?.split(",")?.toTypedArray()

                strs?.forEach {
                    var voucherProductAmountsItem = VoucherProductAmountsItem()
                    voucherProductAmountsItem.name = it
                    giftList?.add(voucherProductAmountsItem)
                }
                submitList(giftList)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val returnValue: ContactEntity? =
            data?.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE)
        if (requestCode == 13 && resultCode != RESULT_CANCELED && returnValue != null) {

            mViewBinding.phone.setText(Utility.removePlusOrNineOneFromNo(returnValue.contactNumber))
            if (!returnValue?.lastName.isNullOrEmpty()) {
                mViewBinding.etFirstName.setText(returnValue?.firstName + " " + returnValue?.lastName)

            } else {
                mViewBinding.etFirstName.setText(returnValue?.firstName)

            }

        }


    }

    private fun intentToActivity(contactEntity: VoucherBrandItem?, aClass: Class<*>) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.GIFT_BRAND_SELECTED, contactEntity)
        startActivity(intent)
    }

    private fun intentToActivityGift(contactEntity: Int?, aClass: Class<*>) {
        contactEntity.let {
            val intent = Intent(this, aClass)
            intent.putExtra(AppConstants.GIFT_ID, it)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
            finish()
        }

    }

    private fun intentToActivity(aClass: Class<*>) {
        startActivityForResult(Intent(this, aClass), 13)
    }


    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
//        mViewModel?.productList?.observe(this) {
//
//            mViewBinding.messageText.text = it.giftMessage
//
//
//            (mViewBinding?.rvProducts?.adapter as GiftProductListAdapter).run {
//                giftList = it.voucherProduct
//                submitList(giftList)
//            }
//        }
        mViewModel?.giftpurchased?.observe(this) {
            GiftCardPurchased(it)


        }

        mViewModel.onAddClicked.observe(this) {
            if (it) {
                if (mViewModel.selectedGiftCard.get() != null) {
                    var purchase = PurchaseGiftCardRequest()
//                purchase.destinationName =
//                    mViewModel.selectedContactFromList.get()?.firstName + " " + mViewModel.selectedContactFromList.get()?.lastName

                    if (mViewBinding.myself.isChecked) {
                        purchase.destinationMobileNo = SharedPrefUtils.getString(
                            application,
                            SharedPrefUtils.SF_KEY_USER_MOBILE
                        )

                        purchase.giftedPerson = "MYSELF"
                        var firstName = SharedPrefUtils.getString(
                            getApplication(), key = SharedPrefUtils.SF_KEY_USER_FIRST_NAME
                        )
                        var lastName = SharedPrefUtils.getString(
                            getApplication(), key = SharedPrefUtils.SF_KEY_USER_LAST_NAME
                        )
                        purchase.destinationEmail = SharedPrefUtils.getString(
                            application,
                            SharedPrefUtils.SF_KEY_USER_EMAIL
                        )
                        purchase.destinationName = "$firstName $lastName"
                        var voucher = VoucherDetailsItem()
                        voucher.voucherProductId = mViewModel.selectedGiftCard.get()?.id
                        mViewModel.amountSelected.get()?.toIntOrNull()?.let {
                            voucher.amount = it * 100
                        }
                        val supplierNames: List<VoucherDetailsItem> = listOf(voucher)
                        purchase.voucherDetails = supplierNames
                        mViewModel.purchaseGiftCardRequest(purchase)
                    } else if (mViewBinding.someone.isChecked) {
                        purchase.destinationMobileNo = mViewBinding.phone.text.toString()
                        purchase.giftedPerson = "NONE"
                        purchase.destinationName = mViewBinding.etFirstName.text.toString()
                        purchase.destinationEmail = mViewBinding.etEmailIdData.text.toString()
                        var voucher = VoucherDetailsItem()
                        voucher.voucherProductId = mViewModel.selectedGiftCard.get()?.id
                        mViewModel.amountSelected.get()?.toIntOrNull()?.let {
                            voucher.amount = it * 100
                        }
                        val supplierNames: List<VoucherDetailsItem> = Arrays.asList(voucher)
                        purchase.voucherDetails = supplierNames
                        mViewModel.purchaseGiftCardRequest(purchase)


                    }
                }
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


    }


    private fun GiftCardPurchased(list: PurchaseGiftCardResponse?) {
        giftCardpurchaseBottomSheet?.dismiss()
        var bottomSheetMessage2 =
            GiftCardPurchasedBottomSheet(list, clickedListner = {
                if (list?.voucherOrderDetailId != null) {

                    intentToActivityGift(
                        list?.voucherOrderDetailId,
                        GiftDetailsActivity::class.java
                    )

                } else {
                    val intent = Intent(this, GiftHistoryActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                    finish()
                }

            })
        bottomSheetMessage2.isCancelable = false
        bottomSheetMessage2?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage2?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun intentToActivity(contactEntity: Int?, aClass: Class<*>) {
        contactEntity.let {
            val intent = Intent(this, aClass)
            intent.putExtra(AppConstants.GIFT_ID, it)
            startActivity(intent)
        }

    }

    private fun GiftCardNotFyperSheet() {

        giftCardpurchaseBottomSheet =
            GiftCardNotFyperBottomSheet(mViewModel)
        giftCardpurchaseBottomSheet?.isCancelable = true
        giftCardpurchaseBottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        giftCardpurchaseBottomSheet?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun setUpRecyclerView() {
        giftCardAdapter = GiftCardPossibleDenominationAmountList(
            lifecycleOwner = this, onAmountClicked = {
                giftList?.forEachIndexed { pos, item ->
                    if (it == pos) {
                        giftList?.get(pos)?.selected = true


                        if (mViewModel.selectedContactFromList.get() != null) {
                            mViewBinding.payAndPurchase.isEnabled = true

                        }
                        mViewBinding.giftAmount.setText(item?.name.toString())
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
                this@CreateEGiftCardFragment,
                RecyclerView.HORIZONTAL,
                false
            )
        }
    }






    override fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>?) {

    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        finish()
    }


}

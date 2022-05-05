package com.fypmoney.view.ordercard.placeorder

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewPlaceCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.fragment.PriceBreakupBottomSheet
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.ordercard.model.PinCodeData
import com.fypmoney.view.ordercard.model.UserDeliveryAddress
import com.fypmoney.view.ordercard.placeordersuccess.PlaceOrderSuccessActivity
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.register.fragments.CompleteKYCBottomSheet
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*


/*
* This class is used to order card
* */
class PlaceOrderCardView : BaseActivity<ViewPlaceCardBinding, PlaceOrderCardViewModel>() {
    private lateinit var binding: ViewPlaceCardBinding
    private lateinit var mViewModel: PlaceOrderCardViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_place_card
    }

    override fun getViewModel(): PlaceOrderCardViewModel {
        mViewModel = ViewModelProvider(this).get(PlaceOrderCardViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@PlaceOrderCardView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.order_card),  backArrowTint = Color.WHITE,
            titleColor = Color.WHITE
        )
        helpValue.text = getString(R.string.aadhaar_number_help_text)
        helpValue.setOnClickListener {
            callFreshChat(applicationContext)

        }

        mViewModel.userOfferCard = intent.getParcelableExtra(AppConstants.ORDER_CARD_INFO)
        mViewModel.placeOrderRequest?.nameOnCard = SharedPrefUtils.getString(this,
            SharedPrefUtils.SF_KEY_NAME_ON_CARD
        )
        mViewModel.mapRequestOrderObject()
        setUpViews()
        setObservers()


    }

    private fun setUpViews() {

        mViewModel.userOfferCard?.let {
            binding.cardPriceValueTv.text =
                "${getString(R.string.Rs)} ${Utility.convertToRs(it?.mrp.toString())}"
            binding.cardNameTv.text = it.name
            binding.btnContinue.setText(String.format(getString(R.string.pay_amount),Utility.convertToRs(
                it.mrp)))
        }
        Utility.getCustomerDeliveryAddress()?.let {
             binding.pinCodeTie.setText(it.pincode)
            mViewModel.getPinCode(it.pincode)
            binding.houseNumberBuildingTie.setText(it.houseAddress)
             binding.roadNameAreaTie.setText(it.areaDetail)
             binding.landmarkTie.setText(it.landmark)
        }
        binding.pinCodeTie.doOnTextChanged { text, start, before, count ->
            if (text?.length == 6) {
                mViewModel.getPinCode(text.toString())
                if(validateAndEnabledPlaceOrderBtn(pinCode = binding.pinCodeTie.text.toString(),
                    state = binding.stateTie.text.toString(),city = binding.cityTie.toString(),
                    houseNumber = binding.houseNumberBuildingTie.text.toString(),
                    roadNumber = binding.roadNameAreaTie.text.toString()
                )){
                    binding.btnContinue.isEnabled = true
                }
            }
        }
        binding.pinCodeTie.doBeforeTextChanged { text, start, count, after ->
            if (text?.length != 6) {
                binding.stateTie.setText("")
                binding.cityTie.setText("")
                mViewModel.placeOrderRequest?.pincode = null
                mViewModel.placeOrderRequest?.state = null
                mViewModel.placeOrderRequest?.city = null
                mViewModel.placeOrderRequest?.stateCode = null
            }
        }

        binding.stateTie.doOnTextChanged { text, start, before, count ->
            if(validateAndEnabledPlaceOrderBtn(pinCode = binding.pinCodeTie.text.toString(),
                    state = binding.stateTie.text.toString(),city = binding.cityTie.toString(),
                    houseNumber = binding.houseNumberBuildingTie.text.toString(),
                    roadNumber = binding.roadNameAreaTie.text.toString()
                )){
                binding.btnContinue.isEnabled = true
            }
        }
        binding.cityTie.doOnTextChanged { text, start, before, count ->
            if(validateAndEnabledPlaceOrderBtn(pinCode = binding.pinCodeTie.text.toString(),
                    state = binding.stateTie.text.toString(),city = binding.cityTie.toString(),
                    houseNumber = binding.houseNumberBuildingTie.text.toString(),
                    roadNumber = binding.roadNameAreaTie.text.toString()
                )){
                binding.btnContinue.isEnabled = true
            }
        }
        binding.houseNumberBuildingTie.doOnTextChanged { text, start, before, count ->
            if(validateAndEnabledPlaceOrderBtn(pinCode = binding.pinCodeTie.text.toString(),
                    state = binding.stateTie.text.toString(),city = binding.cityTie.toString(),
                    houseNumber = binding.houseNumberBuildingTie.text.toString(),
                    roadNumber = binding.roadNameAreaTie.text.toString()
                )){
                binding.btnContinue.isEnabled = true
            }
        }
        binding.roadNameAreaTie.doOnTextChanged { text, start, before, count ->
            if(validateAndEnabledPlaceOrderBtn(pinCode = binding.pinCodeTie.text.toString(),
                    state = binding.stateTie.text.toString(),city = binding.cityTie.toString(),
                    houseNumber = binding.houseNumberBuildingTie.text.toString(),
                    roadNumber = binding.roadNameAreaTie.text.toString()
                )){
                binding.btnContinue.isEnabled = true
            }
        }
    }

    fun setObservers() {
        mViewModel.event.observe(this) {
            handelEvent(it)
        }
        mViewModel.state.observe(this) {
            handelState(it)
        }
    }
    private fun handelEvent(it: PlaceOrderCardViewModel.PlaceOrderCardEvent?) {
        when(it){
            PlaceOrderCardViewModel.PlaceOrderCardEvent.OnViewPriceBreakUp ->{
                callPriceBreakupBottomSheet()
            }
            PlaceOrderCardViewModel.PlaceOrderCardEvent.NotServicingInThisArea -> {
                callNotServicebleSheet()
            }
            PlaceOrderCardViewModel.PlaceOrderCardEvent.OnPlaceOrder ->{
                trackr { it.services = arrayListOf(TrackrServices.FIREBASE,TrackrServices.MOENGAGE)
                it.name = TrackrEvent.pay_button
                it.add(TrackrField.user_id,SharedPrefUtils.getLong(
                    applicationContext,
                    SharedPrefUtils.SF_KEY_USER_ID
                ).toString())
                }

                Utility.getCustomerDataFromPreference()?.let {
                    if(it.postKycScreenCode.isNullOrEmpty()){
                        val completeKYCBottomSheet = CompleteKYCBottomSheet(completeKycClicked = {
                            val intent = Intent(this, PanAdhaarSelectionActivity::class.java)
                            startActivity(intent)
                        })
                        completeKYCBottomSheet.dialog?.window?.setBackgroundDrawable(
                            ColorDrawable(
                                Color.RED)
                        )
                        completeKYCBottomSheet.show(supportFragmentManager, "Completekyc")
                    }else{
                        askForDevicePassword()

                    }
                }
            }
            is PlaceOrderCardViewModel.PlaceOrderCardEvent.InSufficientBalance ->{
                callInsuficientFundMessageSheet(it.amount)
            }

        }
    }

    private fun makePlaceOrderRequest() {
        mViewModel.placeOrderRequest?.houseAddress = binding.houseNumberBuildingTie.text.toString()
        mViewModel.placeOrderRequest?.areaDetail = binding.roadNameAreaTie.text.toString()
        mViewModel.placeOrderRequest?.landmark = binding.landmarkTie.text.toString()
        mViewModel.placeOrderCard()
    }


    private fun handelState(it: PlaceOrderCardViewModel.PlaceOrderCardState?) {
        when(it){
            is PlaceOrderCardViewModel.PlaceOrderCardState.PinCodeSuccess -> {
                setUpStateAndCity(it.pinCode)
            }
            is PlaceOrderCardViewModel.PlaceOrderCardState.PlaceOrderError -> {
                Utility.showToast(getString(R.string.please_tyr_again_later))
            }
            is PlaceOrderCardViewModel.PlaceOrderCardState.PlaceOrderSuccess -> {
                Utility.setCustomerDeliveryAddress(
                    UserDeliveryAddress(
                        pincode = binding.pinCodeTie.text.toString(),
                        houseAddress = binding.houseNumberBuildingTie.text.toString(),
                        areaDetail = binding.roadNameAreaTie.text.toString(),
                        landmark = binding.landmarkTie.text.toString(),
                    )
                )

                trackr {
                    it.services = arrayListOf(
                        TrackrServices.FIREBASE,
                        TrackrServices.MOENGAGE,
                        TrackrServices.FB,TrackrServices.ADJUST)
                    it.name = TrackrEvent.order_success
                    it.add(
                        TrackrField.user_id,SharedPrefUtils.getLong(
                            applicationContext,
                            SharedPrefUtils.SF_KEY_USER_ID
                        ).toString())
                }
                intentToActivity(PlaceOrderSuccessActivity::class.java)
            }
        }
    }

    private fun setUpStateAndCity(it: PinCodeData) {
        binding.stateTie.setText(it.state);
        binding.cityTie.setText(it.city);
    }

    /*
   * This method is used to call card settings
   * */
    private fun callPriceBreakupBottomSheet() {
        mViewModel.userOfferCard?.let {
            val bottomSheet =
                PriceBreakupBottomSheet(it)
            bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
            bottomSheet.show(supportFragmentManager, "PlaceOrderBottomSheet")
        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@PlaceOrderCardView, aClass))
        finish()
    }

    private fun validateAndEnabledPlaceOrderBtn(pinCode:String,
                                                state:String,
                                                city:String,
                                                houseNumber:String,
                                                roadNumber:String):Boolean{
        if(pinCode.isEmpty() or state.isEmpty() or city.isEmpty() or houseNumber.isEmpty() or roadNumber.isEmpty()){
            return false
        }
        return true
    }


    override fun onBackPressed() {
        Utility.setCustomerDeliveryAddress(
            UserDeliveryAddress(
                pincode = binding.pinCodeTie.text.toString(),
                houseAddress = binding.houseNumberBuildingTie.text.toString(),
                areaDetail = binding.roadNameAreaTie.text.toString(),
                landmark = binding.landmarkTie.text.toString(),
            )
        )
        super.onBackPressed()
    }


    private fun callInsuficientFundMessageSheet(amount: String?) {
        var bottomsheetInsufficient:TaskMessageInsuficientFuntBottomSheet? = null
        val itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                bottomsheetInsufficient?.dismiss()
            }

            override fun onRejectClicked(pos: Int) {
                bottomsheetInsufficient?.dismiss()

                trackr {
                    it.name = TrackrEvent.add_money_button
                    it.add(
                        TrackrField.user_id,SharedPrefUtils.getLong(
                            applicationContext,
                            SharedPrefUtils.SF_KEY_USER_ID
                        ).toString())
                }
                val intent = Intent(this@PlaceOrderCardView,AddMoneyView::class.java)
                intent.putExtra("amountshouldbeadded", Utility.convertToRs(amount))
                startActivity(intent)
            }

            override fun ondimiss() {
                bottomsheetInsufficient?.dismiss()
            }
        }
         bottomsheetInsufficient = TaskMessageInsuficientFuntBottomSheet(itemClickListener2,
            title = resources.getString(R.string.insufficient_bank_balance),
            subTitle =  resources.getString(R.string.insufficient_bank_body),
            amount = resources.getString(R.string.add_money_title1)+resources.getString(R.string.Rs)+Utility.convertToRs(amount),
             background = "#2d3039",
             titleColor  = "#ffffff",
             moneyTextColor  = "#ffffff",
             buttonColor  = "#8ECC9A",
         )

        bottomsheetInsufficient.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomsheetInsufficient.show(supportFragmentManager, "PLACEORDER")
    }

    private fun callNotServicebleSheet() {
        val bottomSheet = NotServiceableBottomSheet(onNotifyClick = {
            intentToActivity(HomeActivity::class.java)
        })
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "NOTSERVICEABLE")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        runOnUiThread {
                            makePlaceOrderRequest()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Utility.setCustomerDeliveryAddress(
            UserDeliveryAddress(
                pincode = binding.pinCodeTie.text.toString(),
                houseAddress = binding.houseNumberBuildingTie.text.toString(),
                areaDetail = binding.roadNameAreaTie.text.toString(),
                landmark = binding.landmarkTie.text.toString(),
            )
        )
        super.onDestroy()
    }

}

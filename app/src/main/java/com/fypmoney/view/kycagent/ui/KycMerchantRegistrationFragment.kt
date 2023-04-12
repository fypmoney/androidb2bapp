package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentKycMerchantRegistrationBinding
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.KycMerchantRegistrationFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class KycMerchantRegistrationFragment : BaseFragment<FragmentKycMerchantRegistrationBinding, KycMerchantRegistrationFragmentVM>(),
    LocationListenerClass.GetCurrentLocationListener {

    private lateinit var binding: FragmentKycMerchantRegistrationBinding
    private val kycMerchantRegistrationFragmentVM by viewModels<KycMerchantRegistrationFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        binding.btnAddWithdrawSavings.setOnClickListener {
            if (checkAllFields()){
//                gotoNextScreen()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.agentName = binding.etName.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.addr1 = binding.etAddress.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.agentContact1 = Utility.getCustomerDataFromPreference()?.mobile.toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.shopName = binding.etBusinessName.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.pincode = binding.etPinCode.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.city = binding.etCity.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.state = binding.etState.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.isPosterOrdered = kycMerchantRegistrationFragmentVM.mcbPosterValue

                kycMerchantRegistrationFragmentVM.saveShopDetails(kycMerchantRegistrationFragmentVM.saveShopDetailsRequest)
            }else{
                Utility.showToast("Please fill all required values...")
            }
        }

        binding.mcbYes.setOnClickListener {
            binding.mcbYes.isChecked = true
            binding.mcbNo.isChecked = false
            kycMerchantRegistrationFragmentVM.mcbPosterValue = AppConstants.YES
        }

        binding.mcbNo.setOnClickListener {
            binding.mcbYes.isChecked = false
            binding.mcbNo.isChecked = true
            kycMerchantRegistrationFragmentVM.mcbPosterValue = AppConstants.NO
        }

        LocationListenerClass(
            requireActivity(), this
        ).permissions()

        setUpObserver()

    }

    private fun setUpObserver() {
        kycMerchantRegistrationFragmentVM.state.observe(viewLifecycleOwner){
            when(it){
                is KycMerchantRegistrationFragmentVM.KycMerchantRegistration.Error -> {
                    Utility.showToast("There is some error while processing your request. Please try again later...")
                }

                KycMerchantRegistrationFragmentVM.KycMerchantRegistration.Loading -> {}

                is KycMerchantRegistrationFragmentVM.KycMerchantRegistration.Success -> {
                    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        Utility.showToast("Shop details saved successfully")
                        kycMerchantRegistrationFragmentVM.openPhotoUpload()
                    }
                }

            }
        }

        kycMerchantRegistrationFragmentVM.event.observe(viewLifecycleOwner){
            when(it){
                KycMerchantRegistrationFragmentVM.KycMerchantRegistrationEvent.OpenPhotoUpload -> {
                    gotoNextScreen()
                }
            }
        }
    }

    private fun gotoNextScreen() {
        val bundle = Bundle()
        bundle.putParcelable("shop_data", kycMerchantRegistrationFragmentVM.saveShopDetailsRequest)
        findNavController().navigate(R.id.navigation_photo_upload_kyc, bundle, navOptions {
            anim {
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_righ
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
            }
        })
    }

    private fun checkAllFields() : Boolean {
        return (binding.etName.text.toString().isNotEmpty() && binding.etBusinessName.text.toString().isNotEmpty() && binding.etAddress.text.toString().isNotEmpty()
                && binding.etPinCode.text.toString().isNotEmpty() && binding.etPinCode.text?.trim()?.length == 6 && binding.etCity.text.toString().isNotEmpty() && binding.etState.text.toString().isNotEmpty() && kycMerchantRegistrationFragmentVM.mcbPosterValue != null)
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_kyc_merchant_registration

    override fun getViewModel(): KycMerchantRegistrationFragmentVM = kycMerchantRegistrationFragmentVM

    override fun onTryAgainClicked() {
    }

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {
        kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.latitude = latitude.toString()
        kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.longitude = Longitude.toString()
    }

}
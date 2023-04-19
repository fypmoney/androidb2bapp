package com.fypmoney.view.kycagent.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentKycMerchantRegistrationBinding
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.KycMerchantRegistrationFragmentVM
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.toolbar.*


class KycMerchantRegistrationFragment : BaseFragment<FragmentKycMerchantRegistrationBinding, KycMerchantRegistrationFragmentVM>(){

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

        trackr {
            it.name = TrackrEvent.signup_shop_details_view
        }

        binding.btnAddWithdrawSavings.setOnClickListener {
            if (checkAllFields()){
                trackr {
                    it.name = TrackrEvent.signup_shop_details_submit
                }
//                gotoNextScreen()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.agentName = binding.etName.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.addr1 = binding.etAddress.text?.trim().toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.agentContact1 = Utility.getCustomerDataFromPreference()?.mobile.toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.shopName = binding.etBusinessName.text?.trim().toString()
//                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.pincode = binding.etPinCode.text?.trim().toString()
//                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.city = binding.etCity.text?.trim().toString()
//                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.state = kycMerchantRegistrationFragmentVM.stateDelegate.getValue()
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

//        LocationListenerClass(
//            requireActivity(), this
//        ).permissions()

        setUpObserver()
        //setupStateDropDown()
        shopLocationPlacesWork()


    }

    fun hideSoftKeyboard() {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun shopLocationPlacesWork() {
        val key = "AIzaSyBSaXkJ_-0oPoKvBgk-6fySZx_QM_uXlbo"

        if (!Places.isInitialized()) {
            Places.initialize(PockketApplication.instance, key)
        }

        // Initialize Autocomplete Fragments
        // from the main activity layout file
        val autocompleteSupportFragment1 = childFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?

        val searchView = requireActivity().findViewById<AppCompatEditText>(R.id.places_autocomplete_search_input)
//
        searchView.hint = "Type to search"
        searchView.setPadding(60, 60 , 60, 60)
        searchView.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey))
        searchView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        (requireActivity().findViewById<AppCompatImageButton>(R.id.places_autocomplete_search_button)).visibility =
            View.GONE

        // Information that we wish to fetch after typing
        // the location and clicking on one of the options
        autocompleteSupportFragment1!!.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.ADDRESS_COMPONENTS
            )
        )

//        autocompleteSupportFragment1.setTypesFilter(mutableListOf("school", "primary_school", "secondary_school"))
        autocompleteSupportFragment1.setCountries("IN")

        // Display the fetched information after clicking on one of the options
        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Utility.showToast("Status: $p0")
            }

            override fun onPlaceSelected(place: Place) {

                // Text view where we will
                // append the information that we fetch

                hideSoftKeyboard()
                // Information about the place
                val name = place.name
                val address = place.address
                val latlng = place.latLng
                val latitude = latlng?.latitude
                val longitude = latlng?.longitude

                val placesList = place.addressComponents?.asList()

                placesList?.forEach {
                    kycMerchantRegistrationFragmentVM.mapOfAddress[it.types[0]] = it.name
                }

                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.latitude = latitude.toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.longitude = longitude.toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.state =
                    kycMerchantRegistrationFragmentVM.mapOfAddress["administrative_area_level_1"].toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.city =
                    kycMerchantRegistrationFragmentVM.mapOfAddress["administrative_area_level_2"].toString()
                kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.pincode =
                    kycMerchantRegistrationFragmentVM.mapOfAddress["postal_code"].toString()

//                val nameAddress = "$name, $address"
//                autocompleteSupportFragment1.setText(nameAddress)

                binding.tvLocationText.toVisible()
                binding.tvLocationValue.toVisible()
                binding.tvLocationValue.text = address


            }
        })

    }


//    private fun setupStateDropDown(){
//        val items = listOf("Andhra Pradesh",
//            "Andaman and Nicobar Islands",
//            "Arunachal Pradesh",
//            "Assam",
//            "Bihar",
//            "Chandigarh",
//            "Chhattisgarh",
//            "Dadar and Nagar Haveli",
//            "Daman and Diu",
//            "Delhi",
//            "Lakshadweep",
//            "Puducherry",
//            "Goa",
//            "Gujarat",
//            "Haryana",
//            "Himachal Pradesh",
//            "Jammu and Kashmir",
//            "Jharkhand",
//            "Karnataka",
//            "Kerala",
//            "Madhya Pradesh",
//            "Maharashtra",
//            "Manipur",
//            "Meghalaya",
//            "Mizoram",
//            "Nagaland",
//            "Odisha",
//            "Punjab",
//            "Rajasthan",
//            "Sikkim",
//            "Tamil Nadu",
//            "Telangana",
//            "Tripura",
//            "Uttar Pradesh",
//            "Uttarakhand",
//            "West Bengal")
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
//        (binding.actState as? AutoCompleteTextView)?.setAdapter(adapter)
//    }

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
            popUpTo(R.id.navigation_kyc_agent){
                inclusive = false
            }
        })
    }

    private fun checkAllFields() : Boolean {
        return (binding.etName.text.toString().isNotEmpty() && binding.etBusinessName.text.toString().isNotEmpty() && binding.etAddress.text.toString().isNotEmpty()
                && kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.pincode.isNotEmpty() && kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.pincode.length == 6 && kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.city.isNotEmpty()
                && (kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.state.isNotEmpty()) && kycMerchantRegistrationFragmentVM.mcbPosterValue != null)
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_kyc_merchant_registration

    override fun getViewModel(): KycMerchantRegistrationFragmentVM = kycMerchantRegistrationFragmentVM

    override fun onTryAgainClicked() {
    }

//    override fun getCurrentLocation(
//        isInternetConnected: Boolean?,
//        latitude: Double,
//        Longitude: Double
//    ) {
//        kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.latitude = latitude.toString()
//        kycMerchantRegistrationFragmentVM.saveShopDetailsRequest.longitude = Longitude.toString()
//    }

}
package com.fypmoney.view.kycagent.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentTakeHandFingerBinding
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.viewmodel.TakeHandFingerFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class TakeHandFingerFragment : BaseFragment<FragmentTakeHandFingerBinding, TakeHandFingerFragmentVM>() {

    private lateinit var binding: FragmentTakeHandFingerBinding
    private val takeHandFingerFragmentVM by viewModels<TakeHandFingerFragmentVM> { defaultViewModelProviderFactory }
    private val TAG = TakeHandFingerFragment::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        if (arguments?.containsKey("via") == true) {
            takeHandFingerFragmentVM.via = arguments?.getString("via").toString()
        }

        if (arguments?.containsKey("mobileNumber") == true && arguments?.getString("mobileNumber") != null) {
            takeHandFingerFragmentVM.customerNumber =
                arguments?.getString("mobileNumber").toString()
        }else{
            takeHandFingerFragmentVM.customerNumber = Utility.getCustomerDataFromPreference()?.mobile
        }

        takeHandFingerFragmentVM.customerAadhaarNumber = arguments?.getString("aadhaarNumber")

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = if (takeHandFingerFragmentVM.via == "UserKyc") "Complete Full KYC" else "Become a Fyp Agent!",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        takeHandFingerFragmentVM.deviceDetails = arguments?.getParcelable("DeviceData")

        takeHandFingerFragmentVM.deviceName = takeHandFingerFragmentVM.deviceDetails?.deviceManufactureName
        setUpObserver()

        setupHandDropDown()
        setupFingreDropDown()

        binding.btnMobileNumberProceed.setOnClickListener {
            takeHandFingerFragmentVM.onContinueClick()
//            captureMantraFigure()
//            if (takeHandFingerFragmentVM.handDelegate.isValid.value == true && takeHandFingerFragmentVM.fingreDelegate.isValid.value == true){
//                captureMantraFigure()
//            }
        }

    }

    private fun setupHandDropDown(){
        val items = listOf("Left", "Right")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        (binding.actSelectHand as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupFingreDropDown(){
        val items = listOf("Thumb Finger", "Index Finger","Middle Finger","Ring Finger","Little Finger")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        (binding.actSelectFingure as? AutoCompleteTextView)?.setAdapter(adapter)
    }

//    <Demo></Demo> <CustOpts></CustOpts>
//    <Demo></Demo> <CustOpts></CustOpts>
//    <Demo></Demo> <CustOpts></CustOpts>

    private fun captureMorphoFigure(){
        val intent = Intent("in.gov.uidai.rdservice.fp.CAPTURE")
        intent.setPackage("com.scl.rdservice")
        intent.putExtra("PID_OPTIONS", "<PidOptions ver=\"1.0\"><Opts env = \"P\" fCount=\"1\" fType=\"2\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\"/></PidOptions>")
        startActivityForResult(intent,1002)
    }
    private fun captureMantraFigure(){
        val intent = Intent("in.gov.uidai.rdservice.fp.CAPTURE")
        intent.setPackage("com.mantra.rdservice")
        intent.putExtra("PID_OPTIONS", "<PidOptions ver=\"1.0\"><Opts env = \"P\" fCount=\"1\" fType=\"2\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\"/></PidOptions>")
        startActivityForResult(intent,1002)
    }
    private fun captureStarTekFigure(){
        val intent = Intent("in.gov.uidai.rdservice.fp.CAPTURE")
        intent.setPackage("com.acpl.registersdk")
        intent.putExtra("PID_OPTIONS", "<PidOptions ver=\"1.0\"><Opts env = \"P\" fCount=\"1\" fType=\"2\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\"/></PidOptions>")
        startActivityForResult(intent,1002)
    }

    private fun setUpObserver() {
        takeHandFingerFragmentVM.kycEvent.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelEvent(it: TakeHandFingerFragmentVM.FillKycEvent?) {
        when(it){
            TakeHandFingerFragmentVM.FillKycEvent.CaptureFingrePrint -> {
                if(takeHandFingerFragmentVM.deviceName!!.startsWith("Startek Eng-Inc",false)){
                    captureStarTekFigure()
                }else if(takeHandFingerFragmentVM.deviceName == "MANTRA"){
                    captureMantraFigure()
                }else if(takeHandFingerFragmentVM.deviceName == "Morpho"){
                    captureMorphoFigure()
                }
            }
            null -> {

            }
            is TakeHandFingerFragmentVM.FillKycEvent.FullKycCompleted -> {
                val bundle = Bundle()
                bundle.putString("message", it.message)
                bundle.putString("via", takeHandFingerFragmentVM.via)
                findNavController().navigate(R.id.navigation_kyc_requested_submitted, bundle, navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                    popUpTo(R.id.navigation_kyc_agent){
                        inclusive = false
                    }
                } )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1002) {
                val bundle = data?.extras
                val pid = bundle?.getString("PID_DATA", "")
                val dnc = bundle?.getString("DNC", "")
                val dnr = bundle?.getString("DNR", "")
                if (pid!!.isNotEmpty()) {
                    Log.d(TAG, "Pid Info : ${pid}")
                    val result = takeHandFingerFragmentVM.parseXml(pid)
                    when (result) {
                        TakeHandFingerFragmentVM.CaptureFingerStatus.CaptureFingerQualityIsNotGood -> {
                            takeHandFingerFragmentVM.alertDialog.postValue(
                                DialogUtils.AlertStateUiModel(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_error_alert
                                    )!!,
                                    message = "Finger quality is not good.Please clean device and finger and try Again",
                                    backgroundColor = ContextCompat.getColor(
                                        requireContext(),
                                        R.color.errorAlertBgColor
                                    )
                                )
                            )
                        }
                        TakeHandFingerFragmentVM.CaptureFingerStatus.CaptureFingerQualityIsPoor -> {
                            takeHandFingerFragmentVM.alertDialog.postValue(
                                DialogUtils.AlertStateUiModel(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_error_alert
                                    )!!,
                                    message = "Finger quality is very poor.Please clean device and finger and try again",
                                    backgroundColor = ContextCompat.getColor(
                                        requireContext(),
                                        R.color.errorAlertBgColor
                                    )
                                )
                            )
                        }
                        TakeHandFingerFragmentVM.CaptureFingerStatus.UnableToCaptureFinger -> {
                            takeHandFingerFragmentVM.alertDialog.postValue(
                                DialogUtils.AlertStateUiModel(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_error_alert
                                    )!!,
                                    message = "unable to scan finger.try Again...",
                                    backgroundColor = ContextCompat.getColor(
                                        requireContext(),
                                        R.color.errorAlertBgColor
                                    )
                                )
                            )
                        }
                        is TakeHandFingerFragmentVM.CaptureFingerStatus.CapturedSuccessFully -> {
                            Log.d(TAG, "Captured successfully")
                            Utility.showToast("Finger Captured successfully")
                            takeHandFingerFragmentVM.postKycData(capturedInfo = takeHandFingerFragmentVM.convertPidDataIntoBase64(result.pidOptions))
                        }
                        is TakeHandFingerFragmentVM.CaptureFingerStatus.ErrorInCaptureFinger -> {
                            takeHandFingerFragmentVM.alertDialog.postValue(
                                DialogUtils.AlertStateUiModel(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_error_alert
                                    )!!,
                                    message = result.message,
                                    backgroundColor = ContextCompat.getColor(
                                        requireContext(),
                                        R.color.errorAlertBgColor
                                    )
                                )
                            )
                        }
                    }
                } else if (dnc!!.isEmpty()) {
                    Log.d(TAG, "Device not connected")
                    takeHandFingerFragmentVM.alertDialog.postValue(
                        DialogUtils.AlertStateUiModel(
                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_error_alert
                            )!!,
                            message = "Device not connected",
                            backgroundColor = ContextCompat.getColor(
                                requireContext(),
                                R.color.errorAlertBgColor
                            )
                        )
                    )

                } else if (dnr!!.isEmpty()) {
                    Log.d(TAG, "Device not registered")
                    takeHandFingerFragmentVM.alertDialog.postValue(
                        DialogUtils.AlertStateUiModel(
                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_error_alert
                            )!!,
                            message = "Device not registered",
                            backgroundColor = ContextCompat.getColor(
                                requireContext(),
                                R.color.errorAlertBgColor
                            )
                        )
                    )
                }

            }

        } else {
            takeHandFingerFragmentVM.alertDialog.postValue(
                DialogUtils.AlertStateUiModel(
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_alert)!!,
                    message = "No Device Attached",
                    backgroundColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.errorAlertBgColor
                    )
                )
            )
        }
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_take_hand_finger

    override fun getViewModel(): TakeHandFingerFragmentVM = takeHandFingerFragmentVM

    override fun onTryAgainClicked() {}

}
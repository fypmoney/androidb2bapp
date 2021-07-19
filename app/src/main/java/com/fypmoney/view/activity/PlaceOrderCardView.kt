package com.fypmoney.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewPlaceCardBinding
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.PriceBreakupBottomSheet
import com.fypmoney.viewmodel.PlaceOrderCardViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_order_card.*
import java.util.*


/*
* This class is used to order card
* */
class PlaceOrderCardView : BaseActivity<ViewPlaceCardBinding, PlaceOrderCardViewModel>(),
    LocationListenerClass.GetCurrentLocationListener {
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
        setToolbarAndTitle(
            context = this@PlaceOrderCardView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )

        setObservers()
    }

    fun setObservers() {
        mViewModel.onPriceBreakupClicked.observe(this)
        {
            if (it) {
                callPriceBreakupBottomSheet()
                mViewModel.onPriceBreakupClicked.value = false
            }
        }

        mViewModel.onPlaceOrderClicked.observe(this)
        {
            if (it) {
                askForDevicePassword()
                mViewModel.onPlaceOrderClicked.value = false
            }
        }
        mViewModel.onOrderCardSuccess.observe(this)
        {
            intentToActivity(TrackOrderView::class.java)

        }
        mViewModel.onUseLocationClicked.observe(this)
        {
            if (it) {
                LocationListenerClass(
                    this, this
                ).permissions()
                mViewModel.onUseLocationClicked.value = false
            }
        }
    }

    /*
   * This method is used to call card settings
   * */
    private fun callPriceBreakupBottomSheet() {
        val bottomSheet =
            PriceBreakupBottomSheet(mViewModel.amount.get(), mViewModel.productResponse.value)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "PlaceOrderBottomSheet")
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        Handler(Looper.getMainLooper()).post {
                            mViewModel.callPlaceOrderApi()

                        }

                    }

                }
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@PlaceOrderCardView, aClass))
        finish()
    }

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {

        val geocoder = Geocoder(this, Locale.getDefault())

        val addresses = geocoder.getFromLocation(
            latitude,
            Longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address: String =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        mViewModel.isProgressBarVisible.set(false)
        mViewModel.pin.value = addresses[0].postalCode
  /*      mViewModel.pin.value = addresses[0].locality
        mViewModel.pin.value = addresses[0].adminArea
*/      //  mViewModel.pin.value = addresses[0].postalCode
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied

            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    LocationListenerClass(
                        this, this
                    ).permissions()
                } else {
                    //set to never ask again


                }
            }
        }
    }
}

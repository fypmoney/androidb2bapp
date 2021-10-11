package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewHomeBinding
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.model.NotificationModel
import com.fypmoney.model.SendMoneyResponseDetails
import com.fypmoney.model.UpdateTaskGetResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.INSUFFICIENT_ERROR_CODE
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.AddMoneySuccessBottomSheet
import com.fypmoney.view.fragment.*
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.interfaces.MessageSubmitClickListener
import com.fypmoney.viewmodel.HomeViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.view_home.*
import java.util.concurrent.atomic.AtomicBoolean
import com.facebook.appevents.AppEventsLogger;
import java.lang.RuntimeException


class HomeView : BaseActivity<ViewHomeBinding, HomeViewModel>(),
    Utility.OnAllContactsAddedListener, FamilyNotificationBottomSheet.OnBottomSheetClickListener,
    RequestMoneyBottomSheet.OnRequestMoneyBottomSheetClickListener,
    LocationListenerClass.GetCurrentLocationListener {
    private var actionAllowed: String? = null
    private var startingPosition  = 0
    private var commentstr: String? = null
    private var choresModel: NotificationModel.NotificationResponseDetails? = null
    private var bottomsheetInsufficient: TaskMessageInsuficientFuntBottomSheet? = null
    private var bottomSheetMessage2: TaskMessageBottomSheet2? = null
    private var taskMessageBottomSheet3: TaskActionBottomSheetnotification? = null
    private var bottomSheetMessage: TaskMessageBottomSheet3? = null
    private var deviceSecurityAskedFor:String? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var mViewBinding: ViewHomeBinding
    private val doubleBackPressed = AtomicBoolean(false)

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    companion object {
        lateinit var mViewModel: HomeViewModel
    }
    override fun getLayoutId(): Int {
        return R.layout.view_home
    }

    override fun getViewModel(): HomeViewModel {
        mViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setObserver()
        checkAndAskPermission()
        loadFragment(HomeScreen(),1)
        mFirebaseAnalytics =  FirebaseAnalytics.getInstance(applicationContext)
        mFirebaseAnalytics!!.logEvent("Home_Screen",null)
        logSentFriendRequestEvent()


        LocationListenerClass(
            this, this
        ).permissions()

        mViewBinding.navigationView.itemIconTintList = null;

        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {
            AppConstants.STAY_TUNED_BOTTOM_SHEET -> {
                setupFamily()

            }
            AppConstants.NOTIFICATION -> {
                mViewModel.callGetFamilyNotificationApi(intent.getStringExtra(AppConstants.NOTIFICATION_APRID))
            }
            AppConstants.CardScreen->{
                setupcard()
            }
            AppConstants.StoreScreen->{
                setupStore()
            }
            AppConstants.FEEDSCREEN->{
                setupFeeds()
            }
            AppConstants.FyperScreen->{
                setupFamily()
            }

        }

        mViewBinding.navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setupHome()
                }
                R.id.feeds -> {
                    setupFeeds()
                }
                R.id.store -> {
                    setupStore()
                }
                R.id.card -> {
                    setupcard()
                }
                R.id.family -> {
                    setupFamily()
                }

            }
            true
        }
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    fun logSentFriendRequestEvent() {
        AppEventsLogger.newLogger(applicationContext).logEvent("home_screen")
    }
    private fun setupFamily() {
        loadFragment(FamilySettingsView(), 5)
        mViewBinding.navigationView.menu.findItem(R.id.family).isChecked = true;
        mViewBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
        mViewModel.headerText.set(
            getString(
                R.string.family_settings_toolbar_heading
            )
        )
    }

    private fun setupcard() {
        loadFragment(CardScreen(), 3)
        mViewBinding.navigationView.menu.findItem(R.id.card).isChecked = true;
        mViewBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon)
        mViewModel.headerText.set(getString(R.string.card_details_title))
    }

    private fun setupStore() {
        loadFragment(StoreScreen(), 4)
        mViewBinding.navigationView.menu.findItem(R.id.store).isChecked = true;
        mViewBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
        mViewModel.headerText.set(getString(R.string.store_bottom_nav_title))
    }

    private fun setupFeeds() {
        loadFragment(UserFeedsView(), 2)
        mViewBinding.navigationView.menu.findItem(R.id.feeds).isChecked = true;

        mViewBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        mViewBinding.toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon_black)
        mViewModel.headerText.set(getString(R.string.feeds_bottom_nav_title))
    }

    private fun setupHome() {
        loadFragment(HomeScreen(), 1)
        mViewBinding.navigationView.menu.findItem(R.id.home).isChecked = true;
        mViewBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        mViewBinding.toolbarTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.text_color_dark
            )
        )
        mViewBinding.ivNotificationBell.setImageResource(R.drawable.ic_bell_icon)
        mViewModel.headerText.set("")
    }

    override fun onResume() {
        super.onResume()
        loadProfile(
            SharedPrefUtils.getString(
                applicationContext,
                SharedPrefUtils.SF_KEY_PROFILE_IMAGE
            )
        )
    }

    override fun onStart() {
        super.onStart()
        showNewMessage()

    }

    private fun showNewMessage() {
        if (SharedPrefUtils.getString(
                applicationContext,
                SharedPrefUtils.SF_KEY_NEW_MESSAGE
            ).isNullOrEmpty()
        ) {
            mViewBinding.newNotification.visibility = View.GONE
        } else {
            mViewBinding.newNotification.visibility = View.VISIBLE
        }
    }

    private fun loadProfile(url: String?) {
        url?.let {
            loadImage(
                mViewBinding.myProfile,
                it,
                ContextCompat.getDrawable(this, R.drawable.ic_profile_img),
                true
            )

        }
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onFeedClicked.observe(this) {
            if (it) {
                intentToActivity(UserFeedsView::class.java)
                mViewModel.onFeedClicked.value = false
            }
        }

        mViewModel.sendMoneyApiResponse.observe(this) {
            callTransactionSuccessBottomSheet(it)
        }
        mViewModel.onProfileClicked.observe(this) {
            if (it) {
                intentToActivity(UserProfileView::class.java)
                mViewModel.onProfileClicked.value = false
            }
        }
        mViewModel.onQrCodeClicked.observe(this) {
            if (it) {
                intentToActivity(QrCodeScannerView::class.java)
                mViewModel.onQrCodeClicked.value = false
            }
        }
        mViewModel.onNotificationClicked.observe(this) {
            if (it) {
                intentToActivity(NotificationView::class.java)
                mViewModel.onNotificationClicked.value = false
            }
        }

        mViewModel.onNotificationListener.observe(this) {
            when (it?.requestCategoryCode) {
                AppConstants.NOTIFICATION_TYPE_ADD_FAMILY -> {
                    callBottomSheet(it)
                }
                AppConstants.NOTIFICATION_TYPE_REQUEST_MONEY -> {
                    callRequestMoneyBottomSheet()
                }
                AppConstants.NOTIFICATION_TYPE_ADD_TASK -> {

                    if (it.actionAllowed == "REJECT,ACCEPT" || it.actionAllowed == "CANCEL" || it.actionAllowed == "COMPLETE" || it.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY" || it.actionAllowed == "") {

                        if (it.actionAllowed == "" && it.requestStatus == "DEPRECIATE") {
                            callTaskMessageSheet(it)
                        } else {
                            callTaskActionSheet(it)
                        }
                    }
                }
            }

        }
        mViewModel.error.observe(this, { list ->
            if (list == INSUFFICIENT_ERROR_CODE) {
                callInsuficientFundMessageSheet(Utility.convertToRs(mViewModel.amountToBeAdded))
            }
        })
        mViewModel.bottomSheetStatus.observe(this, androidx.lifecycle.Observer { list ->
            bottomSheetMessage?.dismiss()
            taskMessageBottomSheet3?.dismiss()
            if (list.currentState == "ACCEPT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "REJECT") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "CANCEL") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "COMPLETE") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "DEPRECIATE") {

                callTaskMessageSheet(list)
            }
            if (list.currentState == "APPRECIATEANDPAY") {

                callTaskMessageSheet(list)
            }
        })

    }
    private fun callInsuficientFundMessageSheet(amount:String?) {
        var itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                bottomsheetInsufficient?.dismiss()
                intentToPayActivity(ContactListView::class.java, AppConstants.PAY)
            }

            override fun onRejectClicked(pos: Int) {
                bottomsheetInsufficient?.dismiss()
                callActivity(AddMoneyView::class.java, amount)
            }

            override fun ondimiss() {

            }
        }
        bottomsheetInsufficient =
            TaskMessageInsuficientFuntBottomSheet(itemClickListener2,title = resources.getString(R.string.insufficient_bank_balance),
                subTitle =  resources.getString(R.string.insufficient_bank_body),
                amount = resources.getString(R.string.add_money_title1)+resources.getString(R.string.Rs)+amount)
        bottomsheetInsufficient?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomsheetInsufficient?.show(supportFragmentManager, "TASKMESSAGE")
    }
    private fun intentToPayActivity(aClass: Class<*>, pay: String? = null) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, pay)
        startActivity(intent)
    }


    private fun callActivity(aClass: Class<*>, amount: String?) {
        val intent = Intent(this, aClass)
        intent.putExtra("amountshouldbeadded", amount)
        startActivity(intent)
    }
    private fun callTaskMessageSheet(list: NotificationModel.NotificationResponseDetails?) {
        var itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {
                bottomSheetMessage?.dismiss()
            }

        }
        bottomSheetMessage =
            TaskMessageBottomSheet3(itemClickListener2, list, list?.entityId)
        bottomSheetMessage?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage?.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun callTaskMessageSheet(list: UpdateTaskGetResponse?) {
        var itemClickListener2 = object : MessageSubmitClickListener {
            override fun onSubmit() {
                bottomSheetMessage2?.dismiss()
            }

        }
        bottomSheetMessage2 =
            TaskMessageBottomSheet2(itemClickListener2, list, list?.id?.toString())
        bottomSheetMessage2?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage2?.show(supportFragmentManager, "TASKMESSAGE")
    }


    private fun callTaskActionSheet(list: NotificationModel.NotificationResponseDetails?) {
        var itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                if (pos == 56) {
                    commentstr = str
                    choresModel = list
                    deviceSecurityAskedFor  = FOR_APPRICATE_AND_PAY
                    askForDevicePassword()
                }


            }

            override fun onRejectClicked(pos: Int) {


            }

            override fun ondimiss() {
                taskMessageBottomSheet3?.dismiss()
            }
        }
        taskMessageBottomSheet3 =
            TaskActionBottomSheetnotification(itemClickListener2, list)
        taskMessageBottomSheet3?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        taskMessageBottomSheet3?.show(supportFragmentManager, "TASKACCEPTREJECT")
    }
    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@HomeView, aClass))
    }

    /*
 * This method is used to check for permissions
 * */
    private fun checkAndAskPermission() {
        when (checkPermission(Manifest.permission.READ_CONTACTS)) {
            true -> {
                Utility.getAllContactsInList(
                    contentResolver,
                    this,
                    contactRepository = mViewModel.contactRepository
                )
            }
            else -> {
                requestPermission(Manifest.permission.READ_CONTACTS)
            }
        }
        when (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            false -> {
                mViewModel.postLatlong("","",SharedPrefUtils.getLong(
                    application, key = SharedPrefUtils.SF_KEY_USER_ID
                ))
            }
            else -> {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        when(deviceSecurityAskedFor) {
                            FOR_APPRICATE_AND_PAY-> {
                                if (commentstr == null) {
                                    commentstr = ""
                                }
                                mViewModel!!.callTaskAccept(
                                    "APPRECIATEANDPAY", choresModel?.entityId.toString(), commentstr!!

                                )
                            }
                            FOR_REQUEST_AND_PAY->{
                                mViewModel.callPayMoneyApi(actionAllowed!!)                            }

                        }

                    }

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                requestPermission(Manifest.permission.READ_CONTACTS)
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    var list = Utility.getAllContactsInList(
                        contentResolver,
                        this,
                        contactRepository = mViewModel.contactRepository
                    )

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

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_righ)
            replace(R.id.container, fragment)
            commit()
        }

    private fun loadFragment(fragment: Fragment?, newPosition: Int): Boolean {
        if (fragment != null) {
            if (newPosition == 0) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment).commit()
            }
            if (startingPosition > newPosition) {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_righ)
                    .replace(R.id.container, fragment).commit()
            }
            if (startingPosition < newPosition) {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.container, fragment).commit()
            }
            startingPosition = newPosition
            return true
        }
        return false
    }

    /*
  * This method is used to call leave member
  * */
    private fun callBottomSheet(notificationResponse: NotificationModel.NotificationResponseDetails?) {
        val bottomSheet =
            FamilyNotificationBottomSheet(
                notificationResponse?.actionAllowed,
                notificationResponse?.description,
                notificationResponse?.isApprovalProcessed, notificationResponse,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "FamilyNotification")
    }


    override fun onBottomSheetButtonClick(actionAllowed: String?) {
        mViewModel.callUpdateApprovalRequestApi(actionAllowed!!)


    }


    /*
   * This method is used to call leave member
   * */
    private fun callRequestMoneyBottomSheet() {
        val bottomSheet =
            RequestMoneyBottomSheet(
                response = mViewModel.notificationSelectedResponse,
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "RequestMoneyNotification")
    }

    private fun callTransactionSuccessBottomSheet(sendMoneyResponse: SendMoneyResponseDetails) {
        val bottomSheet =
            mViewModel.notificationSelectedResponse.additionalAttributes?.amount?.let {
                Utility.convertToRs(sendMoneyResponse.currentBalance)?.let { it1 ->
                    Utility.convertToRs(it)?.let { it2 ->
                        AddMoneySuccessBottomSheet(
                            it2,
                            it1,onViewDetailsClick=null,successTitle = "Payment Made Successfully to ${sendMoneyResponse.receiverName}",onHomeViewClick = {
                                intentToActivity(HomeView::class.java)
                            }
                        )
                    }
                }
            }
        bottomSheet?.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet?.show(supportFragmentManager, "TransactionSuccess")
    }

    override fun onRequestMoneyBottomSheetButtonClick(actionAllowed: String?) {
        deviceSecurityAskedFor  = FOR_REQUEST_AND_PAY
        this.actionAllowed = actionAllowed
        askForDevicePassword()


    }

    override fun onBackPressed() {

        if (doubleBackPressed.get()) {
            finish()
            return
        }
        doubleBackPressed.compareAndSet(false, true)
        Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackPressed.set(false) }, 2000)
    }

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {
        SharedPrefUtils.getLong(
            application, key = SharedPrefUtils.SF_KEY_USER_ID
        ).let {
            mViewModel.postLatlong("$latitude","$Longitude",it)
        }

    }

}


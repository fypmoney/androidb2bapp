package com.fypmoney.view.fragment

import android.animation.*
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenCardBinding
import com.fypmoney.model.UpDateCardSettingsRequest
import com.fypmoney.model.UpdateCardLimitRequest
import com.fypmoney.util.*
import com.fypmoney.util.AppConstants.CARD_TYPE_PHYSICAL
import com.fypmoney.util.AppConstants.NO
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.view.CardSettingClickListener
import com.fypmoney.view.activity.*
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.view.ordercard.OrderCardView
import com.fypmoney.view.setpindialog.SetPinDialogFragment
import com.fypmoney.view.setpindialog.SetPinFragmentVM
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.view.webview.WebViewActivity
import com.fypmoney.viewmodel.CardScreenViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.card_add_task.*
import kotlinx.android.synthetic.main.screen_card.*
import kotlinx.android.synthetic.main.screen_card.notify_btn
import kotlinx.android.synthetic.main.virtual_card_back_layout.*
import kotlinx.android.synthetic.main.virtual_card_front_layout.*


/**
 * This fragment is used for handling card
 */
class CardScreen : BaseFragment<ScreenCardBinding, CardScreenViewModel>(),
    MyProfileListAdapter.OnListItemClickListener,
    CardSettingsBottomSheet.OnCardSettingsClickListener,
    SetSpendingLimitBottomSheet.OnSetSpendingLimitClickListener, CardSettingClickListener,
    ManageChannelsBottomSheet.OnBottomSheetDismissListener,
    ActivateCardBottomSheet.OnActivateCardClickListener,
    SetOrChangePinBottomSheet.OnSetOrChangePinClickListener {
    private lateinit var mViewModel: CardScreenViewModel
    private lateinit var mViewBinding: ScreenCardBinding
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var mIsBackVisible = false
    lateinit var myProfileAdapter: MyProfileListAdapter
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_card
    }

    override fun getViewModel(): CardScreenViewModel {
        mViewModel = ViewModelProvider(this).get(CardScreenViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState)


    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        mViewBinding.fragment = this

        if(SharedPrefUtils.getString(
                requireContext(),
                SharedPrefUtils.SF_KEY_CARD_FLAG
            )=="1"){
            showNotifyCardLayout()
        }else{
            mViewBinding.notifyOrderCardNsv.visibility = View.GONE
            showCardLayout()
        }

    }

    override fun onStart() {
        super.onStart()
        mViewModel.callGetWalletBalanceApi()
        mViewModel.callGetBankProfileApi()
    }
    private fun showCardLayout() {
        val textString = ArrayList<String>()
        textString.add(PockketApplication.instance.getString(R.string.card_settings))
        textString.add(PockketApplication.instance.getString(R.string.order_card))
        textString.add(PockketApplication.instance.getString(R.string.account_stmt))
        val drawableIds = ArrayList<Int>()
        drawableIds.add(R.drawable.ic_card_settings)
        drawableIds.add(R.drawable.ic_order_card)
        drawableIds.add(R.drawable.ic_account_statement)


        myProfileAdapter = MyProfileListAdapter(requireContext(), this)
        list.adapter = myProfileAdapter
        myProfileAdapter.setList(
            iconList1 = drawableIds,
            textString
        )
        setObservers()
        loadAnimations()
        changeCameraDistance()
        val behavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(mViewBinding.clBottomsheet)
        BottomSheetBehavior.from<ConstraintLayout>(mViewBinding.clBottomsheet)
        behavior.state =
            BottomSheetBehavior.STATE_COLLAPSED
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mViewBinding.upIv.rotation = 270.0f
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mViewBinding.upIv.rotation = 90.0f
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        /*up_iv.setOnClickListener {

        }*/

        front_fl.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeTop() {
            }

            override fun onSwipeRight() {
                flipCardRight()
            }


            override fun onSwipeLeft() {
                flipCardLeft()

            }

            override fun onSwipeBottom() {
            }

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
        back_fl.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeTop() {
            }

            override fun onSwipeRight() {
                flipCardRight()
            }


            override fun onSwipeLeft() {
                flipCardLeft()

            }

            override fun onSwipeBottom() {
            }

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
    }

    private fun showNotifyCardLayout() {
        val uri: Uri =
            Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.notify_order_card)
        mViewBinding.video.setMediaController(null)
        mViewBinding.video.setVideoURI(uri)
        mViewBinding.video.setOnPreparedListener {
            it.isLooping = true
            mViewBinding.video.start()
        }
        notify_btn.setOnClickListener {
            Utility.showToast(resources.getString(R.string.thanks_we_will_keep_you_notify))
        }
        mViewBinding.notifyOrderCardNsv.visibility = View.VISIBLE
    }


    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mViewModel.onViewDetailsClicked.observe(viewLifecycleOwner) {
            if (it) {
                askForDevicePassword()
                mViewModel.onViewDetailsClicked.value = false
            }
        }
        mViewModel.onBankProfileSuccess.observe(viewLifecycleOwner) {
            if (it) {
                for(cardInfo in mViewModel.bankProfileResponse.get()?.cardInfos!!){
                    when(cardInfo.cardType){
                        CARD_TYPE_PHYSICAL->{
                            if(cardInfo.kitNumber.isNullOrEmpty()){
                                val textString = ArrayList<String>()
                                textString.add(PockketApplication.instance.getString(R.string.card_settings))
                                textString.add(PockketApplication.instance.getString(R.string.order_card))
                                textString.add(PockketApplication.instance.getString(R.string.account_stmt))
                                val drawableIds = ArrayList<Int>()
                                drawableIds.add(R.drawable.ic_card_settings)
                                drawableIds.add(R.drawable.ic_order_card)
                                drawableIds.add(R.drawable.ic_account_statement)
                                myProfileAdapter.setList(drawableIds,textString)
                            }
                            if(!cardInfo.kitNumber.isNullOrEmpty()){
                                val textString = ArrayList<String>()
                                textString.add(PockketApplication.instance.getString(R.string.card_settings))
                                textString.add(PockketApplication.instance.getString(R.string.track_order))
                                textString.add(PockketApplication.instance.getString(R.string.account_stmt))
                                val drawableIds = ArrayList<Int>()
                                drawableIds.add(R.drawable.ic_card_settings)
                                drawableIds.add(R.drawable.ic_order_card)
                                drawableIds.add(R.drawable.ic_account_statement)
                                myProfileAdapter.setList(drawableIds,textString)
                            }
                            if(cardInfo.isCardActivationAllowed==YES){
                                val textString = ArrayList<String>()
                                textString.add(PockketApplication.instance.getString(R.string.activate_card_heading))
                                textString.add(PockketApplication.instance.getString(R.string.card_settings))
                                textString.add(PockketApplication.instance.getString(R.string.track_order))
                                textString.add(PockketApplication.instance.getString(R.string.account_stmt))
                                val drawableIds = ArrayList<Int>()
                                drawableIds.add(R.drawable.ic_activate)
                                drawableIds.add(R.drawable.ic_card_settings)
                                drawableIds.add(R.drawable.ic_order_card)
                                drawableIds.add(R.drawable.ic_account_statement)
                                myProfileAdapter.setList(drawableIds,textString)
                            }
                            if(cardInfo.status==AppConstants.ENABLE){
                                val textString = ArrayList<String>()
                                textString.add(PockketApplication.instance.getString(R.string.card_settings))
                                textString.add(PockketApplication.instance.getString(R.string.account_stmt))
                                val drawableIds = ArrayList<Int>()
                                drawableIds.add(R.drawable.ic_card_settings)
                                drawableIds.add(R.drawable.ic_account_statement)
                                myProfileAdapter.setList(drawableIds,textString)
                            }
                        }
                    }
                }
                mViewModel.onBankProfileSuccess.value = false
            }

        }



        mViewModel.onGetCardDetailsSuccess.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.onGetCardDetailsSuccess.value = false
            }
        }
        mViewModel.onActivateCardInit.observe(viewLifecycleOwner) {
            if (it) {
                val setPinFragment = SetPinDialogFragment(setPinClickListener = {
                    mViewModel.callSetOrChangeApi()
                })
                setPinFragment.show(childFragmentManager,"set pin")
                mViewModel.onActivateCardInit.value = false

            }
        }

        mViewModel.onSetPinSuccess.observe(viewLifecycleOwner) {
            it.url.let {
                intentToActivity(
                    SetPinView::class.java,
                    type = AppConstants.SET_PIN_URL,
                    url = it
                )
            }
        }
    }

    override fun onTryAgainClicked() {
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                            mViewModel.callGetVirtualRequestApi()
                    }

                }
            }
        }
    }

    private fun changeCameraDistance() {
        val distance = 8000
        val scale: Float = resources.displayMetrics.density * distance
        mCardFrontLayout!!.cameraDistance = scale
        mCardBackLayout!!.cameraDistance = scale
    }

    private fun loadAnimations() {
        mSetRightOut =
            AnimatorInflater.loadAnimator(context, R.animator.out_animation) as AnimatorSet
        mSetLeftIn = AnimatorInflater.loadAnimator(context, R.animator.in_animation) as AnimatorSet
    }

    fun flipCardRight() {
        if (!mIsBackVisible) {
            mViewModel.isBackVisible.set(true)
            mSetRightOut!!.setTarget(mCardFrontLayout)
            mSetLeftIn!!.setTarget(mCardBackLayout)
            mSetRightOut!!.start()
            mSetLeftIn!!.start()
            mIsBackVisible = true
            val handler = Handler()
            handler.postDelayed({
                mViewModel.isFrontVisible.set(false)
            }, 100)

        }else{
            mViewModel.isFrontVisible.set(true)
            mSetLeftIn!!.setTarget(mCardFrontLayout)
            mSetRightOut!!.setTarget(mCardBackLayout)
            mSetLeftIn!!.start()
            mSetRightOut!!.start()
            mIsBackVisible = false

        }
    }
    fun flipCardLeft() {
        if (!mIsBackVisible) {
            mViewModel.isBackVisible.set(true)
            mSetRightOut!!.setTarget(mCardFrontLayout)
            mSetLeftIn!!.setTarget(mCardBackLayout)
            mSetLeftIn!!.start()
            mSetRightOut!!.start()
            mIsBackVisible = true
            val handler = Handler()
            handler.postDelayed({
                mViewModel.isFrontVisible.set(false)
            }, 100)

        }else{
            mViewModel.isFrontVisible.set(true)
            mSetLeftIn!!.setTarget(mCardFrontLayout)
            mSetRightOut!!.setTarget(mCardBackLayout)
            mSetLeftIn!!.start()
            mSetRightOut!!.start()
            mIsBackVisible = false
        }
    }

    /*
    * This method is used to copy the text to clipboard
    * */
    fun onCopyClicked() {
        Utility.copyTextToClipBoard(
            requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
            mViewModel.cardNumber.get()
        )
    }



    private fun intentToActivity(aClass: Class<*>, type: String? = null, url: String? = null) {
        val intent = Intent(requireActivity(), aClass)
        if (type == AppConstants.SET_PIN_URL) {
            intent.putExtra(AppConstants.SET_PIN_URL, url)
        }
        requireContext().startActivity(intent)
    }

    /*
    * This method is used to call card settings
    * */
    private fun callCardSettingsBottomSheet() {
        val bottomSheet =
            CardSettingsBottomSheet(mViewModel.bankProfileResponse.get(),this)
        bottomSheet.show(childFragmentManager, "CardSettings")
    }

    /*
   * This method is used to call set or change pin
   * */
    private fun callSetPinBottomSheet() {
        val bottomSheet =
            SetOrChangePinBottomSheet(this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "SetPin")
    }

    /*
   * This method is used to call card block / unblock card
   * */
    private fun callCardBlockUnblockBottomSheet() {
        val bottomSheet =
            BlockUnblockCardBottomSheet(mViewModel.bankProfileResponse.get()?.cardInfos, this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "BlockUnblockCard")
    }

    /*
   * This method is used to call set spending limit fragment
   * */
    private fun callSetSpendingLimitSheet() {
        val bottomSheet =
            SetSpendingLimitBottomSheet(mViewModel.bankProfileResponse.get(), this, this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "SetSpendingLimit")
    }

    /*
   * This method is used to call card block / unblock card
   * */
    private fun callManageChannelsBottomSheet() {
        val bottomSheet =
            ManageChannelsBottomSheet(mViewModel.bankProfileResponse.get()?.cardInfos, this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "ManageChannels")
    }

    /*
   * This method is used to call set spending limit fragment
   * */
    private fun callActivateCardSheet() {
        val bottomSheet =
            ActivateCardBottomSheet(this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "ActivateCard")
    }


    override fun onCardSettingsClick(position: Int) {
        when (position) {
            0 -> {
                callCardBlockUnblockBottomSheet()
            }
            1 -> {
                callSetSpendingLimitSheet()
            }
            2 -> {
                callManageChannelsBottomSheet()
            }
            3 -> {
                callSetPinBottomSheet()
            }
        }
    }

    override fun onSetSpendingLimitButtonClick(updateCardLimitRequest: UpdateCardLimitRequest) {
        mViewModel.callUpdateCardLimitApi(updateCardLimitRequest)

    }

    override fun onCardSettingClick(
        type: String,
        upDateCardSettingsRequest: UpDateCardSettingsRequest,
        fourDigitNumber: String?
    ) {
        when (type) {
            AppConstants.BLOCK_CARD_ACTION -> {
                mViewModel.callCardSettingsUpdateApi(upDateCardSettingsRequest)
            }
            getString(R.string.activate_card_heading) -> {
                mViewModel.callActivateCardApi(fourDigitNumber)

            }
        }
    }

    /*
    * This method is used to show or hide cvv
    * */
    fun onCvvEyeClicked() {
        when (mViewModel.isCvvVisible.get()) {
            false -> {
                cvvValue.transformationMethod = HideReturnsTransformationMethod.getInstance()
                mViewModel.isCvvVisible.set(true)
                mViewModel.cvv.set(mViewModel.cvvNumber)

            }
            true -> {

                cvvValue.transformationMethod = AsteriskPasswordTransformationMethod()
                mViewModel.isCvvVisible.set(false)
            }

        }

    }

    override fun onBottomSheetDismiss() {
        mViewModel.callGetBankProfileApi()

    }

    override fun onActivateCardClick(kitFourDigit: String?) {
        mViewModel.callActivateCardApi(kitFourDigit)

    }

    override fun onPrivacyPolicyTermsClicked(title: String, url: String) {
        openWebPageFor(title,url)
    }

    private fun openWebPageFor(title: String, url: String) {
        val intent = Intent(requireActivity(), WebViewActivity::class.java)
        intent.putExtra(ARG_WEB_URL_TO_OPEN, url)
        intent.putExtra(ARG_WEB_PAGE_TITLE, title)
        startActivity(intent)
    }


    /**
     * Method to navigate to the Feeds screen after login
     */
    private fun goToEnterOtpScreen(kitFourDigit: String?) {
        val intent = Intent(requireContext(), EnterOtpView::class.java)
        intent.putExtra(
            AppConstants.MOBILE_TYPE,
            ""
        )
        intent.putExtra(
            AppConstants.FROM_WHICH_SCREEN, AppConstants.ACTIVATE_CARD
        )

        intent.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            ""
        )

        intent.putExtra(
            AppConstants.KYC_ACTIVATION_TOKEN, ""

        )
        intent.putExtra(
            AppConstants.KIT_FOUR_DIGIT, kitFourDigit

        )
        startActivity(intent)
    }

    override fun setPinClick() {
        mViewModel.callSetOrChangeApi()
    }

    override fun onItemClick(position: Int, name: String?) {
        when (name) {
            PockketApplication.instance.getString(R.string.card_settings) -> {
                callCardSettingsBottomSheet()
            }
            PockketApplication.instance.getString(R.string.order_card) -> {
                intentToActivity(OrderCardView::class.java)
            }
            PockketApplication.instance.getString(R.string.track_order)->{
                SharedPrefUtils.getString(requireContext(),SharedPrefUtils.SF_KEY_KIT_NUMBER).let {
                    if(!it.isNullOrEmpty()){
                        intentToActivity(TrackOrderView::class.java)
                    }
                }
            }
            PockketApplication.instance.getString(R.string.account_stmt) -> {
                intentToActivity(BankTransactionHistoryView::class.java)
            }
            PockketApplication.instance.getString(R.string.activate_card_heading) -> {
                callActivateCardSheet()
            }

        }

    }

}

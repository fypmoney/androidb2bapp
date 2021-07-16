package com.fypmoney.view.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenCardBinding
import com.fypmoney.model.UpDateCardSettingsRequest
import com.fypmoney.model.UpdateCardLimitRequest
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.CardSettingClickListener
import com.fypmoney.view.activity.BankTransactionHistoryView
import com.fypmoney.view.activity.EnterOtpView
import com.fypmoney.view.activity.OrderCardView
import com.fypmoney.view.activity.TrackOrderView
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.viewmodel.CardScreenViewModel
import kotlinx.android.synthetic.main.screen_card.*
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
    ActivateCardBottomSheet.OnActivateCardClickListener {
    private lateinit var mViewModel: CardScreenViewModel
    private lateinit var mViewBinding: ScreenCardBinding
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var mIsBackVisible = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        mViewBinding.fragment = this

        val textString = ArrayList<String>()
        textString.add(PockketApplication.instance.getString(R.string.card_settings))
        when (mViewModel.isOrderCard.get()) {
            true -> {
                textString.add(PockketApplication.instance.getString(R.string.order_card))
            }
            else -> {
                textString.add(PockketApplication.instance.getString(R.string.track_order))
            }
        }

        textString.add(PockketApplication.instance.getString(R.string.account_stmt))


        val drawableIds = ArrayList<Int>()

        drawableIds.add(R.drawable.lock)
        drawableIds.add(R.drawable.order)
        drawableIds.add(R.drawable.transaction)

        val myProfileAdapter = MyProfileListAdapter(requireContext(), this)
        list.adapter = myProfileAdapter
        myProfileAdapter.setList(
            iconList1 = drawableIds,
            textString
        )
        setObservers()
        loadAnimations()
        changeCameraDistance()


    }

    override fun onResume() {
        super.onResume()
        mViewModel.callGetWalletBalanceApi()
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

        mViewModel.onActivateCardClicked.observe(viewLifecycleOwner) {
            if (it) {
                callActivateCardSheet()
                mViewModel.onActivateCardClicked.value = false
            }
        }

        mViewModel.onGetCardDetailsSuccess.observe(viewLifecycleOwner) {
            if (it) {
                flipCard()
                mViewModel.onGetCardDetailsSuccess.value = false
            }
        }
        mViewModel.onActivateCardInit.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.onActivateCardInit.value = false
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
                        Handler(Looper.getMainLooper()).post(Runnable {
                            mViewModel.callGetVirtualRequestApi()

                        })

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

    fun flipCard() {
        if (!mIsBackVisible) {
            mSetRightOut!!.setTarget(mCardFrontLayout)
            mSetLeftIn!!.setTarget(mCardBackLayout)
            mSetRightOut!!.start()
            mSetLeftIn!!.start()
            mViewModel.isCardDetailsVisible.set(true)
            mIsBackVisible = true
            btnViewDetails.visibility = View.GONE
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

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                callCardSettingsBottomSheet()
            }
            1 -> {
                when (mViewModel.isOrderCard.get()) {
                    true -> {
                        intentToActivity(OrderCardView::class.java)
                    }
                    else -> {
                        intentToActivity(TrackOrderView::class.java)
                    }
                }
            }
            2 -> {
                intentToActivity(BankTransactionHistoryView::class.java)
            }

        }
    }

    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(requireActivity(), aClass)
        requireContext().startActivity(intent)
    }

    /*
    * This method is used to call card settings
    * */
    private fun callCardSettingsBottomSheet() {
        val bottomSheet =
            CardSettingsBottomSheet(this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "CardSettings")
    }

    /*
   * This method is used to call set or change pin
   * */
    private fun callSetPinBottomSheet() {
        val bottomSheet =
            SetOrChangePinBottomSheet()
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "SetPin")
    }

    /*
   * This method is used to call card block / unblock card
   * */
    private fun callCardBlockUnblockBottomSheet() {
        val bottomSheet =
            BlockUnblockCardBottomSheet(mViewModel.bankProfileResponse.get()?.cardInfos, this, this)
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
                mViewModel.callSetOrChangeApi()
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
                mViewModel.callActivateCardInitApi()

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

            }
            true -> {

                cvvValue.transformationMethod = PasswordTransformationMethod.getInstance()
                mViewModel.isCvvVisible.set(false)
            }

        }

    }

    override fun onBottomSheetDismiss() {
        mViewModel.callGetBankProfileApi()

    }

    override fun onActivateCardClick(kitFourDigit: String?) {
        mViewModel.callPhysicalCardInitApi()
        goToEnterOtpScreen(kitFourDigit)
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

}

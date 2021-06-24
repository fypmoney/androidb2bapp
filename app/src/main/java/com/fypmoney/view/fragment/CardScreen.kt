package com.fypmoney.view.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.BankTransactionHistoryView
import com.fypmoney.view.adapter.CardListViewAdapter
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
    CardSettingsBottomSheet.OnCardSettingsClickListener {
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
        textString.add(PockketApplication.instance.getString(R.string.order_card))
        textString.add(PockketApplication.instance.getString(R.string.account_stmt))
        textString.add(PockketApplication.instance.getString(R.string.set_limit))

        val drawableIds = ArrayList<Int>()

        drawableIds.add(R.drawable.lock)
        drawableIds.add(R.drawable.order)
        drawableIds.add(R.drawable.transaction)
        drawableIds.add(R.drawable.set_up_limit)

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

        mViewModel.onGetCardDetailsSuccess.observe(viewLifecycleOwner) {
            if (it) {
                flipCard()
                mViewModel.onGetCardDetailsSuccess.value = false
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
        val scale: Float = getResources().getDisplayMetrics().density * distance
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
   * This method is used to call card block / unblock card
   * */
    private fun callCardBlockUnblockBottomSheet() {
        val bottomSheet =
            BlockUnblockCardBottomSheet()
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "BlockUnblockCard")
    }

    override fun onCardSettingsClick(position: Int) {
        when (position) {
            0 -> {
                callCardBlockUnblockBottomSheet()
            }
        }
    }
}

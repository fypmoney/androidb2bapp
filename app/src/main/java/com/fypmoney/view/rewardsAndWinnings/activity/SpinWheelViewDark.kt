package com.fypmoney.view.rewardsAndWinnings.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSpinWheelBlackBinding
import com.fypmoney.model.SectionListItem
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.ErrorBottomSpinProductSheet
import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinWheelProductViewModel
import kotlinx.android.synthetic.main.dialog_cash_won.*
import kotlinx.android.synthetic.main.dialog_cash_won.better_next_time
import kotlinx.android.synthetic.main.dialog_cash_won.clicked
import kotlinx.android.synthetic.main.dialog_cash_won.luckonside_tv
import kotlinx.android.synthetic.main.dialog_cash_won.spin_green
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_spin_wheel_black.*

/*
* This class is used for spin the wheel
* */
class SpinWheelViewDark : BaseActivity<ViewSpinWheelBlackBinding, SpinWheelProductViewModel>(),
    ErrorBottomSpinProductSheet.OnSpinErrorClickListener {

    private var mp: MediaPlayer? = null
    private var noOfGoldenCard: Int? = null
    private var ProductCode: String? = null
    private var dialogError: Dialog? = null
    private var sectionId: Int? = null
    private var dialogDialog: Dialog? = null
    private var orderId: String? = null

    private lateinit var mViewModel: SpinWheelProductViewModel

    companion object {
        var sectionArrayList: ArrayList<SectionListItem> = ArrayList()
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_spin_wheel_black
    }

    override fun getViewModel(): SpinWheelProductViewModel {
        mViewModel = ViewModelProvider(this).get(SpinWheelProductViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogError = Dialog(this)

        val intent = intent
        orderId = intent.getStringExtra(AppConstants.ORDER_NUM)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, -1)
        ProductCode = intent.getStringExtra(AppConstants.PRODUCT_CODE)
        noOfGoldenCard = intent.getIntExtra(AppConstants.NO_GOLDED_CARD, -1)

//        val args = intent.getBundleExtra("BUNDLE")
//        val getList = args!!.getSerializable("ARRAYLIST") as ArrayList<SectionListItem>


        setObserver()
        setToolbarAndTitle(
            context = this@SpinWheelViewDark,
            toolbar = toolbar,
            isBackArrowVisible = false, titleColor = Color.WHITE
        )

//        Glide.with(applicationContext).load(R.raw.coin).into(coin)
        dialogDialog = Dialog(this)
        luckyWheelView.setRound(5)




        if (sectionArrayList.size == 0) {
            mViewModel.callProductsDetailsApi(orderId)
        } else {
            luckylayout.visibility = View.VISIBLE
            mViewModel.setDataInSpinWheel(sectionArrayList)
            luckyWheelView.setData(mViewModel.luckyItemList)
        }

        try {

            luckyWheelView.setLuckyRoundItemSelectedListener {

                sectionArrayList.forEach { item ->

                    if (item.id == sectionId.toString()) {

                            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                trackr {
                                    it.name = TrackrEvent.spin_success
                                    it.add(TrackrField.spin_product_code, ProductCode)

                                }
                                offer_found_tv?.visibility = View.VISIBLE
                                showwonDialog(item.sectionValue)
                                mp = MediaPlayer.create(
                                    this,
                                    R.raw.reward_won_sound
                                )
                                mp?.start()
                            }


                        return@forEach

                    }


                }


            }
        } catch (e: Exception) {

        }

        mViewModel.enableSpin.value = false

    }

    override fun onPause() {
        super.onPause()
    }

    private fun showwonDialog(sectionValue: String?) {


        dialogDialog?.setCancelable(false)
        dialogDialog?.setCanceledOnTouchOutside(false)
        dialogDialog?.setContentView(R.layout.dialog_cash_won)

        val wlp = dialogDialog?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT


        if (sectionValue == "0") {
            dialogDialog?.congrats_tv?.visibility = View.GONE
            dialogDialog?.textView?.visibility = View.GONE
            dialogDialog?.clicked?.text = getString(R.string.continue_txt)
            dialogDialog?.luckonside_tv?.visibility = View.GONE
            dialogDialog?.spin_green?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.better_luck_next_time
                )
            )
            dialogDialog?.spin_green?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.golden_cards
                )
            )

            dialogDialog?.better_next_time?.visibility = View.INVISIBLE
            if (noOfGoldenCard != null) {
                trackr {
                    it.name = TrackrEvent.ticket_win_success
                }
                dialogDialog!!.golden_cards_won!!.text =
                    getString(R.string.you_won_1) + " " + noOfGoldenCard + " " + getString(R.string.golden_card)
            }
            dialogDialog?.clicked?.text = getString(R.string.continue_txt)

        }
        if (mViewModel.played.get() == true) {
            dialogDialog?.clicked?.text = getString(R.string.continue_txt)
            dialogDialog?.textView?.text =
                getString(R.string.your_wallet_updated) + " " + Utility.convertToRs(sectionValue)

        } else {
            dialogDialog?.textView?.text =
                getString(R.string.your_wallet_will_updated) + " " + Utility.convertToRs(
                    sectionValue
                )
        }



        dialogDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog?.window?.attributes = wlp


        dialogDialog?.clicked?.setOnClickListener(View.OnClickListener {
            if (mViewModel.played.get() == true) {
                if (sectionValue == "0") {
                    trackr {

                        it.name = TrackrEvent.zero_spin
                        it.add(TrackrField.spin_product_code, ProductCode)

                    }
                }
                setResult(23)
                finish()
            } else {
                if (sectionValue == "0") {
                    trackr {

                        it.name = TrackrEvent.zero_spin
                        it.add(TrackrField.spin_product_code, ProductCode)

                    }
                }
                mViewModel.callSpinWheelApi(orderId)
            }

        })



        dialogDialog?.show()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.spinWheelResponseList.observe(this) {
            mp?.stop()
            setResult(23)
            finish()

        }
        mViewModel.redeemCallBackResponse.observe(this) {
            it.sectionList?.forEach { i ->
                if (i != null) {
                    sectionArrayList.add(i)
                }
            }
            ProductCode = it.code
            sectionId = it.sectionId
            noOfGoldenCard = it.noOfJackpotTicket

            luckylayout.visibility = View.VISIBLE
            mViewModel.setDataInSpinWheel(sectionArrayList)
            luckyWheelView.setData(mViewModel.luckyItemList)

        }

        mViewModel.onError.observe(this)
        {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                dialogDialog?.dismiss()
                callErrorDialog(it.msg)
            }

        }

        mViewModel.onPlayClicked.observe(this)
        {
            try {
                when (sectionId) {
                    1 -> {
                        luckyWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)

                    }
                    else -> {

                        luckyWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }


    }

    private fun callErrorDialog(msg: String) {
        dialogError?.setCancelable(false)
        dialogError?.setCanceledOnTouchOutside(false)
        dialogError?.setContentView(R.layout.dialog_rewards_error)

        val wlp = dialogError?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogError?.setCanceledOnTouchOutside(false)



        dialogError?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogError?.window?.attributes = wlp
        dialogError?.error_msg?.text = msg



        dialogError?.clicked?.setOnClickListener(View.OnClickListener {
//            mViewModel.callSpinWheelApi(orderId)
            setResult(23)
            finish()
        })


        dialogError?.show()
    }
    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(52)
    }




    override fun onSpinErrorClick(type: String) {

    }



}
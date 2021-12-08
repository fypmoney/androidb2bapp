package com.fypmoney.view.rewardsAndWinnings.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentSpinnerListBinding
import com.fypmoney.model.aRewardProductResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.activity.ScratchCardActivity
import com.fypmoney.view.rewardsAndWinnings.activity.SpinWheelViewDark
import com.fypmoney.view.adapter.ScratchItemAdapter
import com.fypmoney.view.adapter.SpinnerAdapter
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinWheelProductViewModel
import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinnerFragmentVM

import kotlinx.android.synthetic.main.dialog_burn_mynts.*
import kotlinx.android.synthetic.main.dialog_burn_mynts.clicked
import kotlinx.android.synthetic.main.dialog_burn_mynts.spin_green
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*


import kotlin.collections.ArrayList


class RewardsSpinnerListFragment : BaseFragment<FragmentSpinnerListBinding, SpinnerFragmentVM>() {
    companion object {
        var page = 0

    }

    private var mViewmodel: SpinnerFragmentVM? = null
    private var buyProductDialog: Dialog? = null
    private var itemsArrayList: ArrayList<aRewardProductResponse> = ArrayList()
    private var scratchArrayList: ArrayList<aRewardProductResponse> = ArrayList()

    private var spinnerAdapter: SpinnerAdapter? = null
    private var scratchAdapter: ScratchItemAdapter? = null
    private var mViewBinding: FragmentSpinnerListBinding? = null
    private var dialogInsufficientFund: Dialog? = null
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == 23) {
                mViewmodel?.totalmyntsClicked?.postValue(true)


            }

        }

    override fun getBindingVariable(): kotlin.Int {
        return BR.viewModel
    }

    override fun getLayoutId(): kotlin.Int {
        return R.layout.fragment_spinner_list
    }

    override fun getViewModel(): SpinnerFragmentVM {

        mViewmodel = ViewModelProvider(this).get(SpinnerFragmentVM::class.java)



        return mViewmodel!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        setRecyclerView()
        setRvScratchCard()
        dialogInsufficientFund = Dialog(requireContext())
        buyProductDialog = Dialog(requireContext())
        observeInput(mViewmodel!!)


    }


    override fun onTryAgainClicked() {

    }

    private fun callInsuficientFundDialog(msg: String) {
        dialogInsufficientFund?.setCancelable(false)
        dialogInsufficientFund?.setCanceledOnTouchOutside(false)
        dialogInsufficientFund?.setContentView(R.layout.dialog_rewards_insufficient)

        val wlp = dialogInsufficientFund?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogInsufficientFund?.setCanceledOnTouchOutside(false)




        dialogInsufficientFund?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogInsufficientFund?.window?.attributes = wlp
        dialogInsufficientFund?.error_msg?.text = msg



        dialogInsufficientFund?.clicked?.setOnClickListener(View.OnClickListener {


            trackr {

                it.name = TrackrEvent.insufficient_mynts

            }
            dialogInsufficientFund?.dismiss()
        })


        dialogInsufficientFund?.show()
    }

    private fun observeInput(mviewModel: SpinnerFragmentVM) {

        mViewmodel?.error?.observe(
            viewLifecycleOwner,
            { list ->
                if (list.errorCode == "PKT_2051") {

                    callInsuficientFundDialog(list.msg)
                }

            }
        )
        mViewmodel?.redeemproductDetails?.observe(this) {
            if (it != null) {
                mViewmodel?.redeemproductDetails?.postValue(null)
                Glide.with(this).asDrawable().load(it.scratchResourceHide)
                    .into(object : CustomTarget<Drawable?>() {

                        override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                        ) {
                            val intent =
                                Intent(requireContext(), ScratchCardActivity::class.java)
                            intent.putExtra(AppConstants.SECTION_ID, it.sectionId)
                            intent.putExtra(AppConstants.NO_GOLDED_CARD, it.noOfJackpotTicket)
                            it.sectionList?.forEachIndexed { pos, item ->
                                if (item != null) {
                                    ScratchCardActivity.sectionArrayList.add(item)
                                }
                            }
                            ScratchCardActivity.imageScratch = resource

                            intent.putExtra(
                                AppConstants.ORDER_NUM,
                                mViewmodel?.orderNumber?.value
                            )
                            intent.putExtra(
                                AppConstants.PRODUCT_HIDE_IMAGE,
                                it.scratchResourceShow
                            )
                            startActivity(intent)
                        }
                    })

            }


        }
        mviewModel.spinArrayList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->

                itemsArrayList.clear()
                mViewBinding?.shimmerLayout?.visibility = View.GONE
                mViewBinding?.shimmerLayout?.stopShimmer()

                itemsArrayList.addAll(list)
                spinnerAdapter?.notifyDataSetChanged()

            })
        mviewModel.error.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->

                if (list.errorCode == "PKT_2051") {
                    buyProductDialog?.dismiss()


                }

            }
        )
        mviewModel.scratchArrayList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                mViewBinding?.shimmerscratch?.visibility = View.GONE
                mViewBinding?.shimmerscratch?.stopShimmer()

                scratchArrayList.clear()

                scratchArrayList.addAll(list)
                scratchAdapter?.notifyDataSetChanged()

            })

        mviewModel.coinsBurned.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                buyProductDialog?.dismiss()
                if (list != null) {
                    mviewModel.coinsBurned.postValue(null)

                    when (mviewModel.clickedType.get()) {
                        AppConstants.PRODUCT_SPIN -> {
                            val intent = Intent(requireContext(), SpinWheelViewDark::class.java)
                            SpinWheelViewDark.sectionArrayList.clear()
                            intent.putExtra(AppConstants.ORDER_NUM, list.orderNo)
                            intent.putExtra(AppConstants.NO_GOLDED_CARD, list.noOfJackpotTicket)
                            intent.putExtra(AppConstants.SECTION_ID, list.sectionId)
                            intent.putExtra(
                                AppConstants.PRODUCT_CODE,
                                itemsArrayList[mviewModel.selectedPosition.get()!!].code
                            )


                            itemsArrayList[mviewModel.selectedPosition.get()!!].sectionList?.forEachIndexed { pos, item ->
                                if (item != null) {
                                    SpinWheelViewDark.sectionArrayList.add(item)
                                }
                            }
                            startForResult.launch(intent)

                        }
                        AppConstants.PRODUCT_SCRATCH -> {
                            Glide.with(this).asDrawable()
                                .load(scratchArrayList[mviewModel.selectedPositionScratch.get()!!].scratchResourceHide)
                                .into(object : CustomTarget<Drawable?>() {

                                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                                    }

                                    override fun onResourceReady(
                                        resource: Drawable,
                                        transition: Transition<in Drawable?>?
                                    ) {
                                        val intent =
                                            Intent(requireContext(), ScratchCardActivity::class.java)
                                        intent.putExtra(AppConstants.SECTION_ID, list.sectionId)
                                        intent.putExtra(
                                            AppConstants.NO_GOLDED_CARD,
                                            list.noOfJackpotTicket
                                        )
                                        intent.putExtra(
                                            AppConstants.PRODUCT_CODE,
                                            list.rewardProductCode
                                        )
                                        scratchArrayList[mviewModel.selectedPositionScratch.get()!!].sectionList?.forEachIndexed { pos, item ->
                                            if (item != null) {
                                                ScratchCardActivity.sectionArrayList.add(item)
                                            }
                                        }
                                        ScratchCardActivity.imageScratch = resource

                                        intent.putExtra(
                                            AppConstants.ORDER_NUM,
                                            list.orderNo
                                        )
                                        intent.putExtra(
                                            AppConstants.PRODUCT_HIDE_IMAGE,
                                            scratchArrayList[mviewModel.selectedPositionScratch.get()!!].scratchResourceShow
                                        )
                                        startForResult.launch(intent)
                                    }
                                })

                        }
                    }

                }


            })


    }

    internal fun showBurnDialog(
        i: kotlin.Int,
        type: String,
        appDisplayText: String?,
        detailResource: String?
    ) {

        buyProductDialog?.progress_image?.visibility = View.VISIBLE
        buyProductDialog?.setCancelable(true)
        buyProductDialog?.setCanceledOnTouchOutside(true)
        buyProductDialog?.setContentView(R.layout.dialog_burn_mynts)

        val wlp = buyProductDialog?.window!!.attributes

        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT

        buyProductDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        buyProductDialog?.spin_green?.let {
            Glide.with(requireContext()).load(detailResource)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        buyProductDialog?.progress_image?.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        buyProductDialog?.progress_image?.visibility = View.INVISIBLE

                        return false
                    }

                }).into(it)
        }

        if (appDisplayText != null) {
            buyProductDialog?.amount_to_enter?.text = appDisplayText
            buyProductDialog?.clicked?.text = "Burn $appDisplayText Mynts"

        }

        buyProductDialog?.window?.attributes = wlp
        when (type) {
            AppConstants.PRODUCT_SPIN -> {


                buyProductDialog?.textView?.text =
                    getString(R.string.will_be_deducted_from_your_balance_nto_spin_the_wheel)

            }
            AppConstants.PRODUCT_SCRATCH -> {
                buyProductDialog?.textView?.text =
                    getString(R.string.will_be_deducted_from_your_balance_scratch)

            }
        }


        buyProductDialog?.clicked?.setOnClickListener(View.OnClickListener {
            when (type) {
                AppConstants.PRODUCT_SPIN -> {
                    mViewmodel?.selectedPosition?.set(i)
                    mViewmodel?.clickedType?.set(AppConstants.PRODUCT_SPIN)
                    mViewmodel?.callRewardsRedeem(itemsArrayList[i].code)


                }
                AppConstants.PRODUCT_SCRATCH -> {
                    mViewmodel?.clickedType?.set(AppConstants.PRODUCT_SCRATCH)
                    mViewmodel?.selectedPositionScratch?.set(i)

                    mViewmodel?.callRewardsRedeem(scratchArrayList[i].code)


                }
            }

        })


        buyProductDialog?.show()
    }


    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.rvSpinner?.layoutManager = layoutManager


        val itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: kotlin.Int) {
                itemsArrayList[pos].code?.let { it1 ->
                    trackr {

                        it.name = TrackrEvent.spin
                        it.add(TrackrField.spin_product_code, it1)
                    }
                }


                showBurnDialog(
                        pos,
                        AppConstants.PRODUCT_SPIN,
                        itemsArrayList[pos].appDisplayText,
                        itemsArrayList[pos].detailResource
                    )


            }
        }

        spinnerAdapter = SpinnerAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        mViewBinding?.rvSpinner?.adapter = spinnerAdapter
        if (itemsArrayList.size > 0) {

            mViewBinding?.shimmerLayout?.visibility = View.GONE
            mViewBinding?.shimmerLayout?.stopShimmer()
        }

    }

    private fun setRvScratchCard() {
        val layoutManager =
            GridLayoutManager(requireContext(), 2)
        mViewBinding?.rvScratch?.layoutManager = layoutManager


        val itemClickListener2 = object : ListContactClickListener {
            override fun onItemClicked(pos: kotlin.Int) {
                scratchArrayList[pos].code?.let { it1 ->
                    trackr {

                        it.name = TrackrEvent.scratch
                        it.add(TrackrField.spin_product_code, it1)
                    }
                }

                showBurnDialog(
                    pos,
                        AppConstants.PRODUCT_SCRATCH,
                        scratchArrayList[pos].appDisplayText,
                        scratchArrayList[pos].detailResource
                    )


            }
        }

        scratchAdapter =
            ScratchItemAdapter(scratchArrayList, requireContext(), itemClickListener2!!)
        mViewBinding?.rvScratch!!.adapter = scratchAdapter
        if (scratchArrayList.size > 0) {
            mViewBinding?.shimmerscratch?.visibility = View.GONE
            mViewBinding?.shimmerscratch?.stopShimmer()
        }
    }




}
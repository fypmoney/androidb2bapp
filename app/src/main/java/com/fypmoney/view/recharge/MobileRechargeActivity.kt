package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.database.entity.ContactEntity

import com.fypmoney.databinding.ActivityMobileRechargeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ContactViewAddMember
import com.fypmoney.view.recharge.adapter.recentRechargeAdapter
import com.fypmoney.view.recharge.viewmodel.MobileRechargeViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.toolbar

/*
* This class is used as Home Screen
* */
class MobileRechargeActivity :
    BaseFragment<ActivityMobileRechargeBinding, MobileRechargeViewModel>() {
    private lateinit var mViewModel: MobileRechargeViewModel
    private lateinit var mViewBinding: ActivityMobileRechargeBinding
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mobile_recharge
    }

    override fun getViewModel(): MobileRechargeViewModel {
        mViewModel = ViewModelProvider(this).get(MobileRechargeViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,

            isBackArrowVisible = true, toolbarTitle = "Mobile Recharge"
        )
        setRecyclerView()
        setListners()
    }

    override fun onTryAgainClicked() {

    }

    private fun setListners() {
        mViewModel.opertaorList.observe(viewLifecycleOwner) {


            val directions =
                MobileRechargeActivityDirections.actionGoOperatorScreen(
                    circle = it.info?.circle,
                    operator = it.info?.operator
                )

            directions?.let { it1 -> findNavController().navigate(it1) }

        }

        mViewBinding.continueBtn.setOnClickListener {

            if (!mViewBinding.etNumber.text.isNullOrEmpty() && mViewBinding.etNumber.text.length == 10) {
                mViewModel.callGetMobileHrl(mViewBinding.etNumber.text.toString())
            } else {

                Utility.showToast("Enter correct number")

            }


        }

        mViewBinding.selectContact.setOnClickListener(View.OnClickListener {

            startActivityForResult(Intent(requireContext(), ContactViewAddMember::class.java), 13)
        })
        mViewBinding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewBinding.continueBtn.isEnabled = !s.isNullOrEmpty() && s.length <= 10
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val returnValue: ContactEntity? =
            data?.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE)
        if (requestCode == 13 && resultCode != AppCompatActivity.RESULT_CANCELED && returnValue != null) {

            mViewBinding.etNumber.setText(Utility.removePlusOrNineOneFromNo(returnValue.contactNumber))

        }


    }

    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.rvRecents?.layoutManager = layoutManager


        var recentrechargeAdapter = recentRechargeAdapter()
        mViewBinding?.rvRecents?.adapter = recentrechargeAdapter


    }


}

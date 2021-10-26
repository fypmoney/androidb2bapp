package com.fypmoney.view.rewardsAndWinnings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR

import com.fypmoney.base.BaseActivity

import com.fypmoney.view.fragment.*

import com.google.android.material.tabs.TabLayout

import java.util.ArrayList


import com.fypmoney.databinding.ViewRewardsBinding
import kotlinx.android.synthetic.main.toolbar.*

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.util.AppConstants
import com.fypmoney.view.rewardsAndWinnings.activity.ScratchCardActivity
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardHistoryFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsOverviewFragment
import com.fypmoney.view.rewardsAndWinnings.fragments.RewardsSpinnerListFragment
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*


class CashBackWonHistoryActivity : BaseActivity<ViewRewardsBinding, RewardsCashbackwonVM>() {


    var mViewModel: RewardsCashbackwonVM? = null

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.cash_back_won_history),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )


    }


    override fun onStart() {
        super.onStart()


    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_rewards
    }

    override fun getViewModel(): RewardsCashbackwonVM {
        mViewModel = ViewModelProvider(this).get(RewardsCashbackwonVM::class.java)
        setObserver(mViewModel!!)

        return mViewModel!!
    }

    private fun setObserver(mViewModel: RewardsCashbackwonVM) {

    }


}

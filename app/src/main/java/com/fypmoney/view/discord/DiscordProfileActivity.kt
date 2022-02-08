package com.fypmoney.view.discord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import android.graphics.Color
import com.fypmoney.databinding.ActivityDiscordProfileBinding

import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.discord.viewmodel.DiscordActivityVM

import kotlinx.android.synthetic.main.toolbar.*


class DiscordProfileActivity : BaseActivity<ActivityDiscordProfileBinding, DiscordActivityVM>() {
    private lateinit var mViewModel: DiscordActivityVM
    private lateinit var mViewBinding: ActivityDiscordProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewmodel = mViewModel
        setUpToolbar()
        setUpObserver()

    }

    private fun setUpObserver() {
        mViewModel.event.observe(this) {
            handelEvents(it)
        }
    }

    private fun handelEvents(it: DiscordActivityVM.DiscordEvent?) {
        when (it) {
            DiscordActivityVM.DiscordEvent.contect -> {

            }


        }
    }


    private fun setUpToolbar() {
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.fypxdiscord),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_discord_profile

    override fun getViewModel(): DiscordActivityVM {
        mViewModel = ViewModelProvider(this).get(DiscordActivityVM::class.java)
        return mViewModel
    }


}
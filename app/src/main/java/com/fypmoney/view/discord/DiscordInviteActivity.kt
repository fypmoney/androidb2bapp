package com.fypmoney.view.discord

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityDiscordBinding
import com.fypmoney.util.AppConstants.DISCORD_URL
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.discord.viewmodel.DiscordActivityVM
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*


class DiscordInviteActivity : BaseActivity<ActivityDiscordBinding, DiscordActivityVM>() {
    private lateinit var mViewModel: DiscordActivityVM
    private lateinit var mViewBinding: ActivityDiscordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewmodel = mViewModel
        setUpToolbar()
        setUpObserver()

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
    private fun setUpObserver() {
        mViewModel.event.observe(this) {
            handelEvents(it)
        }
        mViewModel.profileResponse.observe(this) {
            SharedPrefUtils.putString(
                application, key = SharedPrefUtils.SF_DICORD_CONNECTED,
                value = "connected"
            )
            val intent =
                Intent(this, DiscordProfileActivity::class.java)
            startActivity(intent)
            finish()


        }
    }

    private fun handelEvents(it: DiscordActivityVM.DiscordEvent?) {
        when (it) {
            DiscordActivityVM.DiscordEvent.ConnectNow -> {
                openDiscordCommunity()
            }
        }
    }

    private fun openDiscordCommunity() {
        val authToken = SharedPrefUtils.getString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
        )
        val url = authToken?.let { DISCORD_URL.replace("{authtoken}", it) }
        val intent = Intent(this, DiscordWebView::class.java)
        intent.putExtra(ARG_WEB_URL_TO_OPEN, url)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.putExtra(ARG_WEB_PAGE_TITLE, getString(R.string.connect_to_discord))
        startActivity(intent)
        finish()
    }



    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_discord

    override fun getViewModel(): DiscordActivityVM {
        mViewModel = ViewModelProvider(this).get(DiscordActivityVM::class.java)
        return mViewModel
    }


}
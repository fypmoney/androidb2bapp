package com.fypmoney.view.discord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity

import com.fypmoney.util.AppConstants

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.bumptech.glide.Glide
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.ActivityDiscordBinding
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.discord.viewmodel.DiscordActivityVM
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.view.webview.WebViewActivity
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

    private fun setUpObserver() {
        mViewModel.event.observe(this) {
            handelEvents(it)
        }
        mViewModel.profileResponse.observe(this) {
            SharedPrefUtils.putString(
                getApplication(), key = SharedPrefUtils.SF_DICORD_CONNECTED,
                value = "connected"
            )
            finish()
        }
    }

    private fun handelEvents(it: DiscordActivityVM.DiscordEvent?) {
        when (it) {
            DiscordActivityVM.DiscordEvent.contect -> {
                openCommunity(AppConstants.FACEBOOK_PAGE)
            }


        }
    }

    private fun openCommunity(url1: String) {
        var authToken = SharedPrefUtils.getString(
            PockketApplication.instance,
            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
        )
         var url =
            "https://discord.com/oauth2/authorize?response_type=code&client_id=945553921005457468&scope=connections%20email%20gdm.join%20guilds%20guilds.join%20guilds.members.read%20identify%20messages.read&state=$authToken&prompt=consent"

        val intent = Intent(this, DiscordWebView::class.java)
        intent.putExtra(ARG_WEB_URL_TO_OPEN, url)
        intent.putExtra(ARG_WEB_PAGE_TITLE, "Connect to Discord")
        startActivity(intent)

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

    override fun getLayoutId(): Int = R.layout.activity_discord

    override fun getViewModel(): DiscordActivityVM {
        mViewModel = ViewModelProvider(this).get(DiscordActivityVM::class.java)
        return mViewModel
    }


}
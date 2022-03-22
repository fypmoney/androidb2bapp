package com.fypmoney.view.discord

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityDiscordProfileBinding
import com.fypmoney.view.discord.adapter.RolesAdapter
import com.fypmoney.view.discord.viewmodel.DiscordProfileVM
import kotlinx.android.synthetic.main.toolbar.*


class DiscordProfileActivity : BaseActivity<ActivityDiscordProfileBinding, DiscordProfileVM>() {
    private lateinit var mViewModel: DiscordProfileVM
    private lateinit var mViewBinding: ActivityDiscordProfileBinding

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_discord_profile

    override fun getViewModel(): DiscordProfileVM {
        mViewModel = ViewModelProvider(this).get(DiscordProfileVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setUpRecyclerView()
        setUpToolbar()
        setUpObserver()

    }

    private fun setUpObserver() {

        mViewModel.profileResponse.observe(this) {

            mViewBinding.textView9.text = it.discordUserInfoDTO?.username

            mViewBinding.textView10.text = it.discordUserInfoDTO?.discriminator.toString()

            Glide.with(this).load(it.discordUserInfoDTO?.avatar)
                .into(mViewBinding.ivUserProfileImage)

            (mViewBinding.rvroles.adapter as RolesAdapter).submitList(it.discordGuildsInfoDTO?.get(0)?.roles)
        }
    }

    private fun setUpRecyclerView() {
        val topTenUsersAdapter = RolesAdapter(
            this, onRecentUserClick = {

            }
        )


        with(mViewBinding.rvroles) {
            adapter = topTenUsersAdapter
            layoutManager = GridLayoutManager(this@DiscordProfileActivity, 3)
        }
    }


    private fun setUpToolbar() {
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.fypxdiscord),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE,
        )
    }

}
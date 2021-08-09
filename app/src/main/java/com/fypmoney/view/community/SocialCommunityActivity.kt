package com.fypmoney.view.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivitySocialCommunityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.CommunityViewModel
import kotlinx.android.synthetic.main.activity_social_community.*
import android.content.Intent
import android.net.Uri


class SocialCommunityActivity : BaseActivity<ActivitySocialCommunityBinding,SocialCommunityActivityVM>() {
    private lateinit var mViewModel: SocialCommunityActivityVM
    private lateinit var mViewBinding: ActivitySocialCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewmodel = mViewModel
        setUpToolbar()
        setUpObserver()

    }

    private fun setUpObserver() {
        mViewModel.event.observe(this,{
            handelEvents(it)
        })
    }

    private fun handelEvents(it: SocialCommunityActivityVM.SocialCommunityEvent?) {
        when(it){
            SocialCommunityActivityVM.SocialCommunityEvent.JoinOnFacebook -> {
                openCommunity(AppConstants.FACEBOOK_PAGE)
            }
            SocialCommunityActivityVM.SocialCommunityEvent.JoinOnInstagram -> {
                openCommunity(AppConstants.INSTAGRAM_PAGE)

            }
            SocialCommunityActivityVM.SocialCommunityEvent.JoinOnYoutube -> {
                openCommunity(AppConstants.YOUTUBE_PAGE)
            }

        }
    }

    private fun openCommunity(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title= ""
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_social_community

    override fun getViewModel(): SocialCommunityActivityVM {
        mViewModel = ViewModelProvider(this).get(SocialCommunityActivityVM::class.java)
        return mViewModel
    }


}
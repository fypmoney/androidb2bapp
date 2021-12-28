package com.fypmoney.view.register


import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityWaitlistBinding
import com.fypmoney.view.register.viewModel.WaitingGiftVM
import kotlinx.android.synthetic.main.toolbar_animation.*

class WaitlistAprovalActivity : BaseActivity<ActivityWaitlistBinding, WaitingGiftVM>() {

    private lateinit var binding: ActivityWaitlistBinding
    private val homeActivityVM by viewModels<WaitingGiftVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = WaitlistAprovalActivity::class.java.simpleName

        )// lottie a

        binding.getCode.setOnClickListener {

            trackr {
                it.name = TrackrEvent.onboard_waitlist_have_number
            }
            val intent = Intent(this, InviteParentSiblingActivity::class.java)
            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                applicationContext,
                R.anim.slideinleft,
                R.anim.slideinright
            ).toBundle()
            startActivity(intent, bndlAnimation)
            finishAffinity()
        }

    }


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_waitlist

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): WaitingGiftVM = homeActivityVM


}
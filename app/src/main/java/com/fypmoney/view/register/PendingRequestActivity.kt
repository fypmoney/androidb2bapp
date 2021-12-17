package com.fypmoney.view.register

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.view.register.viewModel.PendingRequestVm
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import android.os.CountDownTimer
import android.view.View
import com.fypmoney.databinding.ActivityPendingApprovalBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ChooseInterestRegisterView
import kotlinx.android.synthetic.main.toolbar_animation.*
import java.lang.Exception


class PendingRequestActivity : BaseActivity<ActivityPendingApprovalBinding, PendingRequestVm>() {

    private var gotData: Boolean = false
    private var timer: CountDownTimer? = null
    private lateinit var binding: ActivityPendingApprovalBinding
    private val pendingRequestVM by viewModels<PendingRequestVm> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()


        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift
        )// lottie anima
        pendingRequestVM.callGetCustomerProfileApi()
        timer = object : CountDownTimer(5000, 10) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                try {
                    yourMethod()
                } catch (e: Exception) {

                }
            }
        }.start()
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
            binding.skip.visibility = View.VISIBLE
        }
        binding.skip.setOnClickListener(View.OnClickListener {
            gotData = true
            timer?.onFinish()
            timer?.cancel()
            val intent = Intent(this, ChooseInterestRegisterView::class.java)

            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                applicationContext,
                com.fypmoney.R.anim.slideinleft,
                com.fypmoney.R.anim.slideinright
            ).toBundle()
            startActivity(intent, bndlAnimation)
            finishAffinity()
        })
        binding.instaCv.setOnClickListener {
            openCommunity(AppConstants.INSTAGRAM_PAGE)
        }
        binding.youtubeCv.setOnClickListener {
            openCommunity(AppConstants.YOUTUBE_PAGE)

        }

        observeEvents()

    }

    override fun onStart() {
        super.onStart()
        timer?.start()
    }

    private fun openCommunity(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun observeEvents() {


        pendingRequestVM.user.observe(this, {


            if (it.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_APPROVED) {
                gotData = true
                timer?.onFinish()
                timer?.cancel()
                val intent = Intent(this, ChooseInterestRegisterView::class.java)

                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finish()


            } else if (it.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_DECLINED) {
                gotData = true
                timer?.onFinish()
                timer?.cancel()
                val intent = Intent(this, InviteParentSiblingActivity::class.java)

                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinright,
                    com.fypmoney.R.anim.slideinleft
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finish()
            }

        })
    }

    fun yourMethod() {
        if (!gotData) {
            pendingRequestVM.callGetCustomerProfileApi()
            timer?.start()
        }

    }


    override fun onStop() {
        super.onStop()
        timer?.onFinish()
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
    override fun getLayoutId(): Int = R.layout.activity_pending_approval

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): PendingRequestVm = pendingRequestVM


}
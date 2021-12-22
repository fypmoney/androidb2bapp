package com.fypmoney.view.register

import android.animation.Animator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityOpenGiftBinding
import com.fypmoney.view.register.viewModel.openGiftVM

import com.fypmoney.view.storeoffers.model.offerDetailResponse
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*
import kotlinx.android.synthetic.main.toolbar.*

class OpenGiftActivity : BaseActivity<ActivityOpenGiftBinding, openGiftVM>() {

    private lateinit var binding: ActivityOpenGiftBinding
    private val homeActivityVM by viewModels<openGiftVM> { defaultViewModelProviderFactory }
    private var itemsArrayList: ArrayList<offerDetailResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.giftBox.playAnimation()
        }, 300)

        binding.giftBox.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                Log.e("Animation:", "start")
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.e("Animation:", "end")

                val intent = Intent(this@OpenGiftActivity, YourGiftsActivity::class.java)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finishAffinity()

            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator?) {
                Log.e("Animation:", "repeat")
            }
        })

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
    override fun getLayoutId(): Int = R.layout.activity_open_gift

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): openGiftVM = homeActivityVM


}
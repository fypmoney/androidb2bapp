package com.fypmoney.view.register

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityInviteParentSiblingBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.dynamiclinks.DynamicLinksUtil
import com.fypmoney.view.activity.ChooseInterestRegisterView
import com.fypmoney.view.register.viewModel.InviteParentSiblingVM
import kotlinx.android.synthetic.main.toolbar_animation.*

class InviteParentSiblingActivity :
    BaseActivity<ActivityInviteParentSiblingBinding, InviteParentSiblingVM>() {


    private lateinit var binding: ActivityInviteParentSiblingBinding
    private val inviteParentSiblingVM by viewModels<InviteParentSiblingVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = InviteParentSiblingActivity::class.java.simpleName

        )// lottie anima
        observeEvents()

        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
            binding.skip.text = getString(R.string.skip_title)
            binding.questionRelation.text =
                "Is your child already on Fyp? Enter their registered mobile no."
            binding.textView2.text = "Don’t have your child on Fyp? Invite your child on this app"
            binding.shareInvite.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.sibling_invite
                )
            )

        } else {
            binding.questionRelation.text =
                "Don’t have a teenager sibling/ parent on Fyp? Invite them now!"
            binding.textView2.text =
                "You need a teenager sibling/parent to use Fyp. Enter their registered mobile no."
            binding.skip.text = getString(R.string.i_want_to_use)
            binding.shareInvite.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.sibling_parent
                )
            )
        }
        binding.shareInvite.setOnClickListener(View.OnClickListener {
//            Utility.getCustomerDataFromPreference()?.postKycScreenCode = "90"
            trackr {
                it.name = TrackrEvent.onboard_invite_share
            }
            shareUser()
        })
        binding.skip.setOnClickListener(View.OnClickListener {
            if (binding.skip.text == getString(R.string.i_want_to_use)) {
                trackr {
                    it.name = TrackrEvent.onboard_invite_use_myself
                }
                val intent = Intent(this, WaitlistAprovalActivity::class.java)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slideinleft,
                    R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finish()

            } else if (binding.skip.text == getString(R.string.skip_title)) {
                trackr {
                    it.name = TrackrEvent.onboard_invite_skip
                }
                val intent = Intent(this, ChooseInterestRegisterView::class.java)
                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slideinleft,
                    R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
                finish()

            }
        })

        binding.continueBtn.setOnClickListener {


            if (!binding.phoneEd.text.isNullOrEmpty()) {
                if (binding.phoneEd.text.toString().length != 10) {
                    Utility.showToast("Enter a valid number")
                } else {

                    trackr {
                        it.name = TrackrEvent.onboard_invite_enter_mobile
                        it.add(
                            TrackrField.post_kyc_flag, Utility.getCustomerDataFromPreference()?.postKycScreenCode)
                    }
                    inviteParentSiblingVM.callIsAppUserApi(binding.phoneEd.text.toString())

                }
            } else {
                Utility.showToast("Enter mobile number")
            }


        }


    }

    fun shareUser() {
        var content: String? = null
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null
            && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1"
        ) {
            if (!SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REGISTER_MSG_1
                ).isNullOrEmpty()
            ) {
                content = SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REGISTER_MSG_1
                )

            }

        }
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null
            && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "90"
        ) {
            if (!SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REGISTER_MSG_90
                ).isNullOrEmpty()
            ) {
                content = SharedPrefUtils.getString(
                    applicationContext,
                    SharedPrefUtils.SF_REGISTER_MSG_90
                )

            }

        }
        content?.let { onInviteUser(DynamicLinksUtil.getInviteLinkWithNoData(it)) }
    }


    private fun observeEvents() {

        inviteParentSiblingVM.user.observe(this) {

            if (it.isAppUserResponseDetails.isAppUser == true) {
                val intent = Intent(this, SelectRelationActivity::class.java)
                intent.putExtra("phone", binding.phoneEd.text.toString())
                intent.putExtra("name_user", it.isAppUserResponseDetails.name)
                intent.putExtra(AppConstants.USER_TYPE, it.isAppUserResponseDetails.name)


                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slideinleft,
                    R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)


            } else {
                Utility.showToast("Not a fyp user")
            }

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
    override fun getLayoutId(): Int = R.layout.activity_invite_parent_sibling

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): InviteParentSiblingVM = inviteParentSiblingVM


}
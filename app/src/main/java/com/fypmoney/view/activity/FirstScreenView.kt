package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewFirstScreenBinding
import com.fypmoney.util.textview.ClickableSpanListener
import com.fypmoney.util.textview.FYPClickableSpan
import com.fypmoney.viewmodel.FirstScreenViewModel


/*
* This class is used to handle school name city
* */
class FirstScreenView : BaseActivity<ViewFirstScreenBinding, FirstScreenViewModel>() {
    private lateinit var mViewBinding: ViewFirstScreenBinding
    private lateinit var mViewModel: FirstScreenViewModel
    //private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_first_screen
    }

    override fun getViewModel(): FirstScreenViewModel {
        mViewModel = ViewModelProvider(this).get(FirstScreenViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

       // val view = findViewById<View>(R.id.bottomSheet)
       // mBottomSheetBehavior = BottomSheetBehavior.from(view)
        //setBottomSheetAndCallBackBottomSheetBehaviour()

        showPrivacyPolicyAndTermsAndConditions()
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.event.observe(this) {
           handelEvents(it)

        }

    }

    private fun handelEvents(it: FirstScreenViewModel.FirstScreenEvent?) {
        when(it){
            FirstScreenViewModel.FirstScreenEvent.BackToLogin -> {
                intentToActivity()
            }
            FirstScreenViewModel.FirstScreenEvent.CreateAccount -> {
                intentToActivity()
            }
            else -> {}
        }
    }


    /*
   * navigate to the HomeScreen
   * */
    private fun intentToActivity() {
        val intent = Intent(this@FirstScreenView, LoginView::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * set bottom sheet behavior and state
     */
/*
    private fun setBottomSheetAndCallBackBottomSheetBehaviour() {
        //callback


        mBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    mViewBinding.ivLogoCollasped.visibility = View.VISIBLE
                    mViewBinding.image2.visibility = View.VISIBLE
                    mViewBinding.tvPull.visibility = View.VISIBLE
                    mViewBinding.llExpanededView.visibility = View.GONE
                }else if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    mViewBinding.ivLogoCollasped.visibility = View.GONE
                    mViewBinding.tvPull.visibility = View.GONE
                    mViewBinding.image2.visibility = View.GONE
                    mViewBinding.llExpanededView.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val upperState = 0.66
                val lowerState = 0.33
                if (mBottomSheetBehavior?.state == BottomSheetBehavior.STATE_SETTLING ) {
                    if(slideOffset >= upperState){
                        mBottomSheetBehavior?.isDraggable =false
                        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    if(slideOffset > lowerState && slideOffset < upperState){
                        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }
            }
        })
    }
*/

    private fun showPrivacyPolicyAndTermsAndConditions() {
        val text = resources.getString(R.string.create_account_terms,  resources.getString(R.string.terms_and_conditions),resources.getString(R.string.privacy_policy))
        val privacyPolicyText = resources.getString(R.string.privacy_policy)
        val tAndCText = resources.getString(R.string.terms_and_conditions)
        val privacyPolicyStarIndex = text.indexOf(privacyPolicyText)
        val privacyPolicyEndIndex = privacyPolicyStarIndex + privacyPolicyText.length
        val tAndCStartIndex = text.indexOf(tAndCText)
        val tAndCEndIndex = tAndCStartIndex + tAndCText.length
        val ss = SpannableString(text);
        ss.setSpan(

            FYPClickableSpan(pos = 1, clickableSpanListener = object : ClickableSpanListener {
                override fun onPositionClicked(pos: Int) {
                    openWebPageFor(getString(R.string.privacy_policy),"https://www.fypmoney.in/fyp/privacy-policy/")

                }
            }),
            privacyPolicyStarIndex,
            privacyPolicyEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            FYPClickableSpan(pos = 2, clickableSpanListener = object : ClickableSpanListener {
                override fun onPositionClicked(pos: Int) {
                    openWebPageFor(getString(R.string.terms_and_conditions),"https://www.fypmoney.in/fyp/terms-of-use/")
                }
            }),
            tAndCStartIndex,
            tAndCEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mViewBinding.tvTerms.text = ss
        mViewBinding.tvTerms.highlightColor = Color.TRANSPARENT
        mViewBinding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
    }


}

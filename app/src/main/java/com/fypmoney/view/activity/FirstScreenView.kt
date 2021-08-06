package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewFirstScreenBinding
import com.fypmoney.listener.OnSwipeTouchListener
import com.fypmoney.viewmodel.FirstScreenViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_first_screen.*


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
}

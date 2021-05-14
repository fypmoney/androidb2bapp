package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewFirstScreenBinding
import com.fypmoney.listener.OnSwipeTouchListener
import com.fypmoney.viewmodel.FirstScreenViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_first_screen.*


/*
* This class is used to handle school name city
* */
class FirstScreenView : BaseActivity<ViewFirstScreenBinding, FirstScreenViewModel>() {
    private lateinit var mViewModel: FirstScreenViewModel

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


        image2.setOnTouchListener(object : OnSwipeTouchListener(this@FirstScreenView) {
            override fun onSwipeTop() {
                mViewModel.isLogoVisible.set(false)
                Toast.makeText(this@FirstScreenView, "top", Toast.LENGTH_SHORT).show()

            }

        })
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

     mViewModel.onCreateAccountClicked.observe(this){
         if(it)
         {intentToActivity()
             mViewModel.onCreateAccountClicked.value=false
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

}

package com.fypmoney.view.activity


import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewUserLogsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.viewmodel.LogViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class LogView : BaseActivity<ViewUserLogsBinding, LogViewModel>(),
    MyProfileListAdapter.OnListItemClickListener {
    private lateinit var mViewModel: LogViewModel
    private lateinit var mViewBinding: ViewUserLogsBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_user_logs
    }

    override fun getViewModel(): LogViewModel {
        mViewModel = ViewModelProvider(this).get(LogViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        val myProfileAdapter = MyProfileListAdapter(applicationContext, this)
        mViewBinding.profileList.adapter = myProfileAdapter

        val iconList = ArrayList<Int>()


        val logList = ArrayList<String>()
        mViewModel.logRepository.getAllLogsFromDatabase()?.forEach {
            logList.add(it.methodName + "      " + it.methodValue+"        "+"Time:  "+it.timestamp)
            iconList.add(R.drawable.ic_profile)

        }

        myProfileAdapter.setList(
            iconList1 = iconList,
            logList
        )

        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@LogView, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.LOGOUT)
        startActivity(intent)
        finishAffinity()
    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToFamilySettingsActivity(aClass: Class<*>) {
        val intent = Intent(this@LogView, aClass)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        when (position) {

        }
    }

}

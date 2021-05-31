package com.fypmoney.view.activity

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.ActivityAddTaskBinding
import com.fypmoney.databinding.ViewAddMemberBinding
import com.fypmoney.databinding.ViewChoresBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.AcceptRejectTaskFragment
import com.fypmoney.viewmodel.AddTaskViewModel
import com.fypmoney.viewmodel.ChoresViewModel
import kotlinx.android.synthetic.main.toolbar.*

class AddTaskActivity : BaseActivity<ActivityAddTaskBinding, AddTaskViewModel>() {

    private lateinit var mViewModel: AddTaskViewModel
    private lateinit var mViewBinding: ActivityAddTaskBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_task
    }

    override fun getViewModel(): AddTaskViewModel {
        mViewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@AddTaskActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.chore_history_title)
        )

        setObserver()

       /* findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }

    private fun setObserver() {
        mViewModel.onFromContactClicked.observe(this) {
            if (it) {
                //intentToActivity(ContactView::class.java)
                mViewModel.onFromContactClicked.value = false
            }
        }
    }
}
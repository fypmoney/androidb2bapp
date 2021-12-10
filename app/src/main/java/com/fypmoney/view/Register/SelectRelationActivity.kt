package com.fypmoney.view.Register

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.ActivityHomeBinding
import com.fypmoney.databinding.ActivitySelectRelationshipBinding
import com.fypmoney.databinding.ActivityWaitlistBinding
import com.fypmoney.extension.onNavDestinationSelected
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.Register.viewModel.SelectRelationVM
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.adapter.AssignedTasksAdapter
import com.fypmoney.view.home.main.homescreen.viewmodel.HomeActivityVM
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*

class SelectRelationActivity : BaseActivity<ActivitySelectRelationshipBinding, SelectRelationVM>() {

    private lateinit var binding: ActivitySelectRelationshipBinding
    private val homeActivityVM by viewModels<SelectRelationVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        observeEvents()

        setRecyclerview()
        setRecyclerview()

    }

    private fun setRecyclerview() {
        val layoutManager = GridLayoutManager(this, 2)
        root.rv_assigned!!.layoutManager = layoutManager

        typeAdapter = AssignedTasksAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_assigned!!.adapter = typeAdapter
    }

    private fun observeEvents() {
        homeActivityVM.event.observe(this, {
            handelEvents(it)
        })
    }

    private fun handelEvents(it: SelectRelationVM.HomeActivityEvent?) {
        when (it) {
            SelectRelationVM.HomeActivityEvent.NotificationClicked -> {
                startActivity(Intent(this, NotificationView::class.java))
            }
            SelectRelationVM.HomeActivityEvent.ProfileClicked -> {
                startActivity(Intent(this, UserProfileView::class.java))
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
    override fun getLayoutId(): Int = R.layout.activity_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): SelectRelationVM = homeActivityVM


}
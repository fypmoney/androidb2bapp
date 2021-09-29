package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.FragmentOffersBinding
import com.fypmoney.databinding.FragmentStoreBinding
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.StoreDataModel
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsStoreAdapter

import com.fypmoney.view.adapter.YourTasksAdapter
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.view.offfers.OffersStoreFragmentVM
import com.fypmoney.viewmodel.NotificationViewModel
import com.fypmoney.viewmodel.StoreScreenViewModel

import kotlinx.android.synthetic.main.fragment_your_task.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


import kotlin.collections.ArrayList


class OffersStoreFragment : BaseFragment<FragmentOffersBinding, OffersStoreFragmentVM>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onTryAgainClicked() {

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_offers
    }

    override fun getViewModel(): OffersStoreFragmentVM {
        return ViewModelProvider(this).get(OffersStoreFragmentVM::class.java)
    }

}
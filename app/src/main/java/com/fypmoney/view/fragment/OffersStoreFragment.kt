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
import com.fypmoney.viewmodel.NotificationViewModel
import com.fypmoney.viewmodel.StoreScreenViewModel

import kotlinx.android.synthetic.main.fragment_your_task.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


import kotlin.collections.ArrayList


class OffersStoreFragment : BaseFragment<FragmentOffersBinding, StoreScreenViewModel>(),
    FeedsAdapter.OnFeedItemClickListener {
    companion object {
        var page = 0

    }

    private lateinit var sharedViewModel: StoreScreenViewModel
    private lateinit var mViewBinding: FragmentOffersBinding


    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: FeedsStoreAdapter? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onTryAgainClicked() {

    }


    override fun onFeedClick(position: Int, feedDetails: FeedDetails) {

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_offers
    }

    override fun getViewModel(): StoreScreenViewModel {

        parentFragment?.let {
            sharedViewModel = ViewModelProvider(it).get(StoreScreenViewModel::class.java)
        }

        return sharedViewModel
    }

}
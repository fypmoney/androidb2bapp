package com.fypmoney.view.recharge

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment
import com.fypmoney.base.PaginationListener
import com.fypmoney.databinding.FragmentStoreBinding
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.StoreDataModel
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsStoreAdapter

import com.fypmoney.view.adapter.YourTasksAdapter
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.viewmodel.NotificationViewModel
import com.fypmoney.viewmodel.StoreScreenViewModel

import kotlinx.android.synthetic.main.fragment_your_task.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


import kotlin.collections.ArrayList


class RechargeStoresFragment : BaseFragment<FragmentStoreBinding, StoreScreenViewModel>(),
    FeedsAdapter.OnFeedItemClickListener {

    private lateinit var sharedViewModel: StoreScreenViewModel
    private lateinit var mViewBinding: FragmentStoreBinding


    private var typeAdapter: FeedsStoreAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Store"
        )
        mViewBinding.viewModel = sharedViewModel
        setRecyclerView()
        sharedViewModel.rechargeItemAdapter.setList(
            getListOfApps(
                R.raw.recharges_json,
                arrayListOf(
                    R.drawable.airtel_logo,
                    R.drawable.vi_logo,
                    R.drawable.jio_logo
                )
            )
        )
        mViewBinding.shimmerLayout.startShimmer()
        sharedViewModel.storeAdapter.setList(
            getListOfApps(
                R.raw.shopping_json,
                arrayListOf(
                    R.drawable.flipcart_logo,
                    R.drawable.amazon_logo,
                    R.drawable.myntra_logo,
                    R.drawable.zara_logo,
                    R.drawable.nykaa_logo,
                    R.drawable.bewakoof_logo,
                    R.drawable.maybeonline_logo,
                    R.drawable.handm
                )
            )
        )


        sharedViewModel.foodDeliveryItemAdapter.setList(
            getListOfApps(
                R.raw.food_delivery_json,
                arrayListOf(
                    R.drawable.zomato,
                    R.drawable.swiggy
                )
            )
        )
        sharedViewModel.cabsItemAdapter.setList(
            getListOfApps(
                R.raw.cabs_json,
                arrayListOf(
                    R.drawable.ola,
                    R.drawable.ic_uber
                )
            )
        )


    }

    private fun getListOfApps(stores: Int, iconList: List<Int>): ArrayList<StoreDataModel> {
        val upiList = ArrayList<StoreDataModel>()
        try {
            val obj = JSONObject(loadJSONFromAsset(stores))
            val m_jArry = obj.getJSONArray("stores")

            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)

                val formula_value = jo_inside.getString("title")
                val url_value = jo_inside.getString("url")
                var model = StoreDataModel()
                model.title = formula_value
                model.url = url_value
                model.Icon = iconList[i]

                upiList.add(model)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return upiList


    }

    private fun loadJSONFromAsset(stores: Int): String? {
        var json: String? = null
        json = try {

            val `is`: InputStream? = resources.openRawResource(stores)  // your file name
            val size: Int? = `is`?.available()
            val buffer = size?.let { ByteArray(it) }
            `is`?.read(buffer)
            `is`?.close()
            buffer?.let { String(it, charset("UTF-8")) }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    private fun observeInput(sharedViewModel: StoreScreenViewModel) {
        sharedViewModel.storefeedList.observe(this, { list ->
            if (list.isNullOrEmpty()) {
                mViewBinding.rvStoreFeeds.visibility = View.GONE
            } else {
                mViewBinding.shimmerLayout.stopShimmer()
                typeAdapter?.setList(list)

                typeAdapter?.notifyDataSetChanged()
                sharedViewModel.isRecyclerviewVisible.set(true)
            }


        })

        sharedViewModel.onUpiClicked.observe(requireActivity()) {

            val directions =
                RechargeStoresFragmentDirections.actionDthRechargeScreen()


            directions?.let { it1 -> findNavController().navigate(it1) }

        }
        sharedViewModel.onRechargeClicked.observe(requireActivity()) {
            val directions =
                RechargeStoresFragmentDirections.actionRechargeScreen()


            directions?.let { it1 -> findNavController().navigate(it1) }
        }

    }


    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mViewBinding.rvStoreFeeds.layoutManager = layoutManager


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {


            }

            override fun onCallClicked(pos: Int) {

            }


        }


        typeAdapter = sharedViewModel?.let { FeedsStoreAdapter(requireActivity(), it, this) }
        typeAdapter.let {
            mViewBinding.rvStoreFeeds.adapter = it
        }


    }

    override fun onFeedClick(position: Int, it: FeedDetails) {
        when (it.displayCard) {
            AppConstants.FEED_TYPE_DEEPLINK -> {
                it.action?.url?.let {
                    Utility.deeplinkRedirection(it.split(",")[0], requireContext())

                }
            }
            AppConstants.FEED_TYPE_INAPPWEB2 -> {
                intentToActivity(
                    UserFeedsDetailView::class.java,
                    it,
                    AppConstants.FEED_TYPE_INAPPWEB
                )
            }
            AppConstants.FEED_TYPE_EXTWEBVIEW2 -> {
                startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(it.action?.url)
                        ), getString(R.string.browse_with)
                    )
                )

            }
            AppConstants.FEED_TYPE_INAPPWEB -> {
                intentToActivity(
                    UserFeedsDetailView::class.java,
                    it,
                    AppConstants.FEED_TYPE_INAPPWEB
                )
            }
            AppConstants.FEED_TYPE_BLOG -> {
                intentToActivity(
                    UserFeedsDetailView::class.java,
                    it,
                    AppConstants.FEED_TYPE_BLOG
                )
            }
            AppConstants.FEED_TYPE_EXTWEBVIEW -> {
                startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(it.action?.url)
                        ), getString(R.string.browse_with)
                    )
                )

            }
            AppConstants.FEED_TYPE_STORIES -> {
                if (!it.resourceArr.isNullOrEmpty()) {
                    callDiduKnowBottomSheet(it.resourceArr)
                }

            }
        }

    }

    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "DidUKnowSheet")
    }

    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(requireContext(), aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_store
    }

    override fun getViewModel(): StoreScreenViewModel {


        sharedViewModel = ViewModelProvider(this).get(StoreScreenViewModel::class.java)

        observeInput(sharedViewModel!!)


        return sharedViewModel
    }

}
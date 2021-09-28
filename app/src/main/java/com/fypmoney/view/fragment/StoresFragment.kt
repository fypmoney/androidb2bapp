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


class StoresFragment : BaseFragment<FragmentStoreBinding, StoreScreenViewModel>(),
    FeedsAdapter.OnFeedItemClickListener {
    companion object {
        var page = 0

    }

    private lateinit var sharedViewModel: StoreScreenViewModel
    private lateinit var mViewBinding: FragmentStoreBinding


    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: FeedsStoreAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewBinding.viewModel = sharedViewModel
        setRecyclerView()
        sharedViewModel.rechargeItemAdapter.setList(
            getListOfApps(
                R.raw.recharges_json,
                R.array.rechargeIconList
            )
        )
        sharedViewModel.storeAdapter.setList(
            getListOfApps(
                R.raw.shopping_json,
                R.array.shoppingIconList
            )
        )


    }

    private fun getListOfApps(stores: Int, rechargeIconList: Int): ArrayList<StoreDataModel> {
        val upiList = ArrayList<StoreDataModel>()
        val iconList = PockketApplication.instance.resources.getIntArray(rechargeIconList)

        try {
            val obj = JSONObject(loadJSONFromAsset(stores))
            val m_jArry = obj.getJSONArray("stores")

            var m_li: HashMap<String, String>
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

    open fun loadJSONFromAsset(stores: Int): String? {
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
        sharedViewModel.storefeedList.observe(requireActivity(), { list ->

            typeAdapter?.setList(list)
            typeAdapter?.notifyDataSetChanged()


        })


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

        typeAdapter = sharedViewModel?.let { FeedsStoreAdapter(it, this) }
        mViewBinding.rvStoreFeeds.adapter = typeAdapter
    }

    override fun onFeedClick(position: Int, feedDetails: FeedDetails) {

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_store
    }

    override fun getViewModel(): StoreScreenViewModel {

        parentFragment?.let {
            sharedViewModel = ViewModelProvider(it).get(StoreScreenViewModel::class.java)

            observeInput(sharedViewModel!!)
        }

        return sharedViewModel
    }

}
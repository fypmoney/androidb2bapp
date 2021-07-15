package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewOrderCardBinding
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.fypmoney.view.adapter.TabsAdapter
import com.fypmoney.viewmodel.OrderCardViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_order_card.*

/*
* This class is used to order card
* */
class OrderCardView : BaseActivity<ViewOrderCardBinding, OrderCardViewModel>(),MyProfileListAdapter.OnListItemClickListener {
    private lateinit var mViewModel: OrderCardViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_order_card
    }

    override fun getViewModel(): OrderCardViewModel {
        mViewModel = ViewModelProvider(this).get(OrderCardViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@OrderCardView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )

        val myProfileAdapter = MyProfileListAdapter(applicationContext, this)
        list.adapter = myProfileAdapter

        val iconList = ArrayList<Int>()
        iconList.add(R.drawable.ic_check_skyblue)
        iconList.add(R.drawable.ic_check_skyblue)
        iconList.add(R.drawable.ic_check_skyblue)
        iconList.add(R.drawable.ic_check_skyblue)

        myProfileAdapter.setList(
            iconList1 = iconList,
            resources.getStringArray(R.array.order_card_list).toMutableList()
        )

        setObservers()


    }

    fun setObservers() {
        mViewModel.onOrderCardClicked.observe(this)
        {
            if (it) {
                intentToActivity(PlaceOrderCardView::class.java)
                mViewModel.onOrderCardClicked.value = false
            }


        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@OrderCardView, aClass))
    }

    override fun onItemClick(position: Int) {

    }
}

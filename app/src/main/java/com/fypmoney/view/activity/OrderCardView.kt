package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewOrderCardBinding
import com.fypmoney.view.adapter.MyOrderListAdapter
import com.fypmoney.viewmodel.OrderCardViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_order_card.*

/*
* This class is used to order card
* */
class OrderCardView : BaseActivity<ViewOrderCardBinding, OrderCardViewModel>() {
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
            isBackArrowVisible = true, toolbarTitle = getString(R.string.order_card)
        )

        val myOrderListAdapter = MyOrderListAdapter(applicationContext)
        list.adapter = myOrderListAdapter

        val iconList = ArrayList<Int>()
        iconList.add(R.drawable.ic_limit)
        iconList.add(R.drawable.ic_safe)
        iconList.add(R.drawable.ic_real_time)
        iconList.add(R.drawable.ic_lock__1_)

        myOrderListAdapter.setList(
            iconList1 = iconList,
            resources.getStringArray(R.array.order_card_list).toMutableList()
        )
        setObservers()

        helpValue.setOnClickListener {
            callFreshChat(applicationContext)

        }


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


}

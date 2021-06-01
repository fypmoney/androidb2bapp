package com.fypmoney.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenCardBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.CardListViewAdapter
import com.fypmoney.viewmodel.CardScreenViewModel
import kotlinx.android.synthetic.main.screen_card.*


/**
 * This fragment is used for handling card
 */
class CardScreen : BaseFragment<ScreenCardBinding, CardScreenViewModel>() {
    private lateinit var mViewModel: CardScreenViewModel
    private lateinit var mViewBinding: ScreenCardBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_card
    }

    override fun getViewModel(): CardScreenViewModel {
        mViewModel = ViewModelProvider(this).get(CardScreenViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel

        val textString =
            arrayOf("Card Settings", "Order Card", "Account Statement", "Set up Spending Limit")
        val drawableIds = arrayOf<Int>(
            R.drawable.lock, R.drawable.order, R.drawable.transaction,
            R.drawable.set_up_limit
        )
        val adapter = CardListViewAdapter(requireActivity(), textString, drawableIds)


        list.setAdapter(adapter)
        /*  setToolbarAndTitle(
              context = requireContext(),
              toolbar = toolbar,
              isBackArrowVisible = true, toolbarTitle = getString(R.string.add_money_screen_title)
          )*/


        setObservers()

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mViewModel.onViewDetailsClicked.observe(viewLifecycleOwner) {
              if (it) {
                  askForDevicePassword()
                  mViewModel.onViewDetailsClicked.value = false
              }
          }


    }

    override fun onTryAgainClicked() {

    }
    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                       mViewModel.isCardDetailsVisible.set(true)

                    }

                }
            }
        }
    }


}

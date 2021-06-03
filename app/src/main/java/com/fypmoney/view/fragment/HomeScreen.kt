package com.fypmoney.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenHomeBinding
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.*
import com.fypmoney.viewmodel.ChoresViewModel
import com.fypmoney.viewmodel.HomeScreenViewModel
import com.google.android.material.card.MaterialCardView


/**
 * This fragment is used for loyalty
 */
class HomeScreen : BaseFragment<ScreenHomeBinding, HomeScreenViewModel>() {

    private lateinit var mViewModel: HomeScreenViewModel
    private lateinit var mViewBinding: ScreenHomeBinding
    lateinit var chore_card: MaterialCardView

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_home
    }

    override fun getViewModel(): HomeScreenViewModel {
        mViewModel = ViewModelProvider(this).get(HomeScreenViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        chore_card = view.findViewById(R.id.chore_card)
        chore_card.setOnClickListener {
            intentToAddMemberActivity(ChoresActivity::class.java)
            }
        setObservers()

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mViewModel.onAddMoneyClicked.observe(viewLifecycleOwner) {
            if (it) {
                callFrag()
                //intentToAddMemberActivity(ChoresActivity::class.java)
                mViewModel.onAddMoneyClicked.value = false
            }
        }

        /*mViewModel.onChoreClicked.observe(viewLifecycleOwner) {
            if (it) {
                intentToAddMemberActivity(ChoresActivity::class.java)
                mViewModel.onChoreClicked.value = false
            }
        }*/

        mViewModel.onFeedButtonClick.observe(viewLifecycleOwner) {
            when (it.action?.type) {
                AppConstants.FEED_TYPE_IN_APP -> {
                    try {
                        intentToActivity(
                            Class.forName(AppConstants.BASE_ACTIVITY_URL + it.action?.url!!),
                            it
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        intentToActivity(HomeView::class.java, it)
                    }
                }
                AppConstants.FEED_TYPE_IN_APP_WEBVIEW, AppConstants.FEED_TYPE_FEED -> {
                    intentToActivity(UserFeedsDetailView::class.java, it, type = it.action?.type)
                }
                AppConstants.FEED_TYPE_EXTERNAL_WEBVIEW -> {
                    startActivity(
                        Intent.createChooser(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(it.action?.url)
                            ), getString(R.string.browse_with)
                        )
                    )

                }
            }

        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setCurrentFragment(fragment: Fragment) =
        childFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }

    /*
    * This method is used to call add money fragment
    * */
    private fun callFrag() {
        val fragment2 = AddMoneyScreen()
        val fragmentManager: FragmentManager? = fragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment2)
        fragmentTransaction.commit()
    }

    private fun intentToAddMemberActivity(aClass: Class<*>) {
        val intent = Intent(requireActivity(), aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
        requireContext().startActivity(intent)
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(context, aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponse())
        startActivity(intent)
    }
}

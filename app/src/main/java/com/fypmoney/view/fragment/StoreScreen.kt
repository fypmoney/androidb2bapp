package com.fypmoney.view.fragment

import com.fypmoney.view.WebpageOpener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.StoreDataModel
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.StoreScreenViewModel
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


/**
 * This fragment is used for loyalty
 */
class StoreScreen : BaseFragment<ScreenStoreBinding, StoreScreenViewModel>() {

    private lateinit var mViewModel: StoreScreenViewModel
    private lateinit var mViewBinding: ScreenStoreBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_store
    }

    override fun getViewModel(): StoreScreenViewModel {
        mViewModel = ViewModelProvider(this).get(StoreScreenViewModel::class.java)
        return mViewModel
    }
    private fun getListOfApps(stores: Int): ArrayList<StoreDataModel> {
        val upiList = ArrayList<StoreDataModel>()
        try {
            val obj = JSONObject(loadJSONFromAsset(stores))
            val m_jArry = obj.getJSONArray("stores")

            var m_li: HashMap<String, String>
            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)

                val formula_value = jo_inside.getString("title")
                val url_value = jo_inside.getString("url")
                var model=StoreDataModel()
                model.title=formula_value
                model.url=url_value
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

            val `is`: InputStream? =  resources.openRawResource(stores)  // your file name
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel

        mViewModel.storeAdapter.setList(getListOfApps(R.raw.stores))
        mViewModel.rechargeItemAdapter.setList(getListOfApps(R.raw.rechargers))

        setObservers(requireContext())

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers(requireContext: Context) {

            mViewModel.onUpiClicked.observe(requireActivity()) {


    val intent2 = Intent(requireContext, WebpageOpener::class.java)
                    WebpageOpener.url = "https://www.amazon.in"
                            requireContext.startActivity(intent2)



            }
        mViewModel.onRechargeClicked.observe(requireActivity()) {
            when (mViewModel.clickedPositionForUpi.get()) {


            }
        }

    }

    override fun onTryAgainClicked() {

    }



}

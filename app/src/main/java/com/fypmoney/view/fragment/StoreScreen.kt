package com.fypmoney.view.fragment

import com.fypmoney.view.StoreWebpageOpener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.model.StoreDataModel
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

        mViewModel.rechargeItemAdapter.setList(
            getListOfApps(
                R.raw.recharges_json,
                R.array.rechargeIconList
            )
        )
        mViewModel.storeAdapter.setList(
            getListOfApps(
                R.raw.shopping_json,
                R.array.shoppingIconList
            )
        )

        setObservers(requireContext())

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers(requireContext: Context) {

            mViewModel.onUpiClicked.observe(requireActivity()) {


                val intent2 = Intent(requireContext, StoreWebpageOpener::class.java)
                StoreWebpageOpener.url = it.url!!
                intent2.putExtra("title", it.title)
                requireContext.startActivity(intent2)


            }
        mViewModel.onRechargeClicked.observe(requireActivity()) {


            val intent2 = Intent(requireContext, StoreWebpageOpener::class.java)
            StoreWebpageOpener.url = it.url!!
            intent2.putExtra("title", it.title)
            requireContext.startActivity(intent2)


        }

    }

    override fun onTryAgainClicked() {

    }



}

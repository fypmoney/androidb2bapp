package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRechargeHomeBinding
import com.fypmoney.model.StoreDataModel
import com.fypmoney.util.AppConstants.POSTPAID
import com.fypmoney.view.recharge.viewmodel.RechargeViewModel
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class RechargeStoresFragment : BaseFragment<FragmentRechargeHomeBinding, RechargeViewModel>() {

    private lateinit var sharedViewModel: RechargeViewModel
    private lateinit var mViewBinding: FragmentRechargeHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,

            isBackArrowVisible = true, toolbarTitle = "Mobile Recharge",
            titleColor = Color.BLACK,
            backArrowTint = Color.BLACK
        )
        mViewBinding.viewModel = sharedViewModel

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


        sharedViewModel.RechargePostpaidAdapter.setList(
            getListOfApps(
                R.raw.recharges_json,
                arrayListOf(
                    R.drawable.airtel_logo,
                    R.drawable.vi_logo,
                    R.drawable.jio_logo
                )
            )
        )

        sharedViewModel.storeAdapter.setList(
            getListOfApps(
                R.raw.recharges_json,
                arrayListOf(
                    R.drawable.airtel_logo,
                    R.drawable.vi_logo,
                    R.drawable.jio_logo
                )
            )
        )


//        sharedViewModel.cabsItemAdapter.setList(
//            getListOfApps(
//                R.raw.cabs_json,
//                arrayListOf(
//                    R.drawable.ola,
//                    R.drawable.ic_uber
//                )
//            )
//        )


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

    }

    private fun observeInput(sharedViewModel: RechargeViewModel) {

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
        sharedViewModel.onPostpaidClicked.observe(requireActivity()) {
            val directions =
                RechargeStoresFragmentDirections.actionRechargeScreen(rechargeType = POSTPAID)


            directions?.let { it1 -> findNavController().navigate(it1) }
        }

    }







    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_recharge_home
    }

    override fun getViewModel(): RechargeViewModel {


        sharedViewModel = ViewModelProvider(this).get(RechargeViewModel::class.java)

        observeInput(sharedViewModel!!)


        return sharedViewModel
    }

}
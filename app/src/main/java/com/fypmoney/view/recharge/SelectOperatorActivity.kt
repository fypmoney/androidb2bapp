package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivitySelectOperatorBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.OfflineOperatorResponse
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectOperatorViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.reflect.Type


/*
* This class is used as Home Screen
* */
class SelectOperatorActivity :
    BaseFragment<ActivitySelectOperatorBinding, SelectOperatorViewModel>() {

    private val mViewModel by viewModels<SelectOperatorViewModel> { defaultViewModelProviderFactory }
    private lateinit var mViewBinding: ActivitySelectOperatorBinding
    private val args: SelectOperatorActivityArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_operator
    }

    override fun getViewModel(): SelectOperatorViewModel {
        return mViewModel
    }

    val offileOperators: ArrayList<OfflineOperatorResponse> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()





        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Mobile Recharge"
        )
        parseJSON()
        setBindings()
        setObserver()
        args.rechargeType.let {
            mViewModel.rechargeType.value = it

            if (it == AppConstants.POSTPAID) {
                mViewModel.callGetOperatorList(AppConstants.POSTPAID)
            } else {
                mViewModel.callGetOperatorList(AppConstants.PREPAID)
            }
        }
        args.circle.let {
            mViewModel.circleGot.value = it
        }
        args.mobile.let {
            mViewModel.mobileNumber.value = it

            mViewBinding.tvUserNumber.text = it
        }
        args.operator.let {
            offileOperators.forEach { op ->

                if (op.name == it && mViewModel.rechargeType.value == op.type) {
                    val operator = OperatorResponse()
                    operator.operatorId = op.operator_id
                    operator.name = op.name
                    operator.displayName = op.displayname
                    operator.type = op.type
                    operator.status = op.status
                    operator.category = op.category

                    setToolbarAndTitle(
                        context = requireContext(),
                        toolbar = toolbar, backArrowTint = Color.WHITE,
                        titleColor = Color.WHITE,
                        isBackArrowVisible = true,
                        toolbarTitle = op.name
                    )

                    mViewModel.operatorResponse.set(operator)
                }
            }


            mViewModel.OperatorGot.value = it

            mViewBinding.optionsMenu.text = it

        }





    }


    private fun setBindings() {

        mViewBinding.continueBtn.setOnClickListener {


            if (mViewModel.circleGot.value != null && mViewModel.operatorResponse.get() != null) {


                mViewModel.circleGot.value.let {
                    mViewModel.rechargeType.value.let { itstr ->
                        if (itstr == AppConstants.POSTPAID) {
                            val directions =

                                mViewModel.mobileNumber.value?.let { it2 ->
                                    mViewModel.circleGot.value?.let { it1 ->
                                        SelectCircleActivityDirections.actionPostpaidBill(
                                            mViewModel.operatorResponse.get(),
                                            selectedCircle = it1,
                                            mobile = it2
                                        )
                                    }
                                }



                            directions?.let { it1 -> findNavController().navigate(it1) }
                        } else {

                            val directions =

                                mViewModel.mobileNumber.value?.let { it2 ->
                                    mViewModel.circleGot.value?.let { it1 ->
                                        SelectCircleActivityDirections.actionSelectRechargePlans(
                                            mViewModel.operatorResponse.get(),
                                            selectedCircle = it1,
                                            mobile = it2
                                        )
                                    }
                                }



                            directions?.let { it1 -> findNavController().navigate(it1) }
                        }
                    }


                }


            } else if (mViewModel.operatorResponse.get() != null) {


                val directions =
                    SelectOperatorActivityDirections.actionSelectCircle(
                        selectedOperator = mViewModel.operatorResponse.get(),
                        mobile = mViewModel.mobileNumber.value,
                        rechargeType = mViewModel.rechargeType.value
                    )

                directions.let { it1 -> findNavController().navigate(it1) }


            } else {
                Utility.showToast("fetch details")
            }

        }
    }

    val json = "[{\n" +
            "\n" +
            "\t\t\"operator_id\": \"12\",\n" +
            "\t\t\"name\": \"Airtel Digital TV\",\n" +
            "\t\t\"category\": \"DTH\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\"\n" +
            "\t}\n" +
            "\n" +
            "\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"14\",\n" +
            "\t\t\"name\": \"Dish TV\",\n" +
            "\t\t\"category\": \"DTH\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\"\n" +
            "\t}\n" +
            "\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"18\",\n" +
            "\t\t\"name\": \"JIO\",\n" +
            "\t\t\"category\": \"MOBILE\",\n" +
            "\t\t\"type\": \"PREPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\"\n" +
            "\t}\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"8\",\n" +
            "\t\t\"name\": \"Tata Sky\",\n" +
            "\t\t\"category\": \"DTH\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\"\n" +
            "\t}\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"22\",\n" +
            "\t\t\"name\": \"Vodafone\",\n" +
            "\t\t\"category\": \"MOBILE\",\n" +
            "\t\t\"type\": \"PREPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"23\",\n" +
            "\t\t\"name\": \"Vodafone\",\n" +
            "\t\t\"category\": \"MOBILE\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null\n" +
            "\t}\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"62\",\n" +
            "\t\t\"name\": \"Jio\",\n" +
            "\t\t\"category\": \"MOBILE\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null\n" +
            "\t}\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"9\",\n" +
            "\t\t\"name\": \"Airtel\",\n" +
            "\t\t\"category\": \"MOBILE\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null\n" +
            "\t}\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"11\",\n" +
            "\t\t\"name\": \"Airtel\",\n" +
            "\t\t\"category\": \"MOBILE\",\n" +
            "\t\t\"type\": \"PREPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\",\n" +
            "\t\t\"icon\": \"abc\"\n" +
            "\t}\n" +
            "\n" +
            "\t,\n" +
            "\t{\n" +
            "\n" +
            "\t\t\"operator_id\": \"10\",\n" +
            "\t\t\"name\": \"Videocon D2H\",\n" +
            "\t\t\"category\": \"DTH\",\n" +
            "\t\t\"type\": \"POSTPAID\",\n" +
            "\t\t\"status\": \"ENABLED\",\n" +
            "\t\t\"created-date\": null,\n" +
            "\t\t\"created_by\": null,\n" +
            "\t\t\"last_modified_by\": null,\n" +
            "\t\t\"last_modified_date\": null,\n" +
            "\t\t\"displayname\": \"Mobile Number (+91)\"\n" +
            "\t}\n" +
            "]"

    private fun parseJSON() {
        val gson = Gson()
        val type: Type = object : TypeToken<List<OfflineOperatorResponse?>?>() {}.getType()
        val contactList: List<OfflineOperatorResponse> = gson.fromJson(json, type)

        offileOperators.addAll(contactList)

    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        val navController = findNavController();
        // We use a String here, but any type that can be put in a Bundle is supported
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<OperatorResponse>("operator_selected")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                // Do something with the result.
                var operator = result as OperatorResponse
                mViewModel.operatorResponse.set(operator)
                mViewBinding.optionsMenu.text = operator?.name

            }
        mViewBinding.optionsMenu.setOnClickListener {

            val directions =
                SelectOperatorActivityDirections.actionToOperatorList(
                    rechargeType = mViewModel.rechargeType.value
                )

            directions.let { it1 -> findNavController().navigate(it1) }

        }
        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)

        }
    }


}

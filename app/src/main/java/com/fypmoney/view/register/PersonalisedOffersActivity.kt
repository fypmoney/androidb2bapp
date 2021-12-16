package com.fypmoney.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityPersonalizedOfferBinding
import com.fypmoney.view.register.adapters.OffersAdapterAdapter
import com.fypmoney.view.register.viewModel.PersonalisedOfferVm
import com.fypmoney.view.storeoffers.model.offerDetailResponse

class PersonalisedOffersActivity :
    BaseActivity<ActivityPersonalizedOfferBinding, PersonalisedOfferVm>() {

    private var typeAdapter: OffersAdapterAdapter? = null
    private lateinit var binding: ActivityPersonalizedOfferBinding
    private val personalisedOffersVM by viewModels<PersonalisedOfferVm> { defaultViewModelProviderFactory }
    private var itemsArrayList: ArrayList<offerDetailResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        binding.continueBtn.setOnClickListener(View.OnClickListener {
            intentToActivity(OpenGiftActivity::class.java)
        })


        setRecyclerView(binding)
        setObserver()

    }

    private fun setObserver() {
        personalisedOffersVM.offerList.observe(this, {
            itemsArrayList.clear()
            itemsArrayList.addAll(it)
            typeAdapter?.notifyDataSetChanged()
        })
    }

    private fun intentToActivity(aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(this, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            finishAffinity()
        }
    }

    private fun setRecyclerView(root: ActivityPersonalizedOfferBinding) {
        val layoutManager = GridLayoutManager(this, 2)
        root.rvOffers.layoutManager = layoutManager

        typeAdapter = OffersAdapterAdapter(itemsArrayList, this)
        root.rvOffers.adapter = typeAdapter

    }


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_personalized_offer

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): PersonalisedOfferVm = personalisedOffersVM


}
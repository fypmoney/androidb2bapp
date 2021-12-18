package com.fypmoney.view.ordercard.cardofferdetails

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.setSomePartOfTextInColor
import com.fypmoney.databinding.ActivityCardOfferDetailsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.ordercard.personaliseyourcard.PersonaliseYourCardActivity
import kotlinx.android.synthetic.main.toolbar.*

class CardOfferDetailsActivity : BaseActivity<ActivityCardOfferDetailsBinding,CardOfferDetailsVM>() {
    private lateinit var viewModel:CardOfferDetailsVM
    private lateinit var binding: ActivityCardOfferDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = this@CardOfferDetailsActivity,
            toolbar = toolbar,
            isBackArrowVisible = true,
            backArrowTint = Color.WHITE
        )
        viewModel.userOfferCard = intent.getParcelableExtra(AppConstants.ORDER_CARD_INFO)
        setupViews()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.event.observe(this,{
            when(it){
                CardOfferDetailsVM.CardOfferDetailsEvent.ContinueEvent -> {
                    val bundle = Bundle()
                    bundle.putString(
                        "user_id", SharedPrefUtils.getLong(
                            applicationContext,
                            SharedPrefUtils.SF_KEY_USER_ID
                        ).toString()
                    )
                    trackr {
                        it.name = TrackrEvent.card_order_details_continue
                        it.add(
                            TrackrField.user_id, SharedPrefUtils.getLong(
                                applicationContext,
                                SharedPrefUtils.SF_KEY_USER_ID
                            ).toString()
                        )
                    }
                    val intent =
                        Intent(this@CardOfferDetailsActivity, PersonaliseYourCardActivity::class.java)
                    intent.putExtra(AppConstants.ORDER_CARD_INFO, viewModel.userOfferCard)
                    startActivity(intent)
                }
            }
        })
    }

    private fun setupViews() {
        with(binding){
            Utility.convertToRs("${viewModel?.userOfferCard?.basePrice}")?.let { it1 ->
                cardActualPriceValueTv.text = getString(R.string.Rs)+it1
            }
            Utility.convertToRs("${viewModel?.userOfferCard?.mrp}")?.let { it1 ->
                cardOfferPriceValueTv.text = getString(R.string.Rs)+it1
            }
            setSomePartOfTextInColor(offerDetailsHeadingTv,getString(R.string.your_fyp_card_comes_with_great_benefits),
                        getString(R.string.great_benefits),
                        "#F7AA11"
            )
            setSomePartOfTextInColor(title1Tv,getString(R.string.earn),
                        getString(R.string.mynts_5_x),
                        "#F7AA11",
                        getString(R.string.earn_5x_mynts_on_both_online_and_offline_transactions)

                )
            setSomePartOfTextInColor(title2Tv,"",
                        getString(R.string._7_instant_cashback),
                        "#F7AA11",
                        getString(R.string.on_offline_transactions)

                )

            setSomePartOfTextInColor(title3Tv,getString(R.string.get_one_airport_lounge_access_worth_1500),
                getString(R.string._1500),
                "#F7AA11"
            )
        }
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_card_offer_details

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): CardOfferDetailsVM {
        viewModel =  ViewModelProvider(this).get(CardOfferDetailsVM::class.java)
        return viewModel
    }
}
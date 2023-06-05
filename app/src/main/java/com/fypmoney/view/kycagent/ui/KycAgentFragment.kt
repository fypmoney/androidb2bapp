package com.fypmoney.view.kycagent.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentKycAgentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.LoginView
import com.fypmoney.view.arcadegames.model.ArcadeType
import com.fypmoney.view.arcadegames.model.checkTheArcadeType
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.kycagent.viewmodel.KycAgentFragmentVM
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class KycAgentFragment : BaseFragment<FragmentKycAgentBinding, KycAgentFragmentVM>() {

    private lateinit var binding: FragmentKycAgentBinding
    private val kycAgentFragmentVM by viewModels<KycAgentFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        binding.viewModel = kycAgentFragmentVM

        setUpObserver()

        kycAgentFragmentVM.fullKycDone =
            Utility.getCustomerDataFromPreference()?.bankProfile?.kycType

        binding.ivKycCard.setOnClickListener {

            //check where we want to send user based on conditions

            setUserRedirection()

        }

        binding.mcvNewKyc.setOnClickListener {
            if (kycAgentFragmentVM.isShopListed != null && kycAgentFragmentVM.isShopListed.equals(
                    AppConstants.YES
                )
            ) {
//                findNavController().navigate(
//                    R.id.navigation_qr_code_scan,
//                    null,
//                    navOptions {
//                        anim {
//                            popEnter = R.anim.slide_in_left
//                            popExit = R.anim.slide_out_righ
//                            enter = R.anim.slide_in_right
//                            exit = R.anim.slide_out_left
//                        }
//                    })

                val kycModeBottomSheet = ChooseKycModeBottomSheet()
//                val deviceBottomSheet = DeviceBottomSheet(onContinueClick = {
//                    findNavController().navigateUp()
//                })
                kycModeBottomSheet.show(requireActivity().supportFragmentManager,"kycBottomSheet")
            } else {
                setUserRedirection()
            }
        }

        binding.mcvEarnings.setOnClickListener {
            if (kycAgentFragmentVM.isShopListed != null && kycAgentFragmentVM.isShopListed.equals(
                    AppConstants.YES
                )
            ) {
                findNavController().navigate(R.id.navigation_my_earnings, null, navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
            } else {
                setUserRedirection()
            }
        }

    }

    private fun setUserRedirection(){
        if (kycAgentFragmentVM.shopName == null) {
            findNavController().navigate(
                R.id.navigation_kyc_merchant_registration,
                null,
                navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
        } else if (kycAgentFragmentVM.isShopPhotoUpload == null) {
            findNavController().navigate(R.id.navigation_photo_upload_kyc, null, navOptions {
                anim {
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_righ
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                }
            })
        } else if ((kycAgentFragmentVM.isShopListed == null) || (kycAgentFragmentVM.isShopListed.equals(
                AppConstants.NO
            ))
        ) {
            val bundle = Bundle()
            bundle.putString("via", "SelfKyc")
            findNavController().navigate(
                R.id.navigation_enter_aadhaar_number_kyc,
                bundle,
                navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
        } else {
            Utility.showToast("Self KYC Completed")
        }
    }

    private fun setUpObserver() {

        kycAgentFragmentVM.state.observe(viewLifecycleOwner) {
            when (it) {
                is KycAgentFragmentVM.KycAgentState.Error -> {

                }
                KycAgentFragmentVM.KycAgentState.Loading -> {

                }
                is KycAgentFragmentVM.KycAgentState.Success -> {
                    kycAgentFragmentVM.isShopPhotoUpload = it.shopData.shopPhoto
                    kycAgentFragmentVM.shopName = it.shopData.shopName
                    kycAgentFragmentVM.isShopListed = it.shopData.isShopListed

                    if (kycAgentFragmentVM.isShopListed.equals(AppConstants.YES)){
//                        Glide.with(requireContext()).load(R.drawable.ic_agent_full_kyc).into(binding.ivKycCard)
                        binding.ivKycCard.toGone()
                        binding.ivAfterKycCard.toVisible()
                    }
                }
            }
        }

        kycAgentFragmentVM.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->
            setRecyclerView(binding, list)
        }

        kycAgentFragmentVM.event.observe(viewLifecycleOwner){
            when(it){
                KycAgentFragmentVM.KycAgentEvent.LogOutEvent -> {
                    kycAgentFragmentVM.callLogOutApi()
                }
                KycAgentFragmentVM.KycAgentEvent.LogOutSuccess -> {
                    intentToActivityMain(requireActivity(), LoginView::class.java, isFinishAll = true)
                }
                KycAgentFragmentVM.KycAgentEvent.NotificationClicked -> {
                    callFreshChat(requireContext())
                    //startActivity(Intent(requireActivity(), NotificationView::class.java))
                }
            }
        }

    }

    private fun intentToActivityMain(context: Context, aClass: Class<*>, isFinishAll: Boolean? = false) {
        val intent = Intent(context, aClass)
        startActivity(intent)
        if (isFinishAll == true) {
            requireActivity().finishAffinity()
        }
    }

    private fun setRecyclerView(
        root: FragmentKycAgentBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.exploreShimmerLayout.visibility = View.GONE
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.exploreHomeRv.layoutManager = layoutManager

        val arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        val exploreClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(
                position: Int,
                sectionContentItem: SectionContentItem,
                exploreContentResponse: ExploreContentResponse?
            ) {
                trackr {
                    it.name = TrackrEvent.home_explore_click
                    it.add(TrackrField.explore_content_id, sectionContentItem.id)
                }
                openExploreFeatures(
                    sectionContentItem.redirectionType,
                    sectionContentItem.redirectionResource,
                    sectionContentItem,
                    exploreContentResponse
                )


            }
        }
        val scale: Float = requireActivity().resources.displayMetrics.density
        val typeAdapter = ExploreBaseAdapter(
            arrayList,
            requireContext(),
            exploreClickListener2,
            scale,
            Color.parseColor(kycAgentFragmentVM.textColor.value)
        )
        root.exploreHomeRv.adapter = typeAdapter
    }

    private fun openExploreFeatures(
        redirectionType: String?,
        redirectionResource: String?,
        sectionContentItem: SectionContentItem,
        exploreContentResponse: ExploreContentResponse?
    ) {
        when (redirectionType) {
            AppConstants.TYPE_VIDEO -> {
                val intent = Intent(requireActivity(), VideoActivity2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, redirectionResource)
                startActivity(intent)

            }
            AppConstants.TYPE_VIDEO_EXPLORE -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/videowithexplore?videoUrl=${redirectionResource}&actionFlag=${sectionContentItem.actionFlagCode}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
            AppConstants.TYPE_POCKET_MONEY_REMINDER -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/pocketmoneyremainder"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
            AppConstants.EXPLORE_SECTION_EXPLORE -> {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    sectionContentItem.redirectionResource?.let {
                        param(
                            FirebaseAnalytics.Param.SCREEN_NAME,
                            it
                        )
                    }
                    param(
                        FirebaseAnalytics.Param.SCREEN_CLASS,
                        KycAgentFragment::class.java.simpleName
                    )
                }
//                if(findNavController().currentDestination?.id==R.id.navigation_kyc_agent){
//                    val directions = KycAgentFragmentDirections.actionKycAgentToSectionExplore(sectionExploreItem = sectionContentItem,
//                        sectionExploreName= exploreContentResponse?.sectionDisplayText)
//                    directions.let { it1 -> findNavController().navigate(it1) }
//                }
            }
            AppConstants.EXPLORE_IN_APP -> {
                redirectionResource?.let { uri ->

                    when (val redirectionResources = uri.split(",")[0]) {
                        AppConstants.FyperScreen -> {
                            findNavController().navigate(R.id.navigation_fyper)
                        }
                        AppConstants.JACKPOTTAB -> {
                            findNavController().navigate(R.id.navigation_jackpot)
                        }
                        AppConstants.CardScreen -> {
                            findNavController().navigate(R.id.navigation_card)
                        }
                        AppConstants.RewardHistory -> {
                            findNavController().navigate(R.id.navigation_rewards_history)
                        }
                        AppConstants.ARCADE -> {
                            findNavController().navigate(R.id.navigation_arcade)
                        }
                        AppConstants.GIFT_VOUCHER -> {
                            findNavController().navigate(Uri.parse("fypmoney://creategiftcard/${redirectionResources}"))
                        }
                        AppConstants.F_Store -> {
                            findNavController().navigate(R.id.navigation_explore)
                        }
                        AppConstants.REWARDS -> {
                            findNavController().navigate(R.id.navigation_rewards)
                        }
                        AppConstants.INSIGHTS -> {
                            findNavController().navigate(R.id.navigation_insights)
                        }
                        AppConstants.PREPAID_RECHARGE_REDIRECTION -> {
                            findNavController().navigate(
                                Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.PREPAID}"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }

                        AppConstants.POSTPAID_RECHARGE_REDIRECTION -> {
                            findNavController().navigate(
                                Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.POSTPAID}"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }

                        AppConstants.DTH_RECHARGE_REDIRECTION -> {
                            findNavController().navigate(
                                Uri.parse("https://www.fypmoney.in/recharge/dth"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }
                        AppConstants.TYPE_POCKET_MONEY_REMINDER -> {
                            findNavController().navigate(
                                Uri.parse("https://www.fypmoney.in/pocketmoneyremainder"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }
                        else -> {
                            redirectionResources.let { it1 ->
                                Utility.deeplinkRedirection(
                                    it1,
                                    requireContext()
                                )
                            }
                        }
                    }


                }

            }
            AppConstants.EXPLORE_IN_APP_WEBVIEW -> {

                val intent = Intent(requireContext(), ExploreInAppWebview::class.java)
                intent.putExtra(
                    AppConstants.FROM_WHICH_SCREEN,
                    AppConstants.EXPLORE_IN_APP_WEBVIEW
                )
                intent.putExtra(AppConstants.IN_APP_URL, redirectionResource)

                startActivity(intent)
            }
            AppConstants.IN_APP_WITH_CARD -> {
                val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, redirectionResource)
                startActivity(intent)

            }
            AppConstants.OFFER_REDIRECTION -> {
                kycAgentFragmentVM.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                kycAgentFragmentVM.callFetchFeedsApi(redirectionResource)
            }

            AppConstants.EXT_WEBVIEW -> {
                if (redirectionResource != null) {
                    startActivity(
                        Intent.createChooser(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(redirectionResource)
                            ), getString(R.string.browse_with)
                        )
                    )
                }


            }
            AppConstants.EXPLORE_TYPE_STORIES -> {
                if (!redirectionResource.isNullOrEmpty()) {
                    kycAgentFragmentVM.callFetchFeedsApi(redirectionResource)

                }

            }
            AppConstants.GIFT_VOUCHER -> {
                findNavController().navigate(
                    Uri.parse("fypmoney://creategiftcard/${redirectionResource}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }

            AppConstants.LEADERBOARD -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/leaderboard/${redirectionResource}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
            "ARCADE" -> {
                val type = sectionContentItem.rfu1?.let { rfu ->
                    sectionContentItem.redirectionResource?.let { it1 ->
                        checkTheArcadeType(
                            arcadeType = rfu,
                            productCode = it1
                        )
                    }
                }
                when (type) {
                    ArcadeType.NOTypeFound -> TODO()
                    is ArcadeType.SCRATCH_CARD -> TODO()
                    is ArcadeType.SLOT -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/slot_machine/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    is ArcadeType.SPIN_WHEEL -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/spinwheel/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    is ArcadeType.TREASURE_BOX -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/rotating_treasure/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    is ArcadeType.BRANDED_COUPONS -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/branded_coupons/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    null -> {}
                }
            }
            AppConstants.PREPAID_RECHARGE_REDIRECTION -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.PREPAID}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }

            AppConstants.POSTPAID_RECHARGE_REDIRECTION -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.POSTPAID}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }

            AppConstants.DTH_RECHARGE_REDIRECTION -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/recharge/dth"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
        }
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_kyc_agent

    override fun getViewModel(): KycAgentFragmentVM = kycAgentFragmentVM

    override fun onTryAgainClicked() {
    }

}
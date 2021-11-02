package com.fypmoney.view.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.base.PaginationListener
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.FragmentOffersBinding
import com.fypmoney.databinding.FragmentStoreBinding
import com.fypmoney.databinding.ScreenStoreBinding
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.StoreDataModel
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.activity.AddTaskActivity
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.activity.OfferDetailActivity
import com.fypmoney.view.activity.PayRequestProfileView
import com.fypmoney.view.adapter.*
import com.fypmoney.view.giftCardModule.GiftCardAdapter
import com.fypmoney.view.giftCardModule.PurchaseGiftCardScreen

import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.view.offfers.OffersStoreFragmentVM
import com.fypmoney.viewmodel.NotificationViewModel
import com.fypmoney.viewmodel.StoreScreenViewModel

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


import kotlin.collections.ArrayList


class OffersStoreFragment : BaseFragment<FragmentOffersBinding, OffersStoreFragmentVM>() {
    private var typeAdapter: OffersTopAdapter? = null
    private var bottomAdapter: OffersBottomAdapter? = null
    private var itemsArrayList: ArrayList<FeedDetails> = ArrayList()
    private var bottomArrayList: ArrayList<FeedDetails> = ArrayList()
    private var mViewBinding: FragmentOffersBinding? = null
    private var mviewModel: OffersStoreFragmentVM? = null
    private var isLoading = false

    companion object {
        var page = 0

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        page = 0
        setRecyclerView()
        setRecyclerViewBottom()
        mViewBinding?.shimmerLayout?.startShimmer()
        mviewModel?.offerBottomList?.observe(
            requireActivity(),
            { list ->
                mViewBinding?.shimmerLayout?.stopShimmer()
                itemsArrayList.addAll(list)
                mViewBinding?.shimmerLayout?.visibility = View.INVISIBLE
                typeAdapter?.notifyDataSetChanged()


            })
        mviewModel?.offerBottomList?.observe(viewLifecycleOwner, {
            (mViewBinding?.giftcardRv?.adapter as GiftCardAdapter).run {
                submitList(it)
            }

        })
        mviewModel!!.offerBottomList.observe(
            requireActivity(),
            { list ->
                mViewBinding?.LoadProgressBar?.visibility = View.GONE

                if (page == 0) {
                    bottomArrayList.clear()
                    page += 1
                    mViewBinding?.shimmerLayout?.visibility = View.INVISIBLE
                    mViewBinding?.shimmerLayout?.stopShimmer()




                bottomArrayList.addAll(list)
                    isLoading = false


                    bottomAdapter!!.notifyDataSetChanged()


                }

            })

        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        trackr { it.services = arrayListOf(TrackrServices.FIREBASE, TrackrServices.MOENGAGE)
            it.name = TrackrEvent.Offer_tab
        }
    }

    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.featuredOfferRv!!.layoutManager = layoutManager

        var itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: Int) {

                val intent = Intent(requireContext(), OfferDetailActivity::class.java)
                intent.putExtra("feedid", itemsArrayList[pos].id)
                startActivity(intent)
            }


        }

        typeAdapter =
            OffersTopAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        mViewBinding?.featuredOfferRv!!.adapter = typeAdapter
    }

    private fun setUpRecyclerView() {
        val giftCardAdapter = GiftCardAdapter(
            viewLifecycleOwner, onRecentUserClick = {
                val intent = Intent(requireContext(), PurchaseGiftCardScreen::class.java)
                startActivity(intent)
            }
        )


        with(mViewBinding!!.giftcardRv) {
            adapter = giftCardAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )
        }
    }


    private fun setRecyclerViewBottom() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        mViewBinding?.offerRv!!.layoutManager = layoutManager

        var itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: Int) {
                val intent = Intent(requireContext(), OfferDetailActivity::class.java)

                intent.putExtra("feedid", bottomArrayList[pos].id)





                startActivity(intent)

            }


        }

        bottomAdapter =
            OffersBottomAdapter(bottomArrayList, requireContext(), itemClickListener2!!)
        mViewBinding?.offerRv?.adapter = bottomAdapter
    }
    override fun onTryAgainClicked() {

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_offers
    }

    override fun getViewModel(): OffersStoreFragmentVM {
        mviewModel = ViewModelProvider(this).get(OffersStoreFragmentVM::class.java)
        return mviewModel!!
    }

}
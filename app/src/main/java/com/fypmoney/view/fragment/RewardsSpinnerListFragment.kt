package com.fypmoney.view.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.model.SectionListItem
import com.fypmoney.model.aRewardProductResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.SpinWheelViewDark
import com.fypmoney.view.adapter.ScratchItemAdapter
import com.fypmoney.view.adapter.SpinnerAdapter
import com.fypmoney.view.interfaces.ListContactClickListener

import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.dialog_burn_mynts.*
import kotlinx.android.synthetic.main.fragment_spinner_list.view.*
import java.io.Serializable


import kotlin.collections.ArrayList


class RewardsSpinnerListFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var sharedViewModel: RewardsViewModel? = null
    private var dialogDialog: Dialog? = null
    private var itemsArrayList: ArrayList<aRewardProductResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: SpinnerAdapter? = null
    private var scratchAdapter: ScratchItemAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_spinner_list, container, false)
        setRecyclerView(root!!)
        setRvScratchCard(root!!)
        dialogDialog = Dialog(requireContext())
        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(RewardsViewModel::class.java)
            observeInput(sharedViewModel!!)

        }



        return root
    }

    private fun observeInput(sharedViewModel: RewardsViewModel) {

        sharedViewModel.spinArrayList.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->

                itemsArrayList.clear()

                itemsArrayList.addAll(list)
                typeAdapter?.notifyDataSetChanged()

            })

    }

    internal fun showBurnDialog(i: Int) {


        dialogDialog!!.setCancelable(true)
        dialogDialog!!.setCanceledOnTouchOutside(true)
        dialogDialog!!.setContentView(R.layout.dialog_burn_mynts)

        val wlp = dialogDialog!!.window!!.attributes

        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT

        dialogDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog!!.window!!.attributes = wlp



        dialogDialog!!.clicked.setOnClickListener(View.OnClickListener {
            when (itemsArrayList[i].productType) {
                AppConstants.PRODUCT_SPIN -> {
                    val intent = Intent(requireContext(), SpinWheelViewDark::class.java)
                    val `object` = ArrayList<SectionListItem>()
                    SpinWheelViewDark.itemsArrayList.clear()

                    itemsArrayList[i].sectionList?.forEachIndexed { pos, item ->
                        if (item != null) {
                            SpinWheelViewDark.itemsArrayList.add(item)
                        }
                    }


//                    val args = Bundle()
//                    args.putSerializable("ARRAYLIST", itemsArrayList as Serializable)
//                    intent.putExtra("BUNDLE", args)
                    requireContext().startActivity(intent)
                    dialogDialog!!.dismiss()
                }
                AppConstants.PRODUCT_SCRATCH -> {

                }
            }

        })


        dialogDialog!!.show()
    }


    private fun setRecyclerView(root: View) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rv_spinner!!.layoutManager = layoutManager


        var itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: Int) {
                showBurnDialog(pos)


            }
        }

        typeAdapter = SpinnerAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_spinner!!.adapter = typeAdapter
    }

    private fun setRvScratchCard(root: View) {
        val layoutManager =
            GridLayoutManager(requireContext(), 2)
        root.rv_scratch!!.layoutManager = layoutManager


        var itemClickListener2 = object : ListContactClickListener {
            override fun onItemClicked(pos: Int) {
                showBurnDialog(pos)
            }
        }

        scratchAdapter = ScratchItemAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_scratch!!.adapter = scratchAdapter
    }


}
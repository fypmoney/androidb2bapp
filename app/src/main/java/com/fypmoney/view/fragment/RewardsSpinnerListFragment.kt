package com.fypmoney.view.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.activity.SpinWheelViewDark
import com.fypmoney.view.adapter.SpinnerAdapter

import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.NotificationViewModel
import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.dialog_burn_mynts.*
import kotlinx.android.synthetic.main.fragment_spinner_list.view.*


import kotlin.collections.ArrayList


class RewardsSpinnerListFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var sharedViewModel: RewardsViewModel? = null
    private var dialogDialog: Dialog? = null
    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: SpinnerAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_spinner_list, container, false)
        setRecyclerView(root!!)
        dialogDialog = Dialog(requireContext())
        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(RewardsViewModel::class.java)
            observeInput(sharedViewModel!!)

        }



        return root
    }

    private fun observeInput(sharedViewModel: RewardsViewModel) {

    }

    internal fun showBlockDialog() {


        dialogDialog!!.setCancelable(true)
        dialogDialog!!.setCanceledOnTouchOutside(true)
        dialogDialog!!.setContentView(R.layout.dialog_burn_mynts)

        val wlp = dialogDialog!!.window!!.attributes

        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT

        dialogDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog!!.window!!.attributes = wlp



        dialogDialog!!.clicked.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), SpinWheelViewDark::class.java)

            requireContext().startActivity(intent)
            dialogDialog!!.dismiss()
        })


        dialogDialog!!.show()
    }

    private fun setRecyclerView(root: View) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rv_assigned!!.layoutManager = layoutManager


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {

                showBlockDialog()
            }

            override fun onCallClicked(pos: Int) {

            }


        }

        typeAdapter = SpinnerAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_assigned!!.adapter = typeAdapter
    }

    private fun loadMore(root: View) {

        //LoadProgressBar?.visibility = View.VISIBLE
        root.LoadProgressBar?.visibility = View.VISIBLE
        Log.d("chorespage", page.toString())
        isLoading = true

    }

}
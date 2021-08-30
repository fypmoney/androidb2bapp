package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.model.ChoresTimeLineItem
import com.fypmoney.model.TaskDetailResponse
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility.makeTextViewResizable
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.adapter.ChoresStatusAdapter
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_response_task2.*

import kotlinx.android.synthetic.main.bottom_sheet_response_task2.view.*


class TaskActionBottomSheet(
    var onClickListener: AcceptRejectClickListener,
    var list: TaskDetailResponse,
    val value: Boolean?
) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    private var bottomSheetDialog: BottomSheetDialog? = null
    var otp = ObservableField<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog =
            (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
        return bottomSheetDialog!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_response_task2,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        if (ChoresActivity.mViewModel?.selectedPosition?.value == 0) {
            view.top_bg.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_blue
                )
            )
            view.bg_middle.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_blue
                )
            )
            view.viewdiv.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_blue
                )
            )
        } else if (ChoresActivity.mViewModel?.selectedPosition?.value == 1) {
            view.top_bg.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_pink
                )
            )
            view.viewdiv.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_pink
                )
            )
            view.bg_middle.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_pink
                )
            )
        } else if (ChoresActivity.mViewModel?.selectedPosition?.value == 2) {
            view.top_bg.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_green
                )
            )
            view.bg_middle.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_green
                )
            )
            view.viewdiv.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_green
                )
            )

        } else {
            view.top_bg.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_orange
                )
            )
            view.bg_middle.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_orange
                )
            )
            view.viewdiv.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_task_card_orange
                )
            )
        }

        setRecyclerView(view, list.choresTimeLine)
        if (list != null && list.choresTimeLine != null && list.choresTimeLine!!.isNotEmpty()) {
            view.active.text =
                list.choresTimeLine!![list.choresTimeLine!!.size - 1]?.name.toString()

        }

        view.back_close.setOnClickListener(View.OnClickListener {
            bottomSheetDialog?.dismiss()
        })


        loadImage(
            view.profile_pic,
            list.destinationUserProfilePic,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_user),
            true
        )
        val accept = view.findViewById<Button>(R.id.accept)!!
        view.emoji.text = list.emojis

        accept.setOnClickListener(View.OnClickListener {
            if (accept.text == "Accept") {
                ChoresActivity.mViewModel!!.callTaskAccept("ACCEPT", list.entityId.toString(), "")
            } else if (accept.text == "Pay") {

                onClickListener.onAcceptClicked(
                    56, comment.text?.trim()
                        .toString()
                )


            }


        })

        view.reject.setOnClickListener(View.OnClickListener {
            if (view.reject.text == "Reject") {
                ChoresActivity.mViewModel!!.callTaskAccept("REJECT", list.entityId.toString(), "")
            } else if (view.reject.text == "Depreciate") {
                ChoresActivity.mViewModel!!.callTaskAccept(
                    "DEPRECIATE", list.entityId.toString(), comment.text?.trim()
                        .toString()
                )

            }

        })
        view.cancel.setOnClickListener(View.OnClickListener {


            if (list.actionAllowed == "CANCEL") {
                ChoresActivity.mViewModel!!.callTaskAccept(
                    "CANCEL", list.entityId.toString(), comment.text?.trim()
                        .toString()
                )
            } else if (cancel.text == "Complete") {
                ChoresActivity.mViewModel!!.callTaskAccept(
                    "COMPLETE", list.entityId.toString(), comment.text?.trim()
                        .toString()
                )

            }

        })
        if (list.actionAllowed == "COMPLETE") {

            view.reject.text = "In process"

            view.bywhom.text = list.destinationUserName
            view.cancel.text = "Complete"
            view.accept.text = "Complete"
            view.lin.visibility = View.GONE
            view.cancel.visibility = View.VISIBLE
            view.comment.visibility = View.VISIBLE
        } else if (list.actionAllowed == "REJECT,ACCEPT") {
            accept.text = "Accept"
            view.reject.text = "Reject"
            view.bywhom.visibility = View.VISIBLE

            view.bywhom.text = list.destinationUserName
            view.comment.visibility = View.GONE
        } else if (list.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY") {
            accept.text = "Pay"
            view.reject.text = "Depreciate"

            view.bywhom.text = list.destinationUserName

            view.viewdiv.visibility = View.GONE
            view.comment.visibility = View.VISIBLE
        } else if (list.actionAllowed?.isEmpty() == true) {
            view.comment.visibility = View.GONE
            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE

            view.bywhom.text = list.destinationUserName


        } else if (list.actionAllowed == "CANCEL") {
            view.comment.visibility = View.VISIBLE
            view.lin.visibility = View.VISIBLE

            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE

            view.bywhom.text = list.destinationUserName
            view.cancel.visibility = View.VISIBLE

        }
        if (value!!) {
            view.assigned.text = "Assigned By"
        } else {
            view.assigned.text = "Assigned To"
        }
        var amount1 = list.additionalAttributes?.amount!! / 100
        view.amount.text = "₹" + amount1
        view.days_left.text = list.additionalAttributes?.numberOfDays.toString() + " day(s)"
        view.descrip.text = list.additionalAttributes?.description
        view.verification_title.text = list.additionalAttributes?.title
        if (!list.additionalAttributes?.description.isNullOrEmpty()) {
            makeTextViewResizable(view.descrip, 3, "view more", true);

        }


        return view
    }

    private fun setRecyclerView(root: View, choresTimeLine: List<ChoresTimeLineItem?>?) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.recycler_view!!.layoutManager = layoutManager

        var itemsArrayList: ArrayList<ChoresTimeLineItem> = ArrayList()
        choresTimeLine?.forEach { it ->
            if (it != null) {
                itemsArrayList.add(it)
            }
        }

        var typeAdapter =
            ChoresStatusAdapter(itemsArrayList)
        root.recycler_view!!.adapter = typeAdapter
    }

}
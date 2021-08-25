package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetResponseTaskBinding
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.ChoresTimeLineItem
import com.fypmoney.model.TaskDetailResponse
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.adapter.ChoresStatusAdapter
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_response_task.*
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.accept
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.amount
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.bywhom
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.cancel
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.comment
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.days_left
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.descrip
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.lin
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.reject
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.verification_title
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.viewdiv
import kotlinx.android.synthetic.main.bottom_sheet_response_task2.view.*
import kotlinx.android.synthetic.main.chores_status_row_item.view.*
import kotlinx.android.synthetic.main.toolbar.*


class TaskActionBottomSheet(
    var onClickListener: AcceptRejectClickListener,
    var list: TaskDetailResponse
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
        view.back_close.setOnClickListener(View.OnClickListener {
            bottomSheetDialog?.dismiss()
        })
        Utility.setImageUsingGlide(
            requireContext(),
            list.destinationUserProfilePic,
            view.profile_pic
        )
        val accept = view.findViewById<Button>(R.id.accept)!!
        view.emoji.text = list.emojis

        accept.setOnClickListener(View.OnClickListener {
            if (accept.text == "Accept") {
                ChoresActivity.mViewModel!!.callTaskAccept("ACCEPT", list.entityId.toString(), "")
            } else if (accept.text == "Appreciate") {

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

            view.reject.text = "In process2"

            view.cancel.text = "Complete"
            view.accept.text = "Complete"
            view.lin.visibility = View.GONE
            view.cancel.visibility = View.VISIBLE
            view.comment.visibility = View.VISIBLE
        } else if (list.actionAllowed == "REJECT,ACCEPT") {
            accept.text = "Accept"
            view.reject.text = "Reject"
            view.bywhom.visibility = View.VISIBLE
            view.assigned.text = "Assigned By"
            view.bywhom.text = list.destinationUserName
            view.comment.visibility = View.GONE
        } else if (list.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY") {
            accept.text = "Appreciate"
            view.reject.text = "Depreciate"
            view.assigned.text = "Assigned To"
            view.bywhom.text = list.destinationUserName
            view.days_left.visibility = View.GONE
            view.viewdiv.visibility = View.GONE
            view.comment.visibility = View.VISIBLE
        } else if (list.actionAllowed?.isEmpty() == true) {
            view.comment.visibility = View.GONE
            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE
            view.bywhom.text = list.sourceUserName
            view.assigned.text = "Assigned By"

        } else if (list.actionAllowed == "CANCEL") {
            view.comment.visibility = View.VISIBLE
            view.lin.visibility = View.VISIBLE

            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE
            view.assigned.text = "Assigned To"
            view.bywhom.text = list.destinationUserName
            view.cancel.visibility = View.VISIBLE

        }
        var amount1 = list.additionalAttributes?.amount!! / 100
        view.amount.text = "₹" + amount1
        view.days_left.text = list.additionalAttributes?.numberOfDays.toString() + " days"
        view.descrip.text = list.additionalAttributes?.description
        view.verification_title.text = list.additionalAttributes?.title

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
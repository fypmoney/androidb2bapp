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
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetResponseTaskBinding
import com.fypmoney.model.TaskDetailResponse
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_response_task.*
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.*


class TaskActionBottomSheet(
    var onClickListener: AcceptRejectClickListener,
    var list: TaskDetailResponse
) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_response_task,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        Log.d("chackbottomsheet", list.toString())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetResponseTaskBinding>(
            layoutInflater,
            R.layout.bottom_sheet_response_task,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)


        val accept = view.findViewById<Button>(R.id.accept)!!
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
            view.bywhom.text = "By " + list.destinationUserName
            view.comment.visibility = View.GONE
        } else if (list.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY") {
            accept.text = "Appreciate"
            view.reject.text = "Depreciate"
            view.bywhom.visibility = View.GONE
            view.days_left.visibility = View.GONE
            view.viewdiv.visibility = View.GONE
            view.comment.visibility = View.VISIBLE
        } else if (list.actionAllowed?.isEmpty() == true) {
            view.comment.visibility = View.GONE
            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE
            view.bywhom.text = "By " + list.sourceUserName

        } else if (list.actionAllowed == "CANCEL") {
            view.comment.visibility = View.VISIBLE
            view.lin.visibility = View.VISIBLE

            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE
            view.bywhom.text = "To " + list.destinationUserName
            view.cancel.visibility = View.VISIBLE

        }
        var amount1 = list.additionalAttributes?.amount!! / 100
        view.amount.text = "₹" + amount1
        view.days_left.text = list.additionalAttributes?.numberOfDays.toString() + " days"
        view.descrip.text = list.additionalAttributes?.description
        view.verification_title.text = list.additionalAttributes?.title

        return view
    }


}
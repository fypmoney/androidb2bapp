package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.model.NotificationModel
import com.fypmoney.model.NotificationTaskObjectModel
import com.fypmoney.view.activity.HomeView
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.bottom_sheet_response_task.*
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.*


class TaskActionBottomSheetnotification(
    var onClickListener: AcceptRejectClickListener,
    var list: NotificationModel.NotificationResponseDetails?
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


        val accept = view.findViewById<Button>(R.id.accept)!!

        getExtraDetail(list!!, view)

        accept.setOnClickListener(View.OnClickListener {
            if (accept.text == "Accept") {
                HomeView.mViewModel!!.callTaskAccept("ACCEPT", list?.entityId.toString(), "")
            } else if (accept.text == "Completed") {
                HomeView.mViewModel!!.callTaskAccept(
                    "COMPLETE", list?.entityId.toString(), view.comment.text?.trim()
                        .toString()
                )

            } else if (accept.text == "Pay") {
                onClickListener.onAcceptClicked(
                    56, view.comment.text?.trim()
                        .toString()
                )


            }


        })
        view.reject.setOnClickListener(View.OnClickListener {
            if (view.reject.text == "Reject") {
                HomeView.mViewModel!!.callTaskAccept("REJECT", list?.entityId.toString(), "")
            } else if (view.reject.text == "In process") {
                onClickListener.ondimiss()
            } else if (view.reject.text == "Depreciate") {

                HomeView.mViewModel!!.callTaskAccept(
                    "DEPRECIATE", list?.entityId.toString(), comment.text?.trim()
                        .toString()
                )

            }

        })
        if (list?.actionAllowed == "COMPLETE") {
            accept.text = "Completed"
            view.reject.text = "In process"
            view.bywhom.visibility = View.VISIBLE
            view.bywhom.text = "By " + list?.destinationUserName
            view.comment.visibility = View.VISIBLE
        } else if (list?.actionAllowed == "REJECT,ACCEPT") {
            accept.text = "Accept"
            view.reject.text = "Reject"
            view.bywhom.visibility = View.VISIBLE
            view.bywhom.text = "By " + list?.destinationUserName
            view.comment.visibility = View.GONE
        } else if (list?.actionAllowed == "DEPRECIATE,APPRECIATEANDPAY") {
            view.accept.text = "Pay"
            view.reject.text = "Depreciate"
            view.bywhom.visibility = View.GONE
            view.days_left.visibility = View.GONE
            view.viewdiv.visibility = View.GONE
            view.comment.visibility = View.VISIBLE
        } else if (list?.actionAllowed?.isEmpty() == true) {
            view.comment.visibility = View.GONE
            view.lin.visibility = View.GONE
            view.bywhom.visibility = View.VISIBLE

            view.bywhom.text = "By " + list?.destinationUserName


        }

//        view.days_left.text = list.additionalAttributes?.numberOfDays.toString() + " days"
//
//
//        view.amount.text="₹"+list.additionalAttributes?.amount
//        view.days_left.text=list.additionalAttributes?.numberOfDays.toString()+" days"
//        view.descrip.text=list.additionalAttributes?.description
//        view.verification_title.text=list.additionalAttributes?.title
        view.days_left.visibility = View.GONE
        view.viewdiv.visibility = View.GONE
        return view
    }

    private fun getExtraDetail(list: NotificationModel.NotificationResponseDetails, view: View) {

        val json = JsonParser().parse(list.objectJson) as JsonObject

        val task = Gson().fromJson(
            json,
            NotificationTaskObjectModel::class.java
        )
        view.amount.text = "₹" + task.amount?.div(100)
        view.descrip.text = list?.description
        view.verification_title.text = task.title
    }
}
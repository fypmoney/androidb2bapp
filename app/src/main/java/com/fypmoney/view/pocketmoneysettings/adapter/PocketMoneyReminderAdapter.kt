package com.fypmoney.view.pocketmoneysettings.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.ItemPocketMoneyReminderBinding
import com.fypmoney.model.DeletePocketMoneyReminder
import com.fypmoney.util.Utility
import com.fypmoney.view.pocketmoneysettings.model.DataItem
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyOtpVerifyResponse
import com.fypmoney.view.pocketmoneysettings.ui.EditPocketMoneyBottomSheet
import kotlinx.android.synthetic.main.dialog_delete_reminder_confirm.*

class PocketMoneyReminderAdapter(val childFragmentManager: FragmentManager, val context: Context) :
    ListAdapter<PocketMoneyReminderUiModel, PocketMoneyReminderAdapter.PocketMoneyReminderVH>(
        PocketMoneyReminderDiffUtil
    ), WebApiCaller.OnWebApiResponse {

    inner class PocketMoneyReminderVH(private val binding: ItemPocketMoneyReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PocketMoneyReminderUiModel) {
            binding.tvName.text = item.name
            binding.tvPocketAmount.text = String.format("₹" + item.amount.toString())
            binding.tvPocketReminderFrequency.text = item.frequency
            binding.tvMobileNumber.text = String.format("+91 " + item.mobile)

            binding.ivReminderEdit.setOnClickListener {
                openAddReminderBottomSheet(item)
            }

            binding.ivReminderDelete.setOnClickListener {
                callConfirmDisableNotificationDialog(context, item.mobile)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PocketMoneyReminderVH {
        val binding = ItemPocketMoneyReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PocketMoneyReminderVH(binding)
    }

    override fun onBindViewHolder(holder: PocketMoneyReminderVH, position: Int) {
        holder.bind(getItem(position))
    }

    private fun deletePocketMoneyReminder(deletePocketMoneyOtpReminder: DeletePocketMoneyReminder) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_DELETE_POCKET_MONEY_REMINDER,
                NetworkUtil.endURL(ApiConstant.API_DELETE_POCKET_MONEY_REMINDER),
                ApiUrl.PUT,
                deletePocketMoneyOtpReminder,
                this, isProgressBar = true
            )
        )
    }

    private fun openAddReminderBottomSheet(item: PocketMoneyReminderUiModel) {
        val editReminderBottomSheet = EditPocketMoneyBottomSheet()
        val bundle = Bundle()
        bundle.putString("name", item.name)
        bundle.putString("mobile", item.mobile)
        bundle.putInt("amount", item.amount!!)
        bundle.putString("frequency", item.frequency)
        editReminderBottomSheet.arguments = bundle
        editReminderBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        editReminderBottomSheet.show(childFragmentManager, "EditPocketMoneyBottomSheet")
    }

    override fun progress(isStart: Boolean, message: String) {}

    override fun onSuccess(purpose: String, responseData: Any) {
        when (purpose) {
            ApiConstant.API_DELETE_POCKET_MONEY_REMINDER -> {
                Utility.showToast(responseData.toString())
                if (responseData is PocketMoneyOtpVerifyResponse) {
                    Utility.showToast("Deleted successfully")
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        when (purpose) {
            ApiConstant.API_DELETE_POCKET_MONEY_REMINDER -> {
                Utility.showToast(errorResponseInfo.msg)
            }
        }
    }

    private fun callConfirmDisableNotificationDialog(context: Context, mobile: String) {

        val dialogDisableConfirm = Dialog(context)

        dialogDisableConfirm.setCancelable(false)
        dialogDisableConfirm.setCanceledOnTouchOutside(false)
        dialogDisableConfirm.setContentView(R.layout.dialog_delete_reminder_confirm)

        val wlp = dialogDisableConfirm.window?.attributes
        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogDisableConfirm.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDisableConfirm.window?.attributes = wlp

        dialogDisableConfirm.btnDeleteNotifications?.setOnClickListener {
            deletePocketMoneyReminder(
                DeletePocketMoneyReminder(
                    mobile = mobile
                )
            )
            //TODO close dialog on delete success response
            dialogDisableConfirm.dismiss()
        }

        dialogDisableConfirm.tvCancelNotificationClick.setOnClickListener {
            dialogDisableConfirm.dismiss()
        }

        dialogDisableConfirm.show()
    }

    override fun offLine() {}
}

@Keep
data class PocketMoneyReminderUiModel(
    var mobile: String,
    var name: String?,
    var amount: Int?,
    var frequency: String?
) {
    companion object {
        fun fromPocketMoneyReminderItem(dataItem: DataItem): PocketMoneyReminderUiModel {
            val mobile = dataItem.mobile
            val name = dataItem.name
            val amount = dataItem.amount
            val frequency = dataItem.frequency

            return PocketMoneyReminderUiModel(
                mobile = mobile.toString(),
                name = name,
                amount = amount,
                frequency = frequency
            )
        }
    }
}

object PocketMoneyReminderDiffUtil : DiffUtil.ItemCallback<PocketMoneyReminderUiModel>() {
    override fun areItemsTheSame(
        oldItem: PocketMoneyReminderUiModel,
        newItem: PocketMoneyReminderUiModel
    ): Boolean {
        return ((oldItem.mobile == newItem.mobile) && (oldItem.name == newItem.name) && (oldItem.amount == newItem.amount) && (oldItem.frequency == newItem.frequency))
    }

    override fun areContentsTheSame(
        oldItem: PocketMoneyReminderUiModel,
        newItem: PocketMoneyReminderUiModel
    ): Boolean {
        return oldItem == newItem
    }

}

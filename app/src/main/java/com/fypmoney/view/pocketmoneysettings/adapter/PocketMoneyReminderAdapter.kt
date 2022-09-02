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
import com.fypmoney.databinding.ItemPocketMoneyReminderBinding
import com.fypmoney.view.pocketmoneysettings.model.DataItem
import com.fypmoney.view.pocketmoneysettings.ui.EditPocketMoneyBottomSheet
import com.fypmoney.view.pocketmoneysettings.ui.PocketMoneySettingsFragment
import kotlinx.android.synthetic.main.dialog_delete_reminder_confirm.*

class PocketMoneyReminderAdapter(
    val childFragmentManager: FragmentManager,
    val context: Context,
    val onClickNotifyDelete:(mobileNumber: String) -> Unit,
    val clickNotify: PocketMoneySettingsFragment.OnClickListener
) :
    ListAdapter<PocketMoneyReminderUiModel, PocketMoneyReminderAdapter.PocketMoneyReminderVH>(
        PocketMoneyReminderDiffUtil
    ){

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
        editReminderBottomSheet.setOnEditActionCompleteListener(editNotifyListener)
    }

    private val editNotifyListener =
        object : EditPocketMoneyBottomSheet.OnEditActionCompleteListener {
            override fun onEditActionComplete(data: String) {
                clickNotify.onClick()
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
            onClickNotifyDelete(mobile)
            //TODO close dialog on delete success response
            dialogDisableConfirm.dismiss()
        }

        dialogDisableConfirm.tvCancelNotificationClick.setOnClickListener {
            dialogDisableConfirm.dismiss()
        }

        dialogDisableConfirm.show()
    }
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

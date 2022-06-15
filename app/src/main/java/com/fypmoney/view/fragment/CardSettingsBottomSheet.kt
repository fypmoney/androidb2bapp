package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetCardSettingsBinding
import com.fypmoney.model.BankProfileResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show card settings
* */
class CardSettingsBottomSheet(
    var bankProfileResponse: BankProfileResponseDetails?,
    var onCardSettingsClickListener: OnCardSettingsClickListener
) :
    BottomSheetDialogFragment(),
    MyProfileListAdapter.OnListItemClickListener {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_card_settings,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetCardSettingsBinding>(
            layoutInflater,
            R.layout.bottom_sheet_card_settings,
            null,
            false
        )
        val list = view.findViewById<ListView>(R.id.list)!!

        bottomSheet.setContentView(bindingSheet.root)
        val textString = ArrayList<String>()
        textString.add(PockketApplication.instance.getString(R.string.card_settings_block))

        textString.add(PockketApplication.instance.getString(R.string.card_settings_limit))
        textString.add(PockketApplication.instance.getString(R.string.card_settings_channels))
        bankProfileResponse?.cardInfos?.forEach {
            when (it.cardType) {
                AppConstants.CARD_TYPE_PHYSICAL -> {
                    if (it.status == AppConstants.ENABLE) {
/*
                        if(it.isPinSet.isNullOrEmpty() || it.isPinSet.equals("NO")){
*/
                        textString.add(PockketApplication.instance.getString(R.string.card_settings_pin))
                        /*  }*/
                    }
                }
            }
        }
        val drawableIds = ArrayList<Int>()

        /*drawableIds.add(R.drawable.lock)
        drawableIds.add(R.drawable.order)
        drawableIds.add(R.drawable.transaction)
        drawableIds.add(R.drawable.transaction)*/

        val myProfileAdapter = MyProfileListAdapter(
            requireContext(),
            this,
            PockketApplication.instance.getString(R.string.card_settings),
            0.0f
        )
        list.adapter = myProfileAdapter
        myProfileAdapter.setList(
            iconList1 = drawableIds,
            textString
        )
        return view
    }

    interface OnCardSettingsClickListener {
        fun onCardSettingsClick(position: Int, name: String?)
    }

    override fun onItemClick(position: Int, name: String?) {
        onCardSettingsClickListener.onCardSettingsClick(position, name)
        dismiss()
    }
}
package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetFilterByDateBinding
import com.fypmoney.databinding.BottomSheetTransactionFailBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_filter_by_date.*


/*
* This is used to show filter by date
* */
class FilterByDateFragment(
    private var onBottomSheetClickListener: OnFilterByDateClickListener
) : BottomSheetDialogFragment(), Utility.OnDateSelected {

    val whichOption = ObservableField<Boolean>()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_filter_by_date,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setCancelable(false)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetFilterByDateBinding>(
            layoutInflater,
            R.layout.bottom_sheet_filter_by_date,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val fromDate = view.findViewById<TextView>(R.id.from_date)!!
        val toDate = view.findViewById<TextView>(R.id.to_date)!!
        val clear = view.findViewById<TextView>(R.id.clear_text)!!
        val apply = view.findViewById<Button>(R.id.button_apply)!!

        fromDate.setOnClickListener {
            whichOption.set(true)
            try {
                Utility.showDatePickerDialog(context = requireContext(), this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        toDate.setOnClickListener {
            whichOption.set(false)
            try {
                Utility.showDatePickerDialog(context = requireContext(), this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        clear.setOnClickListener {
            fromDate.text = ""
            toDate.text = ""
        }
        apply.setOnClickListener {
            when {
                fromDate.text.isEmpty() -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.from_date_empty_error))
                }
                toDate.text.isEmpty() -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.to_date_empty_error))
                }

                !Utility.compareDates(
                    fromDate = fromDate.text.toString(),
                    toDate = toDate.text.toString()
                ) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.to_date_bigger))

                }

                else -> {
                    onBottomSheetClickListener.onFilterByDateButtonClick(
                        fromDate.text.toString(),
                        toDate.text.toString()
                    )
                    dismiss()
                }
            }


        }
        return view
    }

    interface OnFilterByDateClickListener {
        fun onFilterByDateButtonClick(fromDate: String, toDate: String)

    }

    override fun onDateSelected(dateOnEditText: String, dateOnServer: String, yearDifference: Int) {
        when (whichOption.get()) {
            true -> {
                from_date.text = dateOnEditText
            }
            else -> {
                to_date.text = dateOnEditText
            }
        }
    }

}
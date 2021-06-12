package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetAddNewCardBinding
import com.fypmoney.model.AddNewCardDetails
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/*
* This is used to show new card added
* */
class AddNewCardBottomSheet(
    private val amount: String?,
    private var onBottomSheetClickListener: OnAddNewCardClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_add_new_card,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetAddNewCardBinding>(
            layoutInflater,
            R.layout.bottom_sheet_add_new_card,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val cardNumber = view.findViewById<EditText>(R.id.enter_card)!!
        val cardName = view.findViewById<EditText>(R.id.name_card)!!
        val expiry = view.findViewById<EditText>(R.id.expiryValue)!!
        val cvv = view.findViewById<EditText>(R.id.cvv)!!
        val saveCardCheckbox = view.findViewById<CheckBox>(R.id.saveCardCheckbox)!!
        val btnAdd = view.findViewById<Button>(R.id.btnContinue)!!

        btnAdd.text = getString(R.string.add_btn_text) + " " + getString(R.string.Rs) + amount

        expiry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                if (start == 1 && start + added == 2 && p0?.contains('/') == false) {
                    expiry.setText("$p0/")
                    expiry.setSelection(expiry.length())
                } else if (start == 3 && start - removed == 2 && p0?.contains('/') == true) {
                    expiry.setText(p0.toString().replace("/", ""))
                    expiry.setSelection(expiry.length())

                }
            }
        })

        btnAdd.setOnClickListener {
            val expiryList = expiry.text.toString().split("/")
            val df: DateFormat =
                SimpleDateFormat("yy", Locale.getDefault()) // Just the year, with 2 digits
            val formattedDate: String = df.format(Calendar.getInstance().time)
            when {
                cardNumber.length() == 0 -> {
                    Utility.showToast(getString(R.string.card_number_empty_error))

                }
                cardName.length() == 0 -> {
                    Utility.showToast(getString(R.string.card_name_empty_error))

                }
                expiry.length() == 0 -> {
                    Utility.showToast(getString(R.string.card_expiry_empty_error))

                }
                expiry.length() < 5 -> {
                    Utility.showToast(getString(R.string.card_expiry_valid_empty_error))

                }

                expiryList[0] == "00" || expiryList[0] > "12" -> {
                    Utility.showToast(getString(R.string.card_expiry_month_valid_empty_error))

                }

                expiryList[1] < formattedDate -> {
                    Utility.showToast(getString(R.string.card_expiry_year_valid_empty_error))

                }

                cvv.length() < 3 -> {
                    Utility.showToast(getString(R.string.card_cvv_valid_error))

                }
                else -> {
                    onBottomSheetClickListener.onAddNewCardButtonClick(
                        AddNewCardDetails(
                            cardNumber = cardNumber.text.toString(),
                            nameOnCard = cardName.text.toString(),
                            expiryMonth = expiryList[0],
                            expiryYear = "20" + expiryList[1],
                            isCardSaved = saveCardCheckbox.isChecked,
                            cvv = cvv.text.toString()
                        )
                    )
                    dismiss()

                }
            }


        }

        return view
    }

    interface OnAddNewCardClickListener {
        fun onAddNewCardButtonClick(addNewCardDetails: AddNewCardDetails)

    }

}
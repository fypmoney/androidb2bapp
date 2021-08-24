package com.fypmoney.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import com.fypmoney.R
import com.fypmoney.connectivity.network.NetworkUtil


/**
 * Dialog Utils Class
 */
class DialogUtils {

    companion object {
        private var dialog: Dialog? = null
         var mAlertDialog: AlertDialog? = null
        fun showProgressDialog(context: Activity) {
            if (dialog != null && dialog!!.isShowing) {
                dialog?.dismiss()
            }
            dialog = Dialog(context)
            dialog?.setCancelable(false)
            dialog?.setContentView(R.layout.roket_loader)
            dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.show()
        }

        /**
         * Method to show alert confirmation dialog
         * isNegativeButtonRequired is used to check whether we need negative/ cancel button
         * unique identifier is used to identify which action to perform in case a class having multiple alert dialogs
         */

        fun showConfirmationDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String,
            negativeButtonText: String,
            isNegativeButtonRequired: Boolean,
            uniqueIdentifier: String,
            onAlertDialogClickListener: OnAlertDialogClickListener
        ) {
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                return
            }
            mAlertDialog = getAlertDialog(context, title, message, isNegativeButtonRequired)
            val positiveButton =
                mAlertDialog?.window!!.findViewById(R.id.button_ok) as Button
            val negativeButton = mAlertDialog?.window!!.findViewById(R.id.button_cancel) as Button

            positiveButton.text = positiveButtonText

            positiveButton.setOnClickListener {
                mAlertDialog?.dismiss()
                onAlertDialogClickListener.onPositiveButtonClick(uniqueIdentifier)
            }
            // set text and listener to negative button if is required
            if (isNegativeButtonRequired) {
                negativeButton.text = negativeButtonText
                negativeButton.setOnClickListener {
                    mAlertDialog?.dismiss()
                }
            }
        }

        /*
        * This method is used to get the progress dialog
        * */
        private fun getAlertDialog(
            context: Context,
            title: String,
            message: String, isNegativeButtonRequired: Boolean
        ): AlertDialog {
            val layoutInflater = LayoutInflater.from(context)
            val dialogView =
                layoutInflater.inflate(R.layout.dialog_layout_with_ok_cancel_option, null)
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setView(dialogView)

            val mAlertDialog = alertDialogBuilder.create()

            mAlertDialog.show()
            mAlertDialog.setCancelable(false)
            /*mAlertDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )*/
            val linearLayout =
                mAlertDialog.window!!.findViewById(R.id.linear_layout) as LinearLayout

            // Hide the negative button if not required and set the gravity of positive button to center horizontal
            if (!isNegativeButtonRequired) {
                val negativeButton =
                    (mAlertDialog.window!!.findViewById(R.id.button_cancel) as Button)
                negativeButton.visibility = View.GONE
                linearLayout.gravity = Gravity.CENTER_HORIZONTAL
            }
            val titleTextView =
                (mAlertDialog.window!!.findViewById(R.id.title) as TextView)

            // Hide the title if it is blank otherwise show the text
            if (title.isNotEmpty()) {
                titleTextView.text = title
            } else {
                titleTextView.visibility = View.GONE
            }

            (mAlertDialog.window!!.findViewById(R.id.message) as TextView).text = message
            mAlertDialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL)
            //mAlertDialog.window!!.setBackgroundDrawableResource(R.color.white)
            mAlertDialog.setCanceledOnTouchOutside(false)
            return mAlertDialog
        }

        /*
     * This method is used to internet error in the dialog
     * */
        fun showInternetErrorDialog(
            context: Context,
            onAlertDialogNoInternetClickListener: OnAlertDialogNoInternetClickListener
        ) {
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                return
            }

            val layoutInflater = LayoutInflater.from(context)
            val dialogView =
                layoutInflater.inflate(R.layout.error_layout, null)
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setView(dialogView)

            mAlertDialog = alertDialogBuilder.create()
            mAlertDialog?.setCancelable(false)
            mAlertDialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            mAlertDialog?.show()
            mAlertDialog?.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val tryAgain =
                (mAlertDialog?.window!!.findViewById(R.id.try_again) as AppCompatTextView)
            tryAgain.setOnClickListener {

                if (NetworkUtil.isNetworkAvailable()) {
                    mAlertDialog?.dismiss()
                    onAlertDialogNoInternetClickListener.onTryAgainClicked()

                }
            }
        }


        /*
            * This method is used to dismiss the progress bar
             */
        fun dismissProgressDialog() {
            try {
                if (dialog != null && dialog!!.isShowing) dialog?.dismiss()
            } catch (ignored: Exception) {
                dialog?.dismiss()
                ignored.printStackTrace()
            }
        }




    }


    interface OnAlertDialogClickListener {
        fun onPositiveButtonClick(uniqueIdentifier: String)
    }

    interface OnAlertDialogNoInternetClickListener {
        fun onTryAgainClicked()
    }



}

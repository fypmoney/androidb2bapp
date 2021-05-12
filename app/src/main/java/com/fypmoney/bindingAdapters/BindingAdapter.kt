package com.fypmoney.bindingAdapters


import android.graphics.Color
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.AppConstants
import com.google.android.material.card.MaterialCardView


/*
* Description : TO implement class Bind data with xml layouts
*/
class BindingAdapter {
    companion object {


        /**
         *  Method to Bind image in with the image view
         */

        @BindingAdapter("IMAGE_URL")
        @JvmStatic
        fun setImageUrl(imageView: AppCompatImageView, imageUrl: String?) {
            imageUrl?.let {
                Glide.with(PockketApplication.instance).load(imageUrl).override(200, 150)
                    .into(imageView).getSize(
                        SizeReadyCallback { width, height ->
                            //before you load image LOG height and width that u actually got?
                            // mEditDeskLayout.setImageSize(width,height);
                        })
                /*   Glide.with(DreamfolksApplication.instance)
                       .load(imageUrl)
                       .into(imageView)*/
            }


        }

        @BindingAdapter("setWebViewClient")
        @JvmStatic
        fun setWebViewClient(view: WebView, client: WebViewClient?) {
            view.webViewClient = client!!
        }

        @BindingAdapter("loadUrl")
        @JvmStatic
        fun loadUrl(view: WebView, url: String?) {
            view.loadUrl(url!!)
        }

        @BindingAdapter("STATUS")
        @JvmStatic
        fun setAddMemberTextStatus(view: AppCompatTextView, status: String) {
            when (status) {
                AppConstants.ADD_MEMBER_STATUS_INVITED -> {
                    view.text = PockketApplication.instance.getString(R.string.cancel_btn_text)
                }

            }
        }
        @BindingAdapter("STATUS_FOR_TEXT_COLOR")
        @JvmStatic
        fun setTextColor(view: AppCompatTextView, status: String) {
            when (status) {
                AppConstants.ADD_MEMBER_STATUS_INVITED -> {
                    view.setTextColor(ContextCompat.getColor(view.context, R.color.colorRed))

                }


            }
        }

        @BindingAdapter("CARD_BACKGROUND_COLOR")
        @JvmStatic
        fun setCardBackground(view: MaterialCardView, color: String?) {
            try {
                view.setCardBackgroundColor(Color.parseColor(color))
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
    }


}









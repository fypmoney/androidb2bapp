package com.fypmoney.bindingAdapters


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView


/*
* Description : TO implement class Bind data with xml layouts
*/
class BindingAdapter {
    companion object {
        /**
         *  Method to Bind image in with the image view
         */

        @BindingAdapter(value = ["position", "IMAGE_URL", "TYPE"], requireAll = false)
        @JvmStatic
        fun setImageUrl(
            imageView: AppCompatImageView,
            position: Int,
            imageUrl: String?,
                type: Int? = 0
        ) {

            imageUrl?.let {
                when (type) {
                    1 -> {
                        Glide.with(PockketApplication.instance).load(imageUrl).placeholder(
                            shimmerColorDrawable()
                        )
                            .into(imageView).getSize(
                                SizeReadyCallback { width, height ->
                                    //before you load image LOG height and width that u actually got?
                                    // mEditDeskLayout.setImageSize(width,height);
                                })
                    }
                    else -> {
                                Glide.with(PockketApplication.instance).load(imageUrl).placeholder(shimmerColorDrawable())
                                    .into(imageView).getSize(
                                        SizeReadyCallback { width, height ->
                                            //before you load image LOG height and width that u actually got?
                                            // mEditDeskLayout.setImageSize(width,height);
                                        })


                    }
                }


            }


        }


        @BindingAdapter("IMAGE")
        @JvmStatic
        fun setImage(imageView: AppCompatImageView, imageUrl: String?) {
            imageUrl?.let {
                Utility.setImageUsingGlide(PockketApplication.instance, imageUrl, imageView)
            }
        }

        @BindingAdapter("CIRCULARIMAGE")
        @JvmStatic
        fun setImage(imageView: CircleImageView, imageUrl: String?) {
            imageUrl?.let {
                Utility.setImageUsingGlide(PockketApplication.instance, imageUrl, imageView)
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        @BindingAdapter("SET_ALIGNMENT")
        @JvmStatic
        fun setLayoutAlignment(view: ConstraintLayout, isSender: String) {
            try {
                when (isSender) {
                    AppConstants.YES -> {
                        val textViewLayoutParams = RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        // add a rule to align to the left

                        // add a rule to align to the left
                        textViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)

                        setBackgroundDrawable(
                            view = view,
                            backgroundColor = ContextCompat.getColor(view.context,R.color.color_orange),
                            cornerRadius = 56.0f,
                            isRounded = false,
                            alpha = 20,
                            strokeColor = null,
                            strokeWidth = null)
                        // make sure the rule was applied
                        //2131362978
                        (view.getChildAt(0) as AppCompatImageView).setImageResource(0)
                        (view.getChildAt(0) as AppCompatImageView).setImageResource(R.drawable.ic_check_yellow)

                        // make sure the rule was applied
                        view.layoutParams = textViewLayoutParams
                    }
                    else -> {
                        val textViewLayoutParams = RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )

                        textViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                        // make sure the rule was applied
                        setBackgroundDrawable(
                            view = view,
                            backgroundColor = ContextCompat.getColor(view.context,R.color.color_task_card_green),
                            cornerRadius = 56.0f,
                            isRounded = false,
                            alpha = 20,
                            strokeColor = null,
                            strokeWidth = null)
                        (view.getChildAt(0) as AppCompatImageView).setImageResource(0)
                        (view.getChildAt(0) as AppCompatImageView).setImageResource(R.drawable.ic_check_with_background)

                        // make sure the rule was applied
                        view.layoutParams = textViewLayoutParams
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /**
         *  Method to Bind image in with the image view
         */

        @BindingAdapter("SET_IMAGE")
        @JvmStatic
        fun setImageInRelation(imageView: AppCompatImageView, position: Int) {
            if (position == 0) {
                imageView.setImageResource(R.drawable.ic_parent)
            } else if (position == 1) {
                imageView.setImageResource(R.drawable.ic_siblings)
            } else if (position == 2) {
                imageView.setImageResource(R.drawable.ic_spouse)
            } else if (position == 3) {
                imageView.setImageResource(R.drawable.ic_kid)
            } else if (position == 4) {
                imageView.setImageResource(R.drawable.ic_grand_parents)
            } else if (position == 5) {
                imageView.setImageResource(R.drawable.ic_grand_child)
            } else {
                imageView.setImageResource(R.drawable.ic_other)

            }
            // imageView.setImageResource(imageUrl)

        }

        /**
         *  Method to Bind image in with the image view
         */

        @BindingAdapter("SET_IMAGE_IN_NOTIFICATION")
        @JvmStatic
        fun setImageInNotification(imageView: AppCompatImageView, imageUrl: String?) {
            if (imageUrl != null)
                Utility.setImageUsingGlide2(PockketApplication.instance, imageUrl, imageView)
            else
            {
                imageView.setImageResource(R.drawable.ic_jay)
            }



        }

        /**
         *  Method to Bind image in with the image view
         */

        @BindingAdapter(value = ["position", "imageUrl"], requireAll = false)
        @JvmStatic
        fun setImageInUPI(imageView: AppCompatImageView, position: Int, imageUrl: Drawable?) {
            imageView.setImageDrawable(imageUrl)

        }

        /**
         *  Method to Bind image in with the image view
         */
        @BindingAdapter("SETIMAGE")
        @JvmStatic
        fun setImage(imageView: AppCompatImageView, imageUrl: Int?) {
            imageView.setImageResource(imageUrl!!)


        }

        @BindingAdapter(value = ["SET_IMAGE_IN_TRACK_ORDER", "IS_DONE"], requireAll = false)
        @JvmStatic
        fun setImageInOrderStatus(imageView: AppCompatImageView, status: String?, isDone: String?) {

            when (status) {
                AppConstants.ORDER_STATUS_ORDERED -> {
                    imageView.setImageResource(R.drawable.ic_order_placed)

                }
                AppConstants.ORDER_STATUS_IN_PROGRESS -> {
                    if (isDone == AppConstants.YES) {
                        imageView.setColorFilter(
                            ContextCompat.getColor(
                                PockketApplication.instance,
                                R.color.color_skyblue
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                    imageView.setImageResource(R.drawable.ic_in_progress)
                }
                AppConstants.ORDER_STATUS_SHIPPED -> {
                    if (isDone == AppConstants.YES) {
                        imageView.setColorFilter(
                            ContextCompat.getColor(
                                PockketApplication.instance,
                                R.color.color_skyblue
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                    imageView.setImageResource(R.drawable.ic_shipped)
                }
                AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY -> {
                    if (isDone == AppConstants.YES) {
                        imageView.setColorFilter(
                            ContextCompat.getColor(
                                PockketApplication.instance,
                                R.color.color_skyblue
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                    imageView.setImageResource(R.drawable.ic_out_for_delivery)
                }
                AppConstants.ORDER_STATUS_SEND_TO_VENDOR -> {
                    if (isDone == AppConstants.YES) {
                        imageView.setColorFilter(
                            ContextCompat.getColor(
                                PockketApplication.instance,
                                R.color.color_skyblue
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                    imageView.setImageResource(R.drawable.ic_send_to_vendor)
                }

                AppConstants.ORDER_STATUS_DELIVERED -> {
                    if (isDone == AppConstants.YES) {
                        imageView.setColorFilter(
                            ContextCompat.getColor(
                                PockketApplication.instance,
                                R.color.color_skyblue
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                    imageView.setImageResource(R.drawable.ic_deliverd)
                }
            }
        }

        @BindingAdapter(value = ["ORDER_STATUS", "IS_DONE_VALUE"], requireAll = false)
        @JvmStatic
        fun setTextColorInOrderStatus(
            textView: AppCompatTextView,
            status: String?,
            isDone: String?
        ) {
            when (status) {
                AppConstants.ORDER_STATUS_ORDERED,
                AppConstants.ORDER_STATUS_IN_PROGRESS,
                AppConstants.ORDER_STATUS_SHIPPED,
                AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY,
                AppConstants.ORDER_STATUS_DELIVERED,
                AppConstants.ORDER_STATUS_SEND_TO_VENDOR -> {
                    if (isDone == AppConstants.YES) {
                        textView.setTextColor(
                            ContextCompat.getColor(
                                PockketApplication.instance,
                                R.color.white
                            )
                        )

                    }
                }

            }
        }

        @BindingAdapter(
            value = ["SET_IMAGE_IN_TRACK_ORDER_LINE", "IS_DONE_VAL", "NEXT_IS_DONE"],
            requireAll = false
        )
        @JvmStatic
        fun setImageInLineOrderStatus(
            imageView: AppCompatImageView,
            status: String?,
            isDone: String?, nextIsDone: String?
        ) {
            when (status) {
                AppConstants.ORDER_STATUS_ORDERED, AppConstants.ORDER_STATUS_IN_PROGRESS, AppConstants.ORDER_STATUS_SHIPPED, AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY, AppConstants.ORDER_STATUS_DELIVERED, AppConstants.ORDER_STATUS_SEND_TO_VENDOR -> {
                    if (nextIsDone == AppConstants.YES) {
                        imageView.setImageResource(R.drawable.dotted_skyblue)
                    } else {
                        imageView.setImageResource(R.drawable.dotted_black)

                    }

                }
            }
        }


    }


}









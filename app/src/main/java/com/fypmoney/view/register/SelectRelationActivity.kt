package com.fypmoney.view.register

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivitySelectRelationshipBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.adapters.SelectRelationAdapter

import com.fypmoney.view.register.model.SendRelationSiblingParentResponse
import com.fypmoney.view.register.viewModel.SelectRelationVM
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.register.model.SelectRelationModel
import kotlinx.android.synthetic.main.toolbar_animation.*
import java.util.*
import kotlin.collections.ArrayList

class SelectRelationActivity : BaseActivity<ActivitySelectRelationshipBinding, SelectRelationVM>() {

    private var userType: String? = "90"
    private var nameOfUser: String? = null
    private var phone: String? = null
    private lateinit var binding: ActivitySelectRelationshipBinding
    private val selectRelationVM by viewModels<SelectRelationVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        observeEvents()

        setRecyclerview()
        binding.continueBtn.setOnClickListener(View.OnClickListener {

        })
        userType = intent.getStringExtra(AppConstants.USER_TYPE)
        setLottieAnimationToolBar(
            isBackArrowVisible = true,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift
        )// lottie anima

        phone = intent?.getStringExtra("phone")
        nameOfUser = intent?.getStringExtra("name_user")

        binding.textView2.text = "Tell us your relationship with " + nameOfUser
        binding.continueBtn.setOnClickListener(View.OnClickListener {
            if (selectRelationVM.selectedRelation.get() != null) {


                var relation =
                    selectRelationVM.selectedRelation.get()?.relationName?.toUpperCase(Locale.getDefault())
                if (relation == "KID") {
                    relation = "CHILD"

                }
                var selectRelationModel =
                    SendRelationSiblingParentResponse(nameOfUser, phone, "NO", relation)
                selectRelationVM.callIsAppUserApi(selectRelationModel, userType)
            } else {
                Utility.showToast("Select any relation")
            }


        })

    }

    private fun setRecyclerview() {
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvRelations.layoutManager = layoutManager
        var chooseRelationList: ArrayList<SelectRelationModel>? = ArrayList()
        chooseRelationList?.add(
            SelectRelationModel(
                0,
                ContextCompat.getDrawable(this, R.drawable.ic_kid_relation),
                "Kid",
                "#F2DC12"
            )
        )
        chooseRelationList?.add(
            SelectRelationModel(
                0,
                ContextCompat.getDrawable(this, R.drawable.ic_sibling_choose_relation),
                "Sibling",
                "#8ECFE3"
            )
        )
        chooseRelationList?.add(
            SelectRelationModel(
                0,
                ContextCompat.getDrawable(this, R.drawable.ic_parent_relation),
                "Parent",
                "#FA4856"
            )
        )
        chooseRelationList?.add(
            SelectRelationModel(
                0,
                ContextCompat.getDrawable(this, R.drawable.ic_spouse_relation),
                "Spouse",
                "#ABE6AF"
            )
        )
        var typeAdapter = SelectRelationAdapter(chooseRelationList, selectRelationVM)
        binding.rvRelations.adapter = typeAdapter
    }

    private fun observeEvents() {
        selectRelationVM.event.observe(this, {
            handelEvents(it)
        })
        selectRelationVM.user.observe(this, {

            val intent = Intent(this, PendingRequestActivity::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("name_user", nameOfUser)

            val bndlAnimation = ActivityOptions.makeCustomAnimation(
                applicationContext,
                com.fypmoney.R.anim.slideinleft,
                com.fypmoney.R.anim.slideinright
            ).toBundle()
            startActivity(intent, bndlAnimation)
            finishAffinity()


        })
    }

    private fun handelEvents(it: SelectRelationVM.HomeActivityEvent?) {
        when (it) {
            SelectRelationVM.HomeActivityEvent.NotificationClicked -> {
                startActivity(Intent(this, NotificationView::class.java))
            }
            SelectRelationVM.HomeActivityEvent.ProfileClicked -> {
                startActivity(Intent(this, UserProfileView::class.java))
            }

        }
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_select_relationship

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): SelectRelationVM = selectRelationVM


}
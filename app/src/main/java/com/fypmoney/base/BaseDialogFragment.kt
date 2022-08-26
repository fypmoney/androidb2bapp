package com.fypmoney.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.fypmoney.R
import com.fypmoney.util.autoCleared
import com.fypmoney.view.activity.LoginView
import com.fypmoney.view.activity.SplashView

abstract class BaseDialogFragment<VDB : ViewDataBinding> : DialogFragment() {

    protected var binding by autoCleared<VDB>()
    abstract val baseFragmentVM: BaseViewModel
    abstract val customTag: String
    open var widthHeightFull:Boolean = false

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false
        return DataBindingUtil.inflate<VDB>(inflater, layoutId, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // observeForError()
    }

   /* private fun observeForError() {
        baseFragmentVM.errorEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is BaseFragmentVM.ErrorEvent.OnErrorOccurred -> {
                    showSnackBar(it.message)
                }
                is BaseFragmentVM.ErrorEvent.OnSessionExpired -> {
                    navigateToLogin()
                }
            }
        })
    }*/

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginView::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()

    }

    override fun onResume() {
        super.onResume()
        if(widthHeightFull){
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        }else{
            val width = (resources.displayMetrics.widthPixels * .95).toInt()
            dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        }

    }
}
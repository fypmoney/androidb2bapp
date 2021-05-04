package com.dreamfolks.baseapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


//create base fragment with AndroidViewModel

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment() {

    private var baseActivity: BaseActivity<*, *>? = null
        private set
    private var mRootView: View? = null
    private var viewDataBinding: T? = null
    private var mViewModel: V? = null


    fun getViewDataBinding(): T {
        return viewDataBinding!!
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * @return layout resource id
     */
    abstract fun getLayoutId(): Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mViewModel = if (this.mViewModel == null)
            getViewModel() else this.mViewModel
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mRootView = viewDataBinding!!.root
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(getBindingVariable(), mViewModel)
        viewDataBinding!!.lifecycleOwner = this
        viewDataBinding!!.executePendingBindings()
    }

}

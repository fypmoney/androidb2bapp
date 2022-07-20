package com.fypmoney.view.arcadegames.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentLeaderBoardBinding
import com.fypmoney.view.arcadegames.viewmodel.FragmentLeaderBoardVM

class LeaderBoardFragment : BaseFragment<FragmentLeaderBoardBinding, FragmentLeaderBoardVM>() {

    private val leaderBoardFragmentVM by viewModels<FragmentLeaderBoardVM> { defaultViewModelProviderFactory }
    private var mViewBinding: FragmentLeaderBoardBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_board, container, false)
    }

    override fun onTryAgainClicked() {}

    private fun setBackgrounds() {
        mViewBinding?.let {
            setBackgroundDrawable(
                it.ivLeaderboardNumber1,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                2f,
                true
            )

            setBackgroundDrawable(
                it.ivLeaderboardNumber2,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                2f,
                true
            )

            setBackgroundDrawable(
                it.chipTicketView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.ivLeaderboardNumber3,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                2f,
                true
            )

        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_leader_board
    }

    override fun getViewModel(): FragmentLeaderBoardVM = leaderBoardFragmentVM

}
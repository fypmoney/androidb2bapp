package com.fypmoney.view.arcadegames.ui

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.view.arcadegames.adapter.LeaderBoardRulesAdapter
import com.fypmoney.view.arcadegames.adapter.LeaderBoardRulesUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LeaderBoardRulesBottomSheet(private val rulesList: List<String>) :
    BottomSheetDialogFragment() {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var recyclerViewLeaderBoardRules: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = (super.onCreateDialog(savedInstanceState) as BottomSheetDialog)
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = View.inflate(context, R.layout.bottom_sheet_leaderboard_rules, null)

        recyclerViewLeaderBoardRules = rootView.findViewById(R.id.leaderboard_rules_rv)
        setUpRecyclerView()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.skipCollapsed = true
        val layout: CoordinatorLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout)
        if (layout != null) {
            layout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }

    private fun setUpRecyclerView() {

        val leaderBoardImagesList: ArrayList<LeaderBoardRulesUiModel> = ArrayList()

        for (i in rulesList.indices) {
            leaderBoardImagesList.add(LeaderBoardRulesUiModel(rulesList[i]))
        }

        val leaderBoardRulesAdapter = LeaderBoardRulesAdapter(leaderBoardImagesList)

        with(recyclerViewLeaderBoardRules) {
            adapter = leaderBoardRulesAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

    }
}
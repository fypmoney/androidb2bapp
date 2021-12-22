package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.RelationModel
import com.fypmoney.view.adapter.RelationAdapter
import com.fypmoney.viewmodel.AddMemberViewModel

class RelationViewHelper(var adapter: RelationAdapter,var position: Int, var relationModel: RelationModel?,var viewModel:AddMemberViewModel) {
    var isBackgroundHighlight = ObservableField<Boolean>()

    fun init() {
        if (viewModel.selectedRelationPosition.get() == position) {
            isBackgroundHighlight.set(true)
        } else {
            isBackgroundHighlight.set(false)
        }

    }


    fun onItemClicked() {
//        if (viewModel.selectedRelationList.size < 1) {
//            relationModel?.isSelected = relationModel?.isSelected != true
//            viewModel.selectedRelationList.add(relationModel!!)
//            isBackgroundHighlight.set(true)
//        } else if (viewModel.selectedRelationList.size == 1) {
//            if (viewModel.selectedRelationList[0].relationName == relationModel?.relationName) {
//                relationModel?.isSelected = relationModel?.isSelected != true
//
//                relationModel?.let {
//                    viewModel.selectedRelationList.remove(it)
//
//                }
//            }
//
//        }
    }


}
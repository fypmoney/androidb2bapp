package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.RelationModel
import com.fypmoney.view.adapter.RelationTaskAdapter
import com.fypmoney.viewmodel.AddTaskViewModel

class RelationTaskViewHelper(var adapter: RelationTaskAdapter, var position: Int, var relationModel: RelationModel?, var viewModel:AddTaskViewModel) {
    var isBackgroundHighlight = ObservableField<Boolean>()

    fun init() {
    }


    fun onItemClicked() {
        if (viewModel.selectedRelationList.size < 1) {
            relationModel?.isSelected = relationModel?.isSelected != true
            viewModel.selectedRelationList.add(relationModel!!)
            isBackgroundHighlight.set(true)
        } else if (viewModel.selectedRelationList.size == 1) {
            if (viewModel.selectedRelationList[0].relationName == relationModel?.relationName) {
                relationModel?.isSelected = relationModel?.isSelected != true
                isBackgroundHighlight.set(false)
                relationModel?.let {
                    viewModel.selectedRelationList.remove(it)

                }
            }

        }
    }


}
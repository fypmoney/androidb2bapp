package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.InterestEntity
import com.fypmoney.viewmodel.SelectInterestViewModel

/*
* This is used to display all the interest in the list
* */
class ChooseInterestViewHelper(
    var position: Int,
    var interestResponseDetails: InterestEntity?,
    var viewModel: SelectInterestViewModel
) {
    var isBackgroundHighlight = ObservableField<Boolean>()
    fun init() {
    }

    /*
    * called when any item is selected
    * */
    fun onItemClicked() {
        interestResponseDetails?.isSelected = interestResponseDetails?.isSelected != true

        if (interestResponseDetails?.isSelected!!) {
            interestResponseDetails?.let { viewModel.selectedInterestList.add(it) }
            isBackgroundHighlight.set(true)
        } else {
            interestResponseDetails?.let { viewModel.selectedInterestList.remove(it) }
            isBackgroundHighlight.set(false)

        }


    }


}
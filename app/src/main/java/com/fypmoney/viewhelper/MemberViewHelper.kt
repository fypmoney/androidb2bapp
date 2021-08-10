package com.fypmoney.viewhelper

import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.view.adapter.MemberAdapter

/*
* This is used to display all the members in the list
* */
class MemberViewHelper(
    var position: Int,
    var memberEntity: MemberEntity?,
    var onMemberItemClickListener: MemberAdapter.OnMemberItemClickListener,
    var OnFamilyMemberClickListener: MemberAdapter.OnFamilyMemberClickListener?
) {

    fun init() {
    }


    fun onItemClicked() {
        onMemberItemClickListener.onItemClick(position)

    }

    fun onFamilyMemberClicked() {

        OnFamilyMemberClickListener?.onItemClick(memberEntity)
    }

}
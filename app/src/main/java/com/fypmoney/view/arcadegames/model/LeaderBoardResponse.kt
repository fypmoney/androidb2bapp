package com.fypmoney.view.arcadegames.model

import androidx.annotation.Keep

@Keep
data class LeaderBoardResponse(
	val data: Data3? = null
)

@Keep
data class LeaderBoardListItem(
	val goldenTickets: Int? = null,
	val position: String? = null,
	val userName: String? = null
)

@Keep
data class RewardProduct(
	val detailResource: String? = null,
	val appDisplayText: String? = null,
	val backgroundColor: Any? = null,
	val code: String? = null,
	val description: String? = null,
	val listResource: String? = null,
	val sectionId: Any? = null,
	val scratchResourceHide: Any? = null,
	val scratchResourceShow: Any? = null,
	val frequency: Int? = null,
	val rewardTypeOnFailure: String? = null,
	val sectionValue: Any? = null,
	val frequencyPlayed: Int? = null,
	val productPoints: Any? = null,
	val appDisplayTextColor: Any? = null,
	val name: String? = null,
	val additionalInfo: String? = null,
	val successResourceId: String? = null,
	val sectionList: Any? = null,
	val noOfJackpotTicket: Int? = null,
	val productType: String? = null,
	val endDate: String? = null,
	val startDate: String? = null,
	)

@Keep
data class Data3(
	val leaderBoardList: List<LeaderBoardListItem?>? = null,
	val rewardProduct: RewardProduct? = null,
	val currUserGoldenTickets: Int? = null
)


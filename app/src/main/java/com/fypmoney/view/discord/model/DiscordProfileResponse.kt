package com.fypmoney.view.discord.model

import androidx.annotation.Keep

@Keep
data class DiscordProfileResponse(
    val discordConnectedAppsDTO: List<DiscordConnectedAppsDTOItem?>? = null,
    val discordUserInfoDTO: DiscordUserInfoDTO? = null,
    val discordGuildsInfoDTO: List<DiscordGuildsInfoDTOItem?>? = null
)

@Keep
data class DiscordGuildsInfoDTOItem(
    val owner: String? = null,
    val features: List<String?>? = null,
    val permissions: String? = null,
    val roles: List<RolesItem?>? = null,
    val name: String? = null,
    val icon: String? = null,
    val id: Long? = null,
    val userId: Any? = null
)

@Keep
data class TagsItem(
    val botId: Long? = null
)

@Keep
data class DiscordConnectedAppsDTOItem(
    val friendSync: String? = null,
    val visibility: Int? = null,
    val showActivity: String? = null,
    val name: String? = null,
    val verified: String? = null,
    val id: String? = null,
    val type: String? = null
)

@Keep
data class DiscordUserInfoDTO(
    val flags: Int? = null,
    val verified: String? = null,
    val banner: String? = null,
    val avatar: String? = null,
    val locale: String? = null,
    val discriminator: Int? = null,
    val bannerColor: String? = null,
    val accentColor: String? = null,
    val mfaEnabled: Any? = null,
    val id: Long? = null,
    val publicFlags: Int? = null,
    val email: String? = null,
    val username: String? = null
)

@Keep
data class RolesItem(
    val color: String? = null,
    val unicodeEmoji: Any? = null,
    val permissions: String? = null,
    val managed: String? = null,
    val name: String? = null,
    val icon: String? = null,
    val mentionable: String? = null,
    val id: Long? = null,
    val position: Int? = null,
    val hoist: String? = null,
    val tags: Any? = null
)


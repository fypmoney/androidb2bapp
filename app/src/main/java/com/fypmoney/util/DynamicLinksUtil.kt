package com.fypmoney.util

import android.net.Uri

import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

object DynamicLinksUtil {
    fun generateInviteContent(content:String):String{
        return "$content ${generateReferralContentLink()}"
    }
    private fun generateReferralContentLink(): Uri {
        val baseUrl = Uri.parse("https://fypmoney.page.link")
        val domain = "https://fypmoney.page.page.link"

        val link = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)
                .setIosParameters(DynamicLink.IosParameters.Builder("com.fypmoney").build())
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.fypmoney").build())
                .buildDynamicLink()

        return link.uri
    }
}

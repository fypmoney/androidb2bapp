package com.fypmoney.util.dynamiclinks

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase


object DynamicLinksUtil {
    private val TAG: String? = DynamicLinksUtil::class.java.simpleName

    fun getInviteLinkWithExtraData(
        content: String,
        referralCode: String,
        onLinkGenerated: (link: String) -> Unit?
    ) {
        generateInviteLinkWithReferralCode(content, referralCode) {
            onLinkGenerated(it)
        }
    }

    fun getInviteLinkWithNoData(content: String): String {
        return "$content ${generateReferralContentLink()}"
    }

    private fun generateReferralContentLink(): Uri {
        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(Uri.parse(DYNAMIC_BASE_URL))
            .setDomainUriPrefix(DYNAMIC_DOMAIN)
            .setIosParameters(DynamicLink.IosParameters.Builder(DYNAMIC_ANDROID_APPLICATION_ID).build())
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(DYNAMIC_IOS_BUNDLE_ID).build())
            .buildDynamicLink()

        return link.uri
    }

    private fun generateInviteLinkWithReferralCode(
        content: String,
        referralCode: String,
        onLinkGenerated: (link: String) -> Unit?
    ) {
        val invitationLink = "$DYNAMIC_INVITE_LINK$referralCode"
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(invitationLink)
            domainUriPrefix = DYNAMIC_DOMAIN
            androidParameters(DYNAMIC_ANDROID_APPLICATION_ID) {

            }
            iosParameters(DYNAMIC_IOS_BUNDLE_ID) {
                appStoreId = DYNAMIC_APP_STORE_ID
            }
        }.addOnSuccessListener { shortDynamicLink ->
            onLinkGenerated(content + shortDynamicLink.shortLink.toString())
        }
    }


    fun getReferralCodeFromDynamicLink(intent: Intent, activity: Activity,
                                       onReferralCodeFound:(referralCode:String)->Unit) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(activity) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null &&
                    deepLink.getBooleanQueryParameter("invitedby", false)
                ) {
                    val referralCode = deepLink.getQueryParameter("invitedby")
                    Log.d(TAG, "${referralCode}")
                    referralCode?.let { onReferralCodeFound(it) }
                } else {
                    Log.d(TAG, "No code found")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Unable to attach dynamic link listener")

            }
    }
}

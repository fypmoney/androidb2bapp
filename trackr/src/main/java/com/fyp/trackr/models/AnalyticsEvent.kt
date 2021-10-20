package com.fyp.trackr.models

import android.os.Bundle
import com.fyp.trackr.base.Trackr
import com.fyp.trackr.services.TrackrServices
import org.json.JSONObject

data class AnalyticsEvent(var name: TrackrEvent = TrackrEvent.None,
                          var map: HashMap<String, Any> = hashMapOf(),
                          var services: ArrayList<TrackrServices> = arrayListOf()) {

    constructor(name: TrackrEvent = TrackrEvent.None,
                map: HashMap<String, Any> = hashMapOf(),
                service: TrackrServices = TrackrServices.MOENGAGE) : this(name, map, arrayListOf(service))

    //constructor(name: TrackrEvent = TrackrEvent.None) : this(name, hashMapOf(), arrayListOf(TrackrServices.CT, TrackrServices.SEGMENT))

    constructor(name: TrackrEvent = TrackrEvent.None, map: HashMap<String, Any> = hashMapOf()) : this(name, map, arrayListOf(TrackrServices.MOENGAGE))
    constructor(name: TrackrEvent = TrackrEvent.None, services: ArrayList<TrackrServices>) : this(name, hashMapOf(), services)

    private var dataMap = hashMapOf<String, Any>()
    private val common = CommonData

    init {
        dataMap = map
    }

    fun mapped() = getDataMap()

    private fun getDataMap() = if (dataMap.isEmpty()) map else dataMap

    fun add(key: TrackrField, value: Any?): AnalyticsEvent {
        if (value == null) return this
        getDataMap()[key.name] = value
        return this
    }

    fun add(service: TrackrServices): AnalyticsEvent {
        services.add(service)
        return this
    }

    fun data() = getDataMap()

    fun json() = JSONObject(getDataMap() as Map<*, *>)

    fun bundle(): Bundle {
        val bundle = Bundle()
        for (data_point in getDataMap()) {
            bundle.putString(data_point.key, data_point.value.toString())
        }
        return bundle
    }
}
// Easy Kotlin Extensions
fun AnalyticsEvent?.push() {
    Trackr.a(this)
}

// Easy Kotlin DSL for init
fun trackr(block: (AnalyticsEvent) -> Unit): AnalyticsEvent {
    val p = AnalyticsEvent()
    block(p)
    p.push()
    return p
}
// List of Events
enum class TrackrEvent(name: String) {

    None(""),
    LOGINSUCCESS("vp1kxg"),
    KYCCOMPLETD("m64ei2"),
    CHECKOFFER("check_offer"),
    PAYBUTTON("pay_button"),
    PERSONALISEDYOUCARD("personalised_card_complete"),
    ORDERSUCCESS("order_success"),
    ADDMONEYBUTTON("add_money_button"),
    SCRATCHCARDCONTINUE("scratch_card_continue"),
    LOGINSUCCESSVIEW("login_success_view"),
    HOMESCREEN("Home_Screen"),
    ORDEREDCARD("ordered_card"),
    PHONEVERIFICATION("Phone_verification"),
    ACCOUNTCREATED("Account_created"),
    ACCOUNTACTIVATION("Account_activation"),
    BANKVERIFICATION("Bank_verification"),
    AADHARVERIFICATION("Aadhar_verification"),
    OFFERTAB("Offer_tab"),
    OFFERCOPY("Offer_copy"),
    APPLAUNCH("App_launch"),
    FEEDLAUNCH("feed_launch"),
    PROFILEIMAGE("Profile_image"),
    REFTAB("Ref_tab"),
    ASSMISSION("Ass_Mission"),
    ADDMISSION("Add_misssion"),
    GIVENMISSIONSUCCESS("Miss_given_success"),
    MISSIONCOMPLETE("Miss_complete"),
    MISSIONPAID("Miss_paid"),
    FAMILYADD("Family_add"),
    LOADMONEYSUCCESS("Load_money_success"),
    LOADMONEYFAIL("Load_money_fail"),
    LOADUSERBACK("Load_user_back"),
    LOADMONEYBACKDISMISS("on_back_dismiss"),
    REFSUCCESS("Ref_success"),
    TRANSCATIONSUCCESS("Tran_success"),
    TRANSCATIONFALIURE("Tran_faliure"),
    LOADMONEYEXTERNALTERMINATE("load_money_external_terminate"),
    FORCEUPDATESCREENISSHOWN("Force_update_screen_is_shown"),
    FLEXIBLEUPDATEPOPUPISSHOWN("Flex_update_popup_is_shown"),
    FORCEUPDATECOMPLETED("Force_update_completed"),
    FORCEUPDATEFAILED("Force_update_failed"),
    NOAPPUPDATEAVAILBLE("NO_app_update_available"),

}

enum class TrackrField(name: String) {
    user_id("user_id"),
    user_mobile_no("user_mobile_no"),
    transaction_amount("transaction_amount"),
    added_family_member_mobile_no("added_family_member_mobile_no"),
    added_family_member_reletionship("added_family_member_reletionship"),
}
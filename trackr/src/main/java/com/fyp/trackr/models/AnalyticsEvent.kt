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

    constructor(name: TrackrEvent = TrackrEvent.None, map: HashMap<String, Any> = hashMapOf()) : this(name, map, arrayListOf(TrackrServices.MOENGAGE,TrackrServices.FIREBASE))
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
    check_offer("check_offer"),
    mobile_entered("mobile_entered"),
    otp_verified("otp_verified"),
    pay_button("pay_button"),
    personalised_card_complete("personalised_card_complete"),
    order_success("order_success"),
    add_money_button("add_money_button"),
    scratch_card_continue("scratch_card_continue"),
    login_success_view("login_success_view"),
    Home_Screen("Home_Screen"),
    ordered_card("ordered_card"),
    Phone_verification("Phone_verification"),
    Account_created("Account_created"),
    Account_activation("Account_activation"),
    Bank_verification("Bank_verification"),
    Aadhar_verification("Aadhar_verification"),
    Offer_tab("Offer_tab"),
    Offer_copy("Offer_copy"),
    App_launch("App_launch"),
    feed_launch("feed_launch"),
    Profile_image("Profile_image"),
    Ref_tab("Ref_tab"),
    Ass_Mission("Ass_Mission"),
    Add_misssion("Add_misssion"),
    Miss_given_success("Miss_given_success"),
    Miss_complete("Miss_complete"),
    Miss_paid("Miss_paid"),
    Family_add("Family_add"),
    Load_money_success("Load_money_success"),
    Load_money_fail("Load_money_fail"),
    Load_user_back("Load_user_back"),
    on_back_dismiss("on_back_dismiss"),
    Ref_success("Ref_success"),
    Tran_success("Tran_success"),
    Tran_faliure("Tran_faliure"),
    load_money_external_terminate("load_money_external_terminate"),
    Force_update_screen_is_shown("Force_update_screen_is_shown"),
    Flex_update_popup_is_shown("Flex_update_popup_is_shown"),
    Force_update_completed("Force_update_completed"),
    Force_update_failed("Force_update_failed"),
    NO_app_update_available("NO_app_update_available"),
    Open_rewards("Open_rewards"),
    Open_arcade("Open_arcade"),
    Spin("Spin"),
    SPINSUCCESS("Spin_success "),
    SPINZERO("Zero_spin"),
    SCRATCHCODE("Scratch"),
    SCRATCHSUCCESS("Scratch_success"),
    INSUFMYNTS("Insuf_mynts"),

}

enum class TrackrField(name: String) {
    user_id("user_id"),
    user_mobile_no("user_mobile_no"),
    spin_product_code("product_code"),
    transaction_amount("transaction_amount"),
    added_family_member_mobile_no("added_family_member_mobile_no"),
    added_family_member_reletionship("added_family_member_reletionship"),
}
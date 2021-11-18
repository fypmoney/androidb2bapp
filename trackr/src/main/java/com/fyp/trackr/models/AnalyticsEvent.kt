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
    home_screen("home_screen"),
    ordered_card("ordered_card"),
    phone_verification("phone_verification"),
    account_creation("account_creation"),
    account_activation("account_activation"),
    bank_verification("bank_verification"),
    kyc_verification("kyc_verification"),
    kyc_verification_teen("kyc_verification_teen"),
    kyc_verification_adult("kyc_verification_adult"),
    kyc_verification_other("kyc_verification_other"),
    offer_tab("offer_tab"),
    offer_copy("offer_copy"),
    app_launch("app_launch"),
    feed_launch("feed_launch"),
    profile_image("profile_image"),
    ref_tab("ref_tab"),
    assigned_mission("assigned_mission"),
    add_misssion("add_misssion"),
    mission_given_success("mission_given_success"),
    mission_complete("mission_complete"),
    mission_paid("mission_paid"),
    add_familymember("add_familymember"),
    load_money_success("load_money_success"),
    load_money_fail("load_money_fail"),
    load_user_back("load_user_back"),
    on_back_dismiss("on_back_dismiss"),
    refferal_shared("refferal_shared"),
    ref_applied_success("ref_applied_success"),
    tran_success("tran_success"),
    tran_faliure("tran_faliure"),
    load_money_external_terminate("load_money_external_terminate"),
    Force_update_screen_is_shown("Force_update_screen_is_shown"),
    flex_update_popup_is_shown ("flex_update_popup_is_shown "),
    force_update_completed("force_update_completed"),
    force_update_failed("force_update_failed"),
    no_app_update_available("no_app_update_available"),
    open_rewards("open_rewards"),
    open_arcade("open_arcade"),
    spin("spin"),
    spin_success("spin_success"),
    zero_spin("zero_spin"),
    scratch("scratch"),
    scratch_success("scratch_success"),
    insufficient_mynts("insufficient_mynts"),
    open_jackpot("open_jackpot"),
    ticket_win_success("ticket_win_success")
}

enum class TrackrField(name: String) {
    user_id("user_id"),
    user_mobile_no("user_mobile_no"),
    spin_product_code("product_code"),
    transaction_amount("transaction_amount"),
    added_family_member_mobile_no("added_family_member_mobile_no"),
    added_family_member_reletionship("added_family_member_reletionship"),
}
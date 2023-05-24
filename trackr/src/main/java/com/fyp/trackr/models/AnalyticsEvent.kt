package com.fyp.trackr.models

import android.os.Bundle
import androidx.annotation.Keep
import com.fyp.trackr.base.Trackr
import com.fyp.trackr.services.TrackrServices
import org.json.JSONObject
@Keep
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
    order_now("order_now"),
    mobile_entered("mobile_entered"),
    otp_verified("otp_verified"),
    pay_button("pay_button"),
    personalised_card_complete("personalised_card_complete"),
    order_success("order_success"),
    add_money_button("add_money_button"),
    scratch_card_continue("scratch_card_continue"),
    card_order_details_continue("card_order_details_continue"),
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
    no_more_coupon("no_more_coupon_code"),
    open_jackpot("open_jackpot"),
    ticket_win_success("ticket_win_success"),
    ref_from_invite_link("ref_from_invite_link"),
    card_activate_success("card_activate_success"),
    pin_success ("pin_success"),
    cust_type_select ("cust_type_select"),
    onboard_doc_sel ("onboard_doc_sel"),
    kyc_detail_fill_action ("kyc_detail_fill_action"),
    onboard_invite_enter_mobile ("onboard_invite_enter_mobile"),
    onboard_invite_choose_relation ("onboard_invite_choose_relation"),
    onboard_invite_share ("onboard_invite_share"),
    onboard_invite_skip ("onboard_invite_skip"),
    onboard_invite_use_myself ("onboard_invite_use_myself"),
    onboard_waitlist_have_number ("onboard_waitlist_have_number"),
    onboard_pending_approval_skip ("onboard_pending_approval_skip"),
    onboard_interest_selection ("onboard_interest_selection"),
    onboard_personal_offer_continue ("onboard_personal_offer_continue"),
    onboard_offer_screen_continue ("onboard_offer_screen_continue"),
    onboard_usertimeline_icon_click ("onboard_usertimeline_icon_click"),
    home_explore_click ("home_explore_click"),
    home_action_click ("home_action_click"),
    home_add_money_click ("home_add_money_click"),
    home_pay_money_click ("home_pay_money_click"),
    home_upi_click ("home_upi_click"),
    tab_explore_click ("tab_explore_click"),
    tab_reward_click ("tab_reward_click"),
    tab_family_click ("tab_family_click"),
    tab_insights_click ("tab_insights_click"),
    tab_home_click ("tab_home_click"),
    increase_limit_clicked("increase_limit_clicked"),
    upgrade_your_kyc_clicked("upgrade_your_kyc_clicked"),
    upgrade_to_aadhar_kyc_clicked("upgrade_to_aadhar_kyc_clicked"),
    get_otp_on_aadhar_clicked("get_otp_on_aadhar_clicked"),
    verify_otp_aadhar_clicked("verify_otp_aadhar_clicked"),
    upgrade_kyc_successfully("upgrade_kyc_successfully"),
    upgrade_kyc_from_pay_clicked("upgrade_kyc_from_pay_clicked"),
    upgrade_kyc_from_profile_clicked("upgrade_kyc_from_profile_clicked"),
    recharge_click("recharge_click"),
    postpaid_click("postpaid_click"),
    dth_click("dth_click"),
    broadband_click("broadband_click"),
    prepaid_enter_number("prepaid_enter_number"),
    postpaid_enter_number("postpaid_enter_number"),
    prepaid_choose_plan("prepaid_choose_plan"),
    dth_choose_operator("dth_choose_operator"),
    recharge_success("recharge_success"),
    recharge_fail("recharge_fail"),
    postpaid_success("postpaid_success"),
    postpaid_fail("postpaid_fail"),
    dth_success("dth_success"),
    dth_fail("dth_fail"),
    skip_to_home_click("skip_to_home_click"),
    gift_card_select("gift_card_select"),
    gift_card_pay("gift_card_pay"),
    gift_card_success("gift_card_success"),
    gift_card_fail("gift_card_fail"),
    gift_card_pending("gift_card_pending"),
    gift_card_history("gift_card_history"),
    gift_card_refresh("gift_card_refresh"),
    gift_card_details("gift_card_details"),
    insufficient_balance("insufficient_balance"),
    yes_bank_otp("yes_bank_otp"),


    //KYC AGENT
    agent_home("agent_home"),
    signup_shop_details_view("signup_shop_details_view"),
    signup_shop_details_submit("signup_shop_details_submit"),
    signup_upload_photo_view("signup_upload_photo_view"),
    signup_upload_photo_submit("signup_upload_photo_submit"),
    signup_aadhaar_view("signup_aadhaar_view"),
    signup_aadhaar_submit("signup_aadhaar_submit"),
    new_aadhaar_submit("new_aadhaar_submit"),
    new_aadhaar_view("new_aadhaar_view"),
    signup_choose_finger("signup_choose_finger"),
    new_choose_finger("new_choose_finger"),
    signup_activate_sensor_view("signup_activate_sensor_view"),
    new_activate_sensor_view("new_activate_sensor_view"),
    new_activate_sensor_fail("new_activate_sensor_fail"),
    signup_activate_sensor_fail("signup_activate_sensor_fail"),
    signup_activate_sensor_success("signup_activate_sensor_success"),
    new_activate_sensor_success("new_activate_sensor_success"),
    error_device_support("error_device_support"),
    error_no_driver("error_no_driver"),
    new_kyc_success("new_kyc_success"),
    new_kyc_fail("new_kyc_fail"),
    signup_kyc_fail("signup_kyc_fail"),
    signup_kyc_success("signup_kyc_success"),
    earnings_view("earnings_view"),


}

enum class TrackrField(name: String) {
    user_id("user_id"),
    cust_type("cust_type"),
    document_type("document_type"),
    status("status"),
    post_kyc_flag("post_kyc_flag"),
    relation_key("relation_key"),
    interest_code("interest_code"),
    form_which_screen("form_which_screen"),
    referral_code("referral_code"),
    user_mobile_no("user_mobile_no"),
    spin_product_code("product_code"),
    transaction_amount("transaction_amount"),
    added_family_member_mobile_no("added_family_member_mobile_no"),
    added_family_member_reletionship("added_family_member_reletionship"),
    explore_content_id("explore_content_id"),
    action_content_id("action_content_id"),
}
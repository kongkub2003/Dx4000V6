package com.emc.edc

sealed class Route(var route: String, var title: String) {


    object Home : Route("home", "Home")
    object Amount : Route("amount", "Amount")
    object WaitOperation : Route("wait_operation", "Wait Operation")
    object CardEntry : Route("card_entry", "Card Entry")
    object CardDisplay : Route("card_display", "Card Display")
    object KeyIn : Route("key_in", "Key In")
    object Setting : Route("setting", "Setting")
    object Select : Route("select", "Select")
    object Administrator : Route("administrator", "Administrator")
    object EditAmount : Route("edit_amount", "Confirm Amount")
    object TipAdjust : Route("tip_adjust", "Tip Adjust")
    object ConfirmTipAdjust : Route("confirm_tip_adjust", "Confirm Tip Adjust")
    object SearchTransaction : Route("search_transaction", "Search Transaction")
    object EnterPasswordPin : Route("enter_password_pin", "Enter Password Pin")
    object EnterApproveCode : Route("enter_prove_code", "Enter Approve Code")
    object TransactionDisplay : Route("transaction_display", "Transaction Display")
}

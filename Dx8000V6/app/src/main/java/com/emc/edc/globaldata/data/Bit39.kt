package com.emc.edc.globaldata.data

import com.emc.edc.globaldata.dataclass.Bit39

object Bit39 {
    val list = listOf(
        Bit39(
            resp_code = "00",
            meaning = "Approve"
        ),
        Bit39(
            resp_code = "01",
            meaning = "Refer to card issuer"
        ),
        Bit39(
            resp_code = "02",
            meaning = "Refer to card issuer's spectial conditions"
        ),
        Bit39(
            resp_code = "03",
            meaning = "Invalid merchant",
        ),
        Bit39(
            resp_code = "04",
            meaning = "Pick-up",
        ),
        Bit39(
            resp_code = "05",
            meaning = "Do not honour",
        ),
        Bit39(
            resp_code = "06",
            meaning = "Error",
        ),
        Bit39(
            resp_code = "07",
            meaning = "Pick-up, special condition",
        ),
        Bit39(
            resp_code = "08",
            meaning = "Honour with identification",
        ),
        Bit39(
            resp_code = "09",
            meaning = "Request in progress",
        ),
        Bit39(
            resp_code = "10",
            meaning = "Approve for partial amount",
        ),
        Bit39(
            resp_code = "11",
            meaning = "Approve [VIP]",
        ),
        Bit39(
            resp_code = "12",
            meaning = "Invalid transaction",
        ),
        Bit39(
            resp_code = "13",
            meaning = "Invalid amount",
        ),
        Bit39(
            resp_code = "14",
            meaning = "Invalid card number",
        ),
        Bit39(
            resp_code = "15",
            meaning = "No such issuer",
        ),
        Bit39(
            resp_code = "16",
            meaning = "Approve, update track 3",
        ),
        Bit39(
            resp_code = "17",
            meaning = "Customer cancellation",
        ),
        Bit39(
            resp_code = "18",
            meaning = "Customer dispute",
        ),
        Bit39(
            resp_code = "19",
            meaning = "Re-enter transaction",
        ),
        Bit39(
            resp_code = "20",
            meaning = "Invalid response",
        ),
        Bit39(
            resp_code = "21",
            meaning = "No action taken (unable to back out prior transaction)",
        ),
        Bit39(
            resp_code = "22",
            meaning = "Suspected malfunction",
        ),
        Bit39(
            resp_code = "23",
            meaning = "Unacceptable transaction fee",
        ),
        Bit39(
            resp_code = "24",
            meaning = "File update not supported by receiver",
        ),
        Bit39(
            resp_code = "25",
            meaning = "Unable to locate record on file/Terminal Inactive",
        ),
        Bit39(
            resp_code = "26",
            meaning = "Duplicate file update",
        ),
        Bit39(
            resp_code = "27",
            meaning = "File update field edit error",
        ),
        Bit39(
            resp_code = "28",
            meaning = "File update file locked out",
        ),
        Bit39(
            resp_code = "29",
            meaning = "File update not successful, contact acquirer",
        ),
        Bit39(
            resp_code = "30",
            meaning = "Format error",
        ),
        Bit39(
            resp_code = "31",
            meaning = "Bank not support by switch",
        ),
        Bit39(
            resp_code = "32",
            meaning = "Completed partially",
        ),
        Bit39(
            resp_code = "33",
            meaning = "Expired card",
        ),
        Bit39(
            resp_code = "34",
            meaning = "Suspected fraud",
        ),
        Bit39(
            resp_code = "35",
            meaning = "Card Acceptor contact acquirer",
        ),
        Bit39(
            resp_code = "36",
            meaning = "Restricted card",
        ),
        Bit39(
            resp_code = "37",
            meaning = "Card Acceptor contact acquirer security",
        ),
        Bit39(
            resp_code = "38",
            meaning = "Allowable PIN",
        ),
        Bit39(
            resp_code = "39",
            meaning = "No credit account",
        ),
        Bit39(
            resp_code = "40",
            meaning = "Requested function not support",
        ),
        Bit39(
            resp_code = "41",
            meaning = "Lost card",
        ),
        Bit39(
            resp_code = "42",
            meaning = "No universal accont",
        ),
        Bit39(
            resp_code = "43",
            meaning = "Stolen card",
        ),
        Bit39(
            resp_code = "44",
            meaning = "No investment account",
        ),
        Bit39(
            resp_code = "51",
            meaning = "Decline/Insufficient fund",
        ),
        Bit39(
            resp_code = "52",
            meaning = "No chequing account",
        ),
        Bit39(
            resp_code = "53",
            meaning = "No savings account",
        ),
        Bit39(
            resp_code = "54",
            meaning = "Expired card",
        ),
        Bit39(
            resp_code = "55",
            meaning = "Incorrect PIN",
        ),
        Bit39(
            resp_code = "56",
            meaning = "No card record",
        ),
        Bit39(
            resp_code = "57",
            meaning = "Transaction not permitted to cardholder",
        ),
        Bit39(
            resp_code = "58",
            meaning = "Transaction not permitted to terminal",
        ),
        Bit39(
            resp_code = "59",
            meaning = "Suspected fraud",
        ),
        Bit39(
            resp_code = "60",
            meaning = "Card acceptor contact acquirer",
        ),
        Bit39(
            resp_code = "61",
            meaning = "Exceeds withdrawal amount limit",
        ),
        Bit39(
            resp_code = "62",
            meaning = "Restricted card",
        ),
        Bit39(
            resp_code = "63",
            meaning = "Security violation",
        ),
        Bit39(
            resp_code = "64",
            meaning = "Original amount incorrect",
        ),
        Bit39(
            resp_code = "65",
            meaning = "Exceeds withdrawal frequency limit",
        ),
        Bit39(
            resp_code = "66",
            meaning = "Card acceptor call acquirer's security department",
        ),
        Bit39(
            resp_code = "67",
            meaning = "Hard capture (requires that card be picked up at ATM)",
        ),
        Bit39(
            resp_code = "68",
            meaning = "Response received too late",
        ),
        Bit39(
            resp_code = "75",
            meaning = "Allowable number of PIN tries exceeded",
        ),
        Bit39(
            resp_code = "76",
            meaning = "Unable to locate previous message (no match on retrieval reference number)",
        ),
        Bit39(
            resp_code = "77",
            meaning = "Previous message located for a repeat or reversal, but repeat or reversal data are inconsistent with original message",
        ),
        Bit39(
            resp_code = "78",
            meaning = "Blocked, first used’—The transaction is from a new cardholder, and the card has not been properly unblocked.",
        ),
        Bit39(
            resp_code = "80",
            meaning = "Bad Batch number",
        ),
        Bit39(
            resp_code = "81",
            meaning = "PIN cryptographic error found (error found by VIC security module during PIN decryption)",
        ),
        Bit39(
            resp_code = "82",
            meaning = "Negative CAM, dCVV, iCVV, or CVV results",
        ),
        Bit39(
            resp_code = "83",
            meaning = "Unable to verify PIN",
        ),
        Bit39(
            resp_code = "85",
            meaning = "No reason to decline a request for account number verification, address verification, CVV2 verification; or a credit voucher or merchandise return",
        ),
        Bit39(
            resp_code = "89",
            meaning = "Bad Terminal",
        ),
        Bit39(
            resp_code = "91",
            meaning = "Host error",
        ),
        Bit39(
            resp_code = "92",
            meaning = "Destination cannot be found for routing",
        ),
        Bit39(
            resp_code = "93",
            meaning = "Transaction cannot be completed, violation of law",
        ),
        Bit39(
            resp_code = "94",
            meaning = "Seqence error / Duplicate transmission",
        ),
        Bit39(
            resp_code = "95",
            meaning = "Reconcile error / Amount not agree",
        ),
        Bit39(
            resp_code = "96",
            meaning = "Invalid message / System malfunction, System malfunction or certain field error conditions",
        ),
        Bit39(
            resp_code = "99",
            meaning = "No promotion",
        ),

    )
}
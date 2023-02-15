package com.emc.edc.globaldata.data

import com.emc.edc.globaldata.dataclass.ISO8583

object ISO8583Data {
    val isoBitElement = listOf(
        ISO8583(
            bit = 0,
            length = 4,
            type = "n",
            name = "Message Type",
            key = "msg_type",
            format = ""
        ),
        ISO8583(
            bit = 1,
            length = 16,
            type = "b",
            name = "Bitmap",
            key = "bitmap",
            format = ""
        ),
        ISO8583(
            bit = 2,
            length = 19,
            type = "n",
            name = "Primary Account Number",
            key = "pan",
            format = "llvar"
        ),
        ISO8583(
            bit = 3,
            length = 6,
            type = "n",
            name = "Processing Code",
            key = "processing_code",
            format = ""
        ),
        ISO8583(
            bit = 4,
            length = 12,
            type = "n",
            name = "Amount, Transaction",
            key = "amount_transaction",
            format = ""
        ),
        ISO8583(
            bit = 5,
            length = 12,
            type = "n",
            name = "Amount, Settlement",
            key = "amount_settlement",
            format = ""
        ),
        ISO8583(
            bit = 6,
            length = 12,
            type = "n",
            name = "Amount, CardHolder Billing",
            key = "amount_cardholder",
            format = ""
        ),
        ISO8583(
            bit = 7,
            length = 10,
            type = "n",
            name = "Transmission Date and Time",
            key = "date_time",
            format = "mmddhhmmss"
        ),
        ISO8583(
            bit = 8,
            length = 8,
            type = "n",
            name = "Amount, CardHolder Billing fee",
            key = "amount_fee",
            format = ""
        ),
        ISO8583(
            bit = 9,
            length = 8,
            type = "n",
            name = "Conversion rate, Settlement",
            key = "conversion_rate_settlement",
            format = ""
        ),
        ISO8583(
            bit = 10,
            length = 8,
            type = "n",
            name = "Conversion rate, CardHolder Billing",
            key = "conversion_rate_card_holder",
            format = ""
        ),
        ISO8583(
            bit = 11,
            length = 6,
            type = "n",
            name = "System Trace Audit number (STAN)",
            key = "stan",
            format = ""
        ),
        ISO8583(
            bit = 12,
            length = 6,
            type = "n",
            name = "Time, Local Transaction",
            key = "time",
            format = "hhmmss"
        ),
        ISO8583(
            bit = 13,
            length = 4,
            type = "n",
            name = "Date, Local Transaction",
            key = "date",
            format = "mmdd"
        ),
        ISO8583(
            bit = 14,
            length = 4,
            type = "n",
            name = "Date, Expiration (Card Expire date)",
            key = "exp",
            format = "yymm"
        ),
        ISO8583(
            bit = 15,
            length = 4,
            type = "n",
            name = "Date, Settlement",
            key = "date_settlement",
            format = "mmdd"
        ),
        ISO8583(
            bit = 16,
            length = 4,
            type = "n",
            name = "Date, Conversion",
            key = "date_conversion",
            format = "mmdd"
        ),
        ISO8583(
            bit = 17,
            length = 4,
            type = "n",
            name = "Date, Capture",
            key = "date_capture",
            format = "mmdd"
        ),
        ISO8583(
            bit = 18,
            length = 4,
            type = "n",
            name = "Merchant's Type",
            key = "merchant_type",
            format = ""
        ),
        ISO8583(
            bit = 19,
            length = 3,
            type = "n",
            name = "Acquiring Institution Country",
            key = "acquiring_country",
            format = ""
        ),
        ISO8583(
            bit = 20,
            length = 3,
            type = "n",
            name = "Primary Account Number Extended, Country Code",
            key = "pan_with_country_code",
            format = ""
        ),
        ISO8583(
            bit = 21,
            length = 3,
            type = "n",
            name = "Forwarding Institution Country Code",
            key = "forward_country_code",
            format = ""
        ),
        ISO8583(
            bit = 22,
            length = 3,
            type = "n",
            name = "Point Of Service Entry Mode (POS Entry Mode)",
            key = "pos_entry_mode",
            format = ""
        ),
        ISO8583(
            bit = 23,
            length = 3,
            type = "n",
            name = "Card Sequence Number",
            key = "card_sequence_number",
            format = ""
        ),
        ISO8583(
            bit = 24,
            length = 3,
            type = "n",
            name = "Network International Identifier (NII)",
            key = "nii",
            format = ""
        ),
        ISO8583(
            bit = 25,
            length = 2,
            type = "n",
            name = "Point Of Service Condition Code (POS Condition Code)",
            key = "pos_condition_code",
            format = ""
        ),
        ISO8583(
            bit = 26,
            length = 2,
            type = "n",
            name = "Point Of Service PIN Capture Code (POS PIN Capture Code)",
            key = "pos_pin_capture_code",
            format = ""
        ),
        ISO8583(
            bit = 27,
            length = 1,
            type = "n",
            name = "Authorization Identification Response Length",
            key = "auth_iden_response_length",
            format = ""
        ),
        ISO8583(
            bit = 28,
            length = 8,
            type = "x+n",
            name = "Amount, Transaction Fee",
            key = "amount_transaction_fee",
            format = ""
        ),
        ISO8583(
            bit = 29,
            length = 8,
            type = "x+n",
            name = "Amount, Settlement Fee",
            key = "amount_settlement_fee",
            format = ""
        ),
        ISO8583(
            bit = 30,
            length = 8,
            type = "x+n",
            name = "Amount, Transaction Processing Fee",
            key = "amount_transaction_processing_fee",
            format = ""
        ),
        ISO8583(
            bit = 31,
            length = 8,
            type = "x+n",
            name = "Amount, Settlement Processing Fee",
            key = "amount_settlement_processing_fee",
            format = ""
        ),
        ISO8583(
            bit = 32,
            length = 11,
            type = "n",
            name = "Acquiring Institution Identification Code",
            key = "acquiring_institution_identification_code",
            format = "llvar"
        ),
        ISO8583(
            bit = 33,
            length = 11,
            type = "n",
            name = "Forwarding Institution Identification Code",
            key = "forwarding_institution_indentification_code",
            format = "llvar"
        ),
        ISO8583(
            bit = 34,
            length = 28,
            type = "n",
            name = "Primary Account Number Extended",
            key = "pan_extended",
            format = "llvar"
        ),
        ISO8583(
            bit = 35,
            length = 37,
            type = "z",
            name = "Track 2 data",
            key = "track2",
            format = "llvar"
        ),
        ISO8583(
            bit = 36,
            length = 28,
            type = "z",
            name = "Track 3 data",
            key = "track3",
            format = "lllvar"
        ),
        ISO8583(
            bit = 37,
            length = 12,
            type = "an",
            name = "Retrieval Reference Number",
            key = "ref_num",
            format = ""
        ),
        ISO8583(
            bit = 38,
            length = 6,
            type = "an",
            name = "Authorization Identification Response",
            key = "auth_id",
            format = ""
        ),
        ISO8583(
            bit = 39,
            length = 2,
            type = "an",
            name = "Response Code",
            key = "res_code",
            format = ""
        ),
        ISO8583(
            bit = 40,
            length = 3,
            type = "an",
            name = "Service Restriction Code",
            key = "service_restriction_code",
            format = ""
        ),
        ISO8583(
            bit = 41,
            length = 8,
            type = "ans",
            name = "Card Acceptor Terminal Identification (TID)",
            key = "tid",
            format = ""
        ),
        ISO8583(
            bit = 42,
            length = 15,
            type = "ans",
            name = "Card Acceptor Identification Code (MID)",
            key = "mid",
            format = ""
        ),
        ISO8583(
            bit = 43,
            length = 40,
            type = "ans",
            name = "Card Acceptor Name/Location",
            key = "card_acceptor",
            format = ""
        ),
        ISO8583(
            bit = 44,
            length = 25,
            type = "ans",
            name = "Additional Response data",
            key = "additional_res_data",
            format = "llvar"
        ),
        ISO8583(
            bit = 45,
            length = 76,
            type = "ans",
            name = "Track 1 data",
            key = "track1",
            format = "llvar"
        ),
        ISO8583(
            bit = 46,
            length = 999,
            type = "ans",
            name = "Additional data – ISO",
            key = "additional_iso",
            format = "llvar"
        ),
        ISO8583(
            bit = 47,
            length = 999,
            type = "ans",
            name = "Additional data – National",
            key = "additional_national",
            format = "lllvar"
        ),
        ISO8583(
            bit = 48,
            length = 999,
            type = "ans",
            name = "Additional data – Private",
            key = "additional_private",
            format = "lllvar"
        ),
        ISO8583(
            bit = 49,
            length = 999,
            type = "a_n",
            name = "Currency code, Transaction",
            key = "currency_code_transaction",
            format = ""
        ),
        ISO8583(
            bit = 50,
            length = 3,
            type = "a_n",
            name = "Currency code, Settlement",
            key = "currency_code_settlement",
            format = ""
        ),
        ISO8583(
            bit = 51,
            length = 3,
            type = "a_n",
            name = "Currency code, CardHolder Billing",
            key = "currency_code_billing",
            format = ""
        ),
        ISO8583(
            bit = 52,
            length = 64,
            type = "b",
            name = "Personal Identification Number (PIN)",
            key = "pin",
            format = ""
        ),
        ISO8583(
            bit = 53,
            length = 16,
            type = "b",
            name = "Security Related Control Information",
            key = "security_related",
            format = ""
        ),
        ISO8583(
            bit = 54,
            length = 120,
            type = "an",
            name = "Additional Amounts",
            key = "additional_amounts",
            format = "lllvar"
        ),
        ISO8583(
            bit = 55,
            length = 999,
            type = "b",
            name = "EMV Data",
            key = "emv",
            format = "lllvar"
        ),
        ISO8583(
            bit = 56,
            length = 999,
            type = "ans",
            name = "Reserved, ISO",
            key = "reserved_iso",
            format = "lllvar"
        ),
        ISO8583(
            bit = 57,
            length = 999,
            type = "ans",
            name = "Reserved, National",
            key = "reserved_national",
            format = "lllvar"
        ),
        ISO8583(
            bit = 58,
            length = 999,
            type = "ans",
            name = "Reserved, National",
            key = "reserved_iso",
            format = "lllvar"
        ),
        ISO8583(
            bit = 59,
            length = 999,
            type = "ans",
            name = "Reserved, National",
            key = "reserved_national",
            format = "lllvar"
        ),
        ISO8583(
            bit = 60,
            length = 999,
            type = "ans",
            name = "Reserved, Private",
            key = "reserved_private",
            format = "lllvar"
        ),
        ISO8583(
            bit = 61,
            length = 999,
            type = "ans",
            name = "Reserved, Private",
            key = "product_code",
            format = "lllvar"
        ),
        ISO8583(
            bit = 62,
            length = 999,
            type = "ans",
            name = "Reserved, Private",
            key = "invoice",
            format = "lllvar"
        ),
        ISO8583(
            bit = 63,
            length = 999,
            type = "ans",
            name = "Reserved, Private",
            key = "check_data",
            format = "lllvar"
        ),
        ISO8583(
            bit = 64,
            length = 64,
            type = "b",
            name = "Message Authentication Code Field (MAC)",
            key = "mac",
            format = ""
        ),
    )
}
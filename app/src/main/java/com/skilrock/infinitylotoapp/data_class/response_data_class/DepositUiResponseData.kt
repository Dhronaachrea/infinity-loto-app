package com.skilrock.infinitylotoapp.data_class.response_data_class


class DepositUiResponseData {
    var listPayTypeMap: ArrayList<PayTypeMap>? = null

    inner class PayTypeMap {
        var arrayId: String = ""
        var payTypeId = 0
        var payTypeCode: String = ""
        var payTypeDispCode: String = ""
        var subTypeCode: String = ""
        var subTypeValue: String = ""
        var accountDetail: ArrayList<AccountDetail>? = null
        var currencyMap: ArrayList<CurrencyMap>? = null
        var uiOrder = 0
        var minValue = 0.0
        var maxValue = 0.0
        var showAddNewAccBtn = false
        var editTextDefaultValue = ""
        var kycType = "" //OTP, MANUAL, NONE

        inner class AccountDetail {
            var accNum: String = ""
            var paymentAccId: Int = 0

            override fun toString(): String {
                return "AccountDetail(accNum='$accNum', paymentAccId=$paymentAccId)"
            }
        }

        inner class CurrencyMap {
            var code: String = ""
            var value: String = ""

            override fun toString(): String {
                return "CurrencyMap(code='$code', value='$value')"
            }
        }

        override fun toString(): String {
            return "PayTypeMap(arrayId='$arrayId', payTypeId=$payTypeId, payTypeCode='$payTypeCode', payTypeDispCode='$payTypeDispCode', subTypeCode='$subTypeCode', subTypeValue='$subTypeValue', accountDetail=$accountDetail, currencyMap=$currencyMap, uiOrder=$uiOrder, minValue=$minValue, maxValue=$maxValue, showAddNewAccBtn=$showAddNewAccBtn)"
        }

    }

    override fun toString(): String {
        return "DepositUiResponseData(listPayTypeMap=$listPayTypeMap)"
    }

}
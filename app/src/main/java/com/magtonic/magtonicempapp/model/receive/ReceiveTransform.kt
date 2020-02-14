package com.magtonic.magtonicempapp.model.receive

class ReceiveTransform {

    companion object {
        //const val ARR_FIELD = "dataList"
        //fun addToJsonArrayStr(str: String): String {

        //    return "{\"dataList\":$str}"
        //}

        fun restoreToJsonStr(str: String): String {
            return str.substring(1, str.length - 1)
        }
    }


}
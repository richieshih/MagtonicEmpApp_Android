package com.magtonic.magtonicempapp.data

class Constants {
    class ACTION {
        companion object {

            const val ACTION_LOGIN_ACTION : String = "com.magtonic.MagtonicEmpApp.LoginAction"
            const val ACTION_LOGIN_SUCCESS : String = "com.magtonic.MagtonicEmpApp.LoginSuccess"
            const val ACTION_LOGIN_FAILED : String = "com.magtonic.MagtonicEmpApp.LoginFailed"
            const val ACTION_LOGIN_NOT_SIGNIN : String = "com.magtonic.MagtonicEmpApp.LoginNotSignin"
            const val ACTION_SIGNIN_ACTION : String = "com.magtonic.MagtonicEmpApp.SigninAction"
            const val ACTION_SIGNIN_SUCCESS : String = "com.magtonic.MagtonicEmpApp.SigninSuccess"
            const val ACTION_SIGNIN_FAILED : String = "com.magtonic.MagtonicEmpApp.SigninFailed"
            const val ACTION_SIGNIN_ALREADY : String = "com.magtonic.MagtonicEmpApp.SigninAlready"
            const val ACTION_LOGIN_NETWORK_ERROR : String = "com.magtonic.MagtonicEmpApp.LoginNetworkError"
            const val ACTION_LOGIN_SERVER_ERROR : String = "com.magtonic.MagtonicEmpApp.LoginServerError"
            //const val ACTION_LOGIN_FRAGMENT_LOGIN_FAILED : String = "com.magtonic.MagtonicEmpApp.LoginFragmentLoginFailedAction"
            const val ACTION_NETWORK_FAILED : String = "com.magtonic.MagtonicEmpApp.ActionNetworkFailed"

            const val ACTION_LOGOUT_ACTION : String = "com.magtonic.MagtonicWarehoouse.LogoutAction"
            const val ACTION_HIDE_KEYBOARD : String = "com.magtonic.MagtonicEmpApp.HideKeyboardAction"
            //const val ACTION_HIDE_FAB_BUTTON : String = "com.magtonic.MagtonicEmpApp.HideFabButtonAction"

            const val ACTION_MAP_SHOW_CLEAR : String = "com.magtonic.MagtonicEmpApp.MapShowClear"
            const val ACTION_MAP_HIDE_CLEAR : String = "com.magtonic.MagtonicEmpApp.MapHideClear"
            const val ACTION_MAP_MARKER_CLEAR : String = "com.magtonic.MagtonicEmpApp.MapMarkerClear"

            //punch card
            const val ACTION_PUNCH_CARD_ACTION : String = "com.magtonic.MagtonicEmpApp.PunchCardAction"
            const val ACTION_PUNCH_CARD_SUCCESS : String = "com.magtonic.MagtonicEmpApp.PunchCardSuccess"
            const val ACTION_PUNCH_CARD_FAILED : String = "com.magtonic.MagtonicEmpApp.PunchCardFailed"
            const val ACTION_PUNCH_CARD_SERVER_ERROR : String = "com.magtonic.MagtonicEmpApp.PunchCardServerError"

            //history
            const val ACTION_HISTORY_CLEAR_SUCCESS : String = "com.magtonic.MagtonicEmpApp.HistoryClearSuccess"
        }

    }
}
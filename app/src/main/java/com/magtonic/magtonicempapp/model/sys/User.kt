package com.magtonic.magtonicempapp.model.sys



class User {

    companion object {
        const val USER_ACCOUNT = "userAccount"
        const val USER_NAME = "userName"
        const val PASSWORD = "password"
    }

    var userName: String = ""
    var userAccount: String = ""
    var password: String = ""
    //public String token;
    var isLogin = false
}
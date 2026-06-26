package com.dxmxp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private var _accessToken: String? = null
    val accessToken: String? get() = _accessToken

    fun saveToken(token: String) {
        _accessToken = token
    }

    fun clearSession() {
        _accessToken = null
    }

    fun isUserLoggedIn(): Boolean = _accessToken != null
}

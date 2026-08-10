package com.dxmxp.ui.common

import com.dxmxp.ui.navigation.model.Route

fun interface InitializableViewModel<S: Route> {
    fun init(route: S)
}
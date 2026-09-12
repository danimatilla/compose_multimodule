package com.dxmxp.navigation.common

import com.dxmxp.navigation.model.Route

fun interface InitializableViewModel<S: Route> {
    fun init(route: S)
}

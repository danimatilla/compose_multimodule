package com.dxmxp.navigation.common

import com.dxmxp.navigation.model.Route

fun interface InitializableViewModel<R: Route> {
    fun init(route: R)
}

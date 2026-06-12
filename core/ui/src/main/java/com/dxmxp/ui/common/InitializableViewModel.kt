package com.dxmxp.ui.common

import com.dxmxp.ui.navigation.Screen

fun interface InitializableViewModel<S: Screen> {
    fun init(screen: S)
}
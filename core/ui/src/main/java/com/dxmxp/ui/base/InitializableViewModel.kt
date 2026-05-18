package com.dxmxp.ui.base

import com.dxmxp.ui.navigation.Screen

interface InitializableViewModel<S: Screen> {
    fun init(screen: S)
}
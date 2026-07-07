package com.dxmxp.ui.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.classes
import com.lemonappdev.konsist.api.ext.list.functions
import com.lemonappdev.konsist.api.ext.list.properties
import com.lemonappdev.konsist.api.ext.list.withParentClassOf
import com.lemonappdev.konsist.api.verify.assertTrue
import com.dxmxp.ui.base.BaseViewModel
import org.junit.Test

class ViewModelArchitectureTest {

    @Test
    fun `view models state properties must be nullable`() {
        Konsist.scopeFromProject()
            .classes()
            .withParentClassOf(BaseViewModel::class)
            .classes()
            .filter { it.name == "State" }
            .properties()
            .filter { it.hasPublicModifier } // Only check public properties (actual state fields)
            .assertTrue { it.type?.isNullable == true }
    }

    @Test
    fun `view models should follow project structure`() {
        Konsist.scopeFromProject()
            .classes()
            .withParentClassOf(BaseViewModel::class)
            .assertTrue {
                // Check inner declarations existence
                val innerClasses = it.classes()
                val innerInterfaces = it.interfaces()
                
                val hasState = innerClasses.any { cls -> cls.name == "State" }
                val hasEvent = innerInterfaces.any { inter -> inter.name == "Event" }
                val hasEffect = innerInterfaces.any { inter -> inter.name == "Effect" }

                // Use simple text search on the class body to verify order
                val classText = it.text
                val stateIndex = classText.indexOf("class State")
                val eventIndex = classText.indexOf("interface Event")
                val effectIndex = classText.indexOf("interface Effect")

                val correctOrder = (stateIndex != -1 && eventIndex != -1 && effectIndex != -1) && 
                                   (stateIndex < eventIndex) && (eventIndex < effectIndex)

                hasState && hasEvent && hasEffect && correctOrder
            }
    }

    @Test
    fun `view models createInitialState should not initialize values`() {
        Konsist.scopeFromProject()
            .classes()
            .withParentClassOf(BaseViewModel::class)
            .functions()
            .filter { it.name == "createInitialState" }
            .assertTrue {
                // Check that it only returns "State()" (with optional expression body "=" or braces "{}")
                // And explicitly check it doesn't contain named parameters or assignments inside State()
                val normalizedText = it.text.replace("\\s".toRegex(), "")
                normalizedText.contains("createInitialState():State=State()") || 
                normalizedText.contains("createInitialState()=State()") ||
                normalizedText.contains("returnState()")
            }
    }
}

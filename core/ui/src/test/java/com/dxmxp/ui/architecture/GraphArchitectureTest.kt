package com.dxmxp.ui.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class GraphArchitectureTest {

    @Test
    fun `graph objects should follow the standard pattern`() {
        Konsist.scopeFromProject()
            .objects()
            .filter { it.hasParentInterface { parent -> parent.name == "Graph" } }
            .assertTrue { obj ->
                // 1. Mandatory Annotations
                val hasSerializable = obj.hasAnnotation { it.name == "Serializable" }
                val hasModule = obj.hasAnnotation { it.name == "Module" }
                val hasInstallIn = obj.hasAnnotation { 
                    it.name == "InstallIn" && it.text.contains("SingletonComponent") 
                }
                
                // 2. Mandatory Properties (MainScaffoldGraph pattern)
                val hasScreensProperty = obj.hasProperty { it.name == "screens" && it.hasOverrideModifier }
                val hasRouteProperty = obj.hasProperty { it.name == "route" && it.hasOverrideModifier }
                
                // 3. Mandatory Functions
                val hasRegisterEntries = obj.hasFunction { 
                    it.name == "registerEntries" && it.hasOverrideModifier 
                }
                
                // 4. Hilt Provision
                val hasHiltProvides = obj.functions().any { func ->
                    func.hasAnnotation { it.name == "Provides" } &&
                    func.hasAnnotation { it.name == "IntoSet" } &&
                    (func.name == "provideGraph" || func.name.startsWith("provide"))
                }

                hasSerializable && 
                hasModule && 
                hasInstallIn && 
                hasScreensProperty && 
                hasRouteProperty && 
                hasRegisterEntries && 
                hasHiltProvides
            }
    }

    @Test
    fun `graph implementations must be objects and have Graph suffix`() {
        // Ensure no regular classes implement Graph
        Konsist.scopeFromProject()
            .classes()
            .filter { it.hasParentInterface { parent -> parent.name == "Graph" } }
            .assertTrue { false } 

        // Ensure all Graph objects have the suffix
        Konsist.scopeFromProject()
            .objects()
            .filter { it.hasParentInterface { parent -> parent.name == "Graph" } }
            .assertTrue { it.name.endsWith("Graph") }
    }

    @Test
    fun `screens defined inside graphs should be serializable`() {
        Konsist.scopeFromProject()
            .objects()
            .filter { it.hasParentInterface { parent -> parent.name == "Graph" } }
            .assertTrue { graphObj ->
                val nestedScreens = (graphObj.classes() + graphObj.objects()).filter { 
                    it.hasParentInterface { parent -> parent.name == "Screen" } 
                }
                
                nestedScreens.all { it.hasAnnotation { annot -> annot.name == "Serializable" } }
            }
    }
}

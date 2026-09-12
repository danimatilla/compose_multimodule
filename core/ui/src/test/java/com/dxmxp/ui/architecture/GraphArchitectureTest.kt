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
                
                // 2. Mandatory Properties
                val hasRouteProperty = obj.hasProperty { it.name == "route" && it.hasOverrideModifier }
                
                // 3. Mandatory Functions
                val hasRegisterEntries = obj.hasFunction { 
                    it.name == "registerEntries" && it.hasOverrideModifier 
                }

                hasSerializable && 
                hasRouteProperty && 
                hasRegisterEntries
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

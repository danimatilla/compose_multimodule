package com.dxmxp.ui.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ScreenArchitectureTest {

    @Test
    fun `screen files should follow the pattern from LoginScreen`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.name.endsWith("Screen.kt") }
            .filterNot { it.path.contains("core/ui/src/main/java/com/dxmxp/ui/navigation/Screen.kt") }
            .assertTrue { file ->
                val functions = file.functions()
                val screenName = file.name.removeSuffix(".kt")
                
                val screenFunction = functions.firstOrNull { it.name == screenName }
                val hasScreenComposable = screenFunction?.hasAnnotation { it.name == "Composable" } == true
                
                if (hasScreenComposable) {
                    val hasContent = functions.any { 
                        it.name == "Content" && 
                        it.hasPrivateModifier && 
                        it.hasAnnotation { annot -> annot.name == "Composable" } 
                    }
                    
                    val hasOnEvent = screenFunction.parameters.any { it.name == "onEvent" }
                    val hasViewModel = screenFunction.parameters.any { it.name == "viewModel" }

                    hasContent && hasOnEvent && hasViewModel
                } else {
                    true
                }
            }
    }

    @Test
    fun `screens with viewmodels should collect effects and state`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.name.endsWith("Screen.kt") }
            .filterNot { it.path.contains("core/ui/src/main/java/com/dxmxp/ui/navigation/Screen.kt") }
            .assertTrue { file ->
                val screenName = file.name.removeSuffix(".kt")
                val screenFunction = file.functions().firstOrNull { it.name == screenName }
                val hasViewModel = screenFunction?.parameters?.any { it.name == "viewModel" } == true
                
                if (hasViewModel) {
                    val text = file.text
                    val collectsState = text.contains("collectAsState()") || text.contains("collectAsStateWithLifecycle()")
                    val collectsEffect = text.contains("viewModel.effect.collect") && text.contains("LaunchedEffect")
                    
                    collectsState && collectsEffect
                } else {
                    true
                }
            }
    }

    @Test
    fun `screens should pass state and onEvent to Content`() {
         Konsist.scopeFromProject()
            .files
            .filter { it.name.endsWith("Screen.kt") }
            .filterNot { it.path.contains("core/ui/src/main/java/com/dxmxp/ui/navigation/Screen.kt") }
            .assertTrue { file ->
                val screenName = file.name.removeSuffix(".kt")
                val screenFunction = file.functions().firstOrNull { it.name == screenName }
                val hasViewModel = screenFunction?.parameters?.any { it.name == "viewModel" } == true
                
                if (hasViewModel) {
                    val text = file.text
                    text.contains("Content(") && 
                    text.contains("state =") && 
                    (text.contains("onEvent = viewModel::setEvent") || text.contains("onEvent = { viewModel.setEvent(it) }"))
                } else {
                    true
                }
            }
    }

    @Test
    fun `Content composable should have state and onEvent parameters`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.name.endsWith("Screen.kt") }
            .filterNot { it.path.contains("core/ui/src/main/java/com/dxmxp/ui/navigation/Screen.kt") }
            .assertTrue { file ->
                val contentFunction = file.functions().firstOrNull { it.name == "Content" }
                if (contentFunction != null) {
                    val hasState = contentFunction.parameters.any { it.name == "state" }
                    val hasOnEvent = contentFunction.parameters.any { it.name == "onEvent" }
                    hasState && hasOnEvent
                } else {
                    true
                }
            }
    }
}

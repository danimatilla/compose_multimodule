package com.dxmxp.seed.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dxmxp.ui.navigation.helpers.NavigationHandler
import com.dxmxp.seed.navigation.routes.MainScaffoldGraph

@Composable
fun LoginScreen(onEvent: (NavigationHandler.NavigationEvent) -> Unit) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginViewModel.Effect.NavigateToMain -> {
                    onEvent(NavigationHandler.NavigationEvent.SetRootScreen(MainScaffoldGraph))
                }
                is LoginViewModel.Effect.ShowError -> {
                    // TODO: Show error message
                }
            }
        }
    }

    val username = state.username ?: ""
    val password = state.password ?: ""
    val isLoading = state.isLoading ?: false

    val showSplashScreen = isLoading && username.isEmpty() && password.isEmpty()

    if (showSplashScreen) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Seed App", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.setEvent(LoginViewModel.Event.OnUsernameChanged(it)) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setEvent(LoginViewModel.Event.OnPasswordChanged(it)) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.setEvent(LoginViewModel.Event.OnLoginClick) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = username.isNotBlank() && password.isNotBlank()
                ) {
                    Text(text = "Login")
                }
            }
        }
    }
}

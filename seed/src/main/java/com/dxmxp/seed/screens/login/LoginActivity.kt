package com.dxmxp.seed.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dxmxp.seed.MainActivity
import com.dxmxp.ui.theme.SeedTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is LoginContract.Effect.NavigateToMain -> {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        is LoginContract.Effect.ShowError -> {
                            Toast.makeText(this@LoginActivity, effect.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        setContent {
            SeedTheme {
                val state by viewModel.uiState.collectAsState()
                
                // We show the splash screen exclusively while isLoading is true 
                // AND the fields are empty.
                val showSplashScreen = state.isLoading && state.username.isEmpty() && state.password.isEmpty()

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
                    LoginScreen(
                        state = state,
                        onUsernameChanged = { viewModel.setEvent(LoginContract.Event.OnUsernameChanged(it)) },
                        onPasswordChanged = { viewModel.setEvent(LoginContract.Event.OnPasswordChanged(it)) },
                        onLoginClick = { viewModel.setEvent(LoginContract.Event.OnLoginClick) }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    state: LoginContract.State,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit
) {
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
            value = state.username,
            onValueChange = onUsernameChanged,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.username.isNotBlank() && state.password.isNotBlank()
            ) {
                Text(text = "Login")
            }
        }
    }
}

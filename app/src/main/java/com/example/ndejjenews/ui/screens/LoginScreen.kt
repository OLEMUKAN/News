package com.example.ndejjenews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lint.kotlin.metadata.Visibility
import androidx.navigation.NavController
import com.example.ndejjenews.R
import com.example.ndejjenews.ui.components.ErrorMessage
import com.example.ndejjenews.ui.components.LoadingIndicator
import com.example.ndejjenews.ui.components.PrimaryButton
import com.example.ndejjenews.ui.components.TextInput
import com.example.ndejjenews.utils.Constants.ROUTE_HOME
import com.example.ndejjenews.utils.Constants.ROUTE_REGISTER
import com.example.ndejjenews.viewmodel.AuthViewModel

/**
 * Login screen component
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    // Collect state from ViewModel
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    // Local UI state
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Handle successful authentication
    if (isAuthenticated) {
        navController.navigate(ROUTE_HOME) {
            popUpTo(ROUTE_HOME) { inclusive = true }
        }
        return
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo or icon
            Image(
                painter = painterResource(id = R.drawable.ic_unews_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(150.dp)
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
            
            // App title
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xlarge)))
            
            // Email input
            TextInput(
                value = email,
                onValueChange = { authViewModel.updateEmail(it) },
                labelText = stringResource(R.string.email),
                leadingIcon = Icons.Default.Email,
                isError = errorMessage != null && email.isBlank(),
                errorMessage = if (errorMessage != null && email.isBlank()) stringResource(R.string.email_required) else null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            
            // Password input
            TextInput(
                value = password,
                onValueChange = { authViewModel.updatePassword(it) },
                labelText = stringResource(R.string.password),
                leadingIcon = Icons.Default.Lock,
                isPassword = !passwordVisible,
                isError = errorMessage != null && password.isBlank(),
                errorMessage = if (errorMessage != null && password.isBlank()) stringResource(R.string.password_required) else null,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        authViewModel.signIn()
                    }
                )
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            
            // Error message
            if (errorMessage != null) {
                ErrorMessage(message = errorMessage!!)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            }
            
            // Login button
            PrimaryButton(
                text = stringResource(R.string.login),
                onClick = { authViewModel.signIn() },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            
            // Register link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.dont_have_account),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.register),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        // Clear form fields
                        authViewModel.clearError()
                        // Navigate to register screen
                        navController.navigate(ROUTE_REGISTER)
                    }
                )
            }
            
            // Loading indicator
            if (isLoading) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                LoadingIndicator(modifier = Modifier.size(48.dp))
            }
        }
    }
} 
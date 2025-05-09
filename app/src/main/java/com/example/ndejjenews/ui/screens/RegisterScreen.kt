package com.example.ndejjenews.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ndejjenews.R
import com.example.ndejjenews.ui.components.ErrorMessage
import com.example.ndejjenews.ui.components.LoadingIndicator
import com.example.ndejjenews.ui.components.NdejjeNewsTopAppBar
import com.example.ndejjenews.ui.components.PrimaryButton
import com.example.ndejjenews.ui.components.TextInput
import com.example.ndejjenews.utils.Constants.ROUTE_HOME
import com.example.ndejjenews.utils.Constants.ROUTE_LOGIN
import com.example.ndejjenews.utils.Constants.ROUTE_REGISTER
import com.example.ndejjenews.viewmodel.AuthViewModel

/**
 * Registration screen component
 */
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    // Collect state from ViewModel
    val displayName by authViewModel.displayName.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val confirmPassword by authViewModel.confirmPassword.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val registrationSuccess by authViewModel.registrationSuccess.collectAsState()
    
    // Local UI state
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    // Handle successful registration or authentication
    LaunchedEffect(isAuthenticated, registrationSuccess) {
        if (isAuthenticated) {
            // If already authenticated, navigate to home
            navController.navigate(ROUTE_HOME) {
                popUpTo(ROUTE_REGISTER) { inclusive = true }
            }
        } else if (registrationSuccess) {
            // Reset the success flag
            authViewModel.resetRegistrationSuccess()
            // Navigate to login screen after successful registration
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_REGISTER) { inclusive = true }
            }
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar
            NdejjeNewsTopAppBar(
                title = stringResource(R.string.register),
                onNavigateBack = { navController.navigateUp() }
            )
            
            // Registration form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.spacing_large))
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Form title
                Text(
                    text = stringResource(R.string.register_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
                
                // Display name input
                TextInput(
                    value = displayName,
                    onValueChange = { authViewModel.updateDisplayName(it) },
                    labelText = stringResource(R.string.display_name),
                    leadingIcon = Icons.Default.Person,
                    isError = errorMessage != null && displayName.isBlank(),
                    errorMessage = if (errorMessage != null && displayName.isBlank()) stringResource(R.string.display_name_required) else null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
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
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Confirm password input
                TextInput(
                    value = confirmPassword,
                    onValueChange = { authViewModel.updateConfirmPassword(it) },
                    labelText = stringResource(R.string.confirm_password),
                    leadingIcon = Icons.Default.Lock,
                    isPassword = !confirmPasswordVisible,
                    isError = errorMessage != null && (confirmPassword.isBlank() || confirmPassword != password),
                    errorMessage = if (errorMessage != null && confirmPassword != password) stringResource(R.string.passwords_not_match) 
                                  else if (errorMessage != null && confirmPassword.isBlank()) stringResource(R.string.confirm_password_required) 
                                  else null,
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
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
                            authViewModel.register()
                        }
                    )
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Error message
                if (errorMessage != null) {
                    ErrorMessage(message = errorMessage!!)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                }
                
                // Register button
                PrimaryButton(
                    text = stringResource(R.string.register),
                    onClick = { authViewModel.register() },
                    enabled = !isLoading && displayName.isNotBlank() && email.isNotBlank() && 
                              password.isNotBlank() && confirmPassword.isNotBlank()
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Already have an account link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.already_have_account),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(ROUTE_REGISTER) { inclusive = true }
                            }
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
} 
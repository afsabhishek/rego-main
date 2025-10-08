package com.rego.screens.joinus
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rego.screens.base.ProgressBarState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinUsParentScreen(
    onNavigateBack: () -> Unit = {},
    onRegistrationSuccess: (userId: String?, firebaseUid: String?) -> Unit
) {
    val viewModel: JoinUsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogData by remember { mutableStateOf<com.rego.screens.base.UIComponent.Dialog?>(null) }

    var showErrorScreen by remember { mutableStateOf(false) }
    var errorScreenData by remember { mutableStateOf<com.rego.screens.base.UIComponent.ErrorData?>(null) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var stateInput by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var showCompanyDropdown by remember { mutableStateOf(false) }
    var showRoleDropdown by remember { mutableStateOf(false) }

    val roles = listOf("CSM", "Sales Manager", "Agent", "Branch Manager", "Team Lead", "Area Manager")

    // Collect actions (one-time events)
    LaunchedEffect(Unit) {
        viewModel.action.collect { action ->
            when (action) {
                is JoinUsAction.ShowDialog -> {
                    errorDialogData = action.uiComponent
                    showErrorDialog = true
                }
                is JoinUsAction.ShowErrorScreen -> {
                    errorScreenData = action.uiComponent
                    showErrorScreen = true
                }
                is JoinUsAction.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = action.uiComponent.message,
                        actionLabel = action.uiComponent.buttonText,
                        duration = SnackbarDuration.Short
                    )
                }
                is JoinUsAction.RegistrationSuccess -> {
                    onRegistrationSuccess(action.data.userId, action.data.firebaseUid)
                }
            }
        }
    }

    // Error Dialog
    if (showErrorDialog && errorDialogData != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(errorDialogData!!.title) },
            text = { Text(errorDialogData!!.message) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Error Screen (Connection Error)
    if (showErrorScreen && errorScreenData != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorScreenData!!.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorScreenData!!.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                showErrorScreen = false
                viewModel.setEvent(JoinUsEvent.RetryLoadingCompanies)
            }) {
                Text(errorScreenData!!.buttonText)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Us") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Loading Indicator
            if (state.progressBarState == ProgressBarState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Partner with REGO",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Join our network and grow your insurance business",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state.progressBarState != ProgressBarState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = state.progressBarState != ProgressBarState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it
                        }
                    },
                    label = { Text("Phone Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    prefix = { Text("+91 ") },
                    enabled = state.progressBarState != ProgressBarState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state.progressBarState != ProgressBarState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = stateInput,
                    onValueChange = { stateInput = it },
                    label = { Text("State *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state.progressBarState != ProgressBarState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Insurance Company Dropdown
                ExposedDropdownMenuBox(
                    expanded = showCompanyDropdown,
                    onExpandedChange = {
                        if (state.progressBarState != ProgressBarState.Loading) {
                            showCompanyDropdown = it
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = state.selectedCompany?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Insurance Company *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showCompanyDropdown
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        enabled = state.progressBarState != ProgressBarState.Loading
                    )

                    ExposedDropdownMenu(
                        expanded = showCompanyDropdown,
                        onDismissRequest = { showCompanyDropdown = false }
                    ) {
                        state.insuranceCompanies.forEach { company ->
                            DropdownMenuItem(
                                text = { Text(company.name) },
                                onClick = {
                                    viewModel.setEvent(JoinUsEvent.SelectInsuranceCompany(company))
                                    showCompanyDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = showRoleDropdown,
                    onExpandedChange = {
                        if (state.progressBarState != ProgressBarState.Loading) {
                            showRoleDropdown = it
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showRoleDropdown
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        enabled = state.progressBarState != ProgressBarState.Loading
                    )

                    ExposedDropdownMenu(
                        expanded = showRoleDropdown,
                        onDismissRequest = { showRoleDropdown = false }
                    ) {
                        roles.forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption) },
                                onClick = {
                                    role = roleOption
                                    showRoleDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.setEvent(
                            JoinUsEvent.SubmitRegistration(
                                name = name,
                                email = email,
                                phoneNumber = phoneNumber,
                                city = city,
                                state = stateInput,
                                role = role,
                                company = state.selectedCompany?.name ?: ""
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = name.isNotBlank() &&
                            email.isNotBlank() &&
                            phoneNumber.length == 10 &&
                            city.isNotBlank() &&
                            stateInput.isNotBlank() &&
                            state.selectedCompany != null &&
                            role.isNotBlank() &&
                            state.progressBarState != ProgressBarState.Loading
                ) {
                    Text(
                        text = "Submit Application",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "* All fields are required",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
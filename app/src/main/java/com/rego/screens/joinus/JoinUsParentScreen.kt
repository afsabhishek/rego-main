package com.rego.screens.joinus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.base.UIComponent
import com.rego.screens.components.DropdownField
import com.rego.screens.components.RegoButton
import com.rego.screens.components.TransparentInputField
import com.rego.screens.joinus.data.InsuranceCompany
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.fontSemiBoldMontserrat
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun JoinUsParentScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: (userId: String?, firebaseUid: String?) -> Unit
){
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val viewModel: JoinUsViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogData by remember { mutableStateOf<UIComponent.Dialog?>(null) }

    var showErrorScreen by remember { mutableStateOf(false) }
    var errorScreenData by remember { mutableStateOf<UIComponent.ErrorData?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedCompanyObject by remember { mutableStateOf<InsuranceCompany?>(null) }


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
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DefaultScreenUI(progressBarState = state.value.progressBarState) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 8.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        tint = Color1A1A1A.copy(alpha = 0.9f),
                        modifier = Modifier
                            .width(28.dp)
                            .height(28.dp)
                            .padding(4.dp)
                            .padding(end = 4.dp)
                            .clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Join Us",
                        style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                        color = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.5f))
                        .height(2.dp)
                        .shadow(1.dp)
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = false
                ) { page ->
                    when (page) {
                        0 -> JoinUsFormScreen(
                            insuranceOptions = state.value.insuranceCompanies.map { it.name },
                            states = state.value.states,
                            stateCityMapping = state.value.stateCityMapping,
                            companyTypeOptions = listOf("CSM", "CR", "admin"),
                            onStateSelected = { selectedState ->
                                viewModel.onTriggerEvent(JoinUsEvent.SelectState(selectedState))
                            },
                            onInsuranceCompanySelected = { companyName ->
                                // Find the company object by name
                                selectedCompanyObject = state.value.insuranceCompanies.find { it.name == companyName }
                            },
                            onSubmit = { name, email, phone, city, st, insurance, companyType ->
                                viewModel.onTriggerEvent(
                                    JoinUsEvent.SubmitRegistration(
                                        name = name,
                                        email = email,
                                        phoneNumber = phone,
                                        city = city,
                                        state = st,
                                        company = insurance,
                                        role = companyType
                                    )
                                )
                            }
                        )

                        1 -> JoinUsSuccessScreen(onOkay = onRegistrationSuccess)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun JoinUsFormScreen(
    insuranceOptions: List<String>,
    states: List<String>,
    stateCityMapping: Map<String, List<String>>,
    onStateSelected: (String) -> Unit,
    onInsuranceCompanySelected: (String) -> Unit = {},
    onSubmit: (
        name: String,
        email: String,
        phone: String,
        city: String,
        state: String,
        insuranceCompany: String,
        companyType: String
    ) -> Unit,
    companyTypeOptions: List<String>
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // State/City managed for dropdowns
    var selectedState by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    var insuranceCompany by remember { mutableStateOf("") }
    var companyType by remember { mutableStateOf("") }

    // Dropdown expanded flags
    var isInsuranceDropdown by remember { mutableStateOf(false) }
    var isStateDropdown by remember { mutableStateOf(false) }
    var isCityDropdown by remember { mutableStateOf(false) }
    var isCompanyTypeDropdown by remember { mutableStateOf(false) }

    // Cities available for currently selected state
    val availableCities = stateCityMapping[selectedState].orEmpty()

    // Email & phone validation
    fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return regex.matches(email)
    }
    fun isValidPhone(phone: String): Boolean =
        phone.length == 10 && phone.all { it.isDigit() }

    val isFormValid =
        name.isNotBlank() &&
                isValidEmail(email) &&
                isValidPhone(phone) &&
                city.isNotBlank() &&
                selectedState.isNotBlank() &&
                insuranceCompany.isNotBlank() &&
                companyType.isNotBlank()

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 18.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Become a Insurance partner today!",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color1A1A1A,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Fill out the form and our team will get back to you.",
            color = Color1A1A1A_60(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
        )
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(18.dp)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                TransparentInputField(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it },
                    leadingIcon = R.drawable.person,
                    placeholder = "Enter Name"
                )
                TransparentInputField(
                    label = "Official Email ID",
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = R.drawable.email,
                    placeholder = "Enter Official E-mail Id",
                    keyboardType = KeyboardType.Email
                )
                TransparentInputField(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = { phone = it.filter { ch -> ch.isDigit() }.take(10) },
                    leadingIcon = R.drawable.phone,
                    placeholder = "Enter Phone number",
                    keyboardType = KeyboardType.Phone
                )

                // State & City side-by-side
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label = "State",
                            value = selectedState,
                            onValueChange = { st ->
                                selectedState = st
                                city = ""                 // reset city when state changes
                                onStateSelected(st)       // notify VM to refresh cities if needed
                            },
                            onDropdownExpand = { isStateDropdown = true },
                            expanded = isStateDropdown,
                            leadingIcon = R.drawable.location,
                            placeholder = "Select State",
                            onDismissRequest = { isStateDropdown = false },
                            options = states
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label = "City",
                            value = city,
                            onValueChange = { ct -> city = ct },
                            onDropdownExpand = {
                                if (selectedState.isNotBlank()) isCityDropdown = true
                            },
                            expanded = isCityDropdown,
                            leadingIcon = R.drawable.location,
                            placeholder = if (selectedState.isBlank()) "Select State first" else "Select City",
                            onDismissRequest = { isCityDropdown = false },
                            options = if (selectedState.isBlank()) emptyList() else availableCities
                        )
                    }
                }

                DropdownField(
                    label = "Insurance company",
                    value = insuranceCompany,
                    onValueChange = {
                        insuranceCompany = it
                        onInsuranceCompanySelected(it)
                    },
                    onDropdownExpand = { isInsuranceDropdown = true },
                    expanded = isInsuranceDropdown,
                    leadingIcon = R.drawable.location,
                    placeholder = "Select Insurance Company",
                    onDismissRequest = { isInsuranceDropdown = false },
                    options = insuranceOptions
                )

                DropdownField(
                    label = "Company Type", // fixed typo
                    value = companyType,
                    onValueChange = { companyType = it },
                    onDropdownExpand = { isCompanyTypeDropdown = true },
                    expanded = isCompanyTypeDropdown,
                    leadingIcon = R.drawable.location,
                    placeholder = "Select Company Type",
                    onDismissRequest = { isCompanyTypeDropdown = false },
                    options = companyTypeOptions
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        RegoButton(
            onClick = {
                onSubmit(
                    name,
                    email,
                    phone,
                    city,
                    selectedState,
                    insuranceCompany,
                    companyType
                )
            },
            text = "Submit",
            enabled = isFormValid
        )
    }
}


@Composable
private fun JoinUsSuccessScreen(onOkay: (String?, String?) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .background(Color00954D, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tick),
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "Response Submitted",
            color = Color1A1A1A,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Thank you for your interest. We will\nreach out to you within the next 24 hours.",
            color = Color1A1A1A_60(),
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 10.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(56.dp))
        RegoButton(
            onClick = onOkay as () -> Unit,
            text = "Okay"
        )
    }
}

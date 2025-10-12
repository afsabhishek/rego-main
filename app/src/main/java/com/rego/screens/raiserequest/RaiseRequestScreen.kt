// app/src/main/java/com/rego/screens/raiserequest/RaiseRequestScreen.kt
package com.rego.screens.raiserequest

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.base.EffectHandler
import com.rego.screens.base.ProgressBarState
import com.rego.screens.components.*
import com.rego.screens.raiserequest.data.*
import com.rego.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun RaiseRequestScreen(
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    val viewModel: RaiseRequestViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(RaiseRequestEvent.Init)
    }

    // Handle navigation actions
    EffectHandler(effectFlow = viewModel.action) { action ->
        when (action) {
            is RaiseRequestAction.NavigateToSuccess -> {
                onSubmit()
            }
            is RaiseRequestAction.ShowError -> {
                // Error is handled through the error flow
            }
        }
    }

    DefaultScreenUI(
        progressBarState = state.progressBarState,
        errors = viewModel.errors
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(ColorF9F9F9)
                .fillMaxWidth()
                .height(16.dp)
        )
        RaiseRequestScreenContent(
            state = state,
            onFieldChange = { field, value ->
                viewModel.setEvent(RaiseRequestEvent.FieldChanged(field, value))
            },
            onPartTypeSelect = { partType ->
                viewModel.setEvent(RaiseRequestEvent.SelectPartType(partType))
            },
            onVehicleVariantSelect = { variant ->
                viewModel.setEvent(RaiseRequestEvent.SelectVehicleVariant(variant))
            },
            onWorkshopDealerSelect = { dealer ->
                viewModel.setEvent(RaiseRequestEvent.SelectWorkshopDealer(dealer))
            },
            onSubmit = {
                viewModel.setEvent(RaiseRequestEvent.SubmitRequest)
            }
        )
    }
}

@Composable
fun RaiseRequestScreenContent(
    modifier: Modifier = Modifier,
    state: RaiseRequestViewState,
    onFieldChange: (String, Any) -> Unit,
    onPartTypeSelect: (PartTypeReference) -> Unit,
    onVehicleVariantSelect: (VehicleVariant) -> Unit,
    onWorkshopDealerSelect: (WorkshopDealer) -> Unit,
    onSubmit: () -> Unit = {},
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Dropdown expansion states
    var vehicleMakeExpanded by remember { mutableStateOf(false) }
    var vehicleModelExpanded by remember { mutableStateOf(false) }
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    var vehicleVariantExpanded by remember { mutableStateOf(false) }
    var workshopLocationExpanded by remember { mutableStateOf(false) }
    var workshopDealerExpanded by remember { mutableStateOf(false) }
    var policyTypeExpanded by remember { mutableStateOf(false) }

    // Image picker states
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var hasPermissions by remember { mutableStateOf(false) }
    val tempImageUriRemember = remember { mutableStateOf<Uri?>(null) }
    var lastImageSource by remember { mutableStateOf<String?>(null) }

    fun createTempImageUri(context: Context): Uri {
        val imageFileName = "temp_image_${System.currentTimeMillis()}.jpg"
        val storageDir = context.getExternalFilesDir("Pictures") ?: context.filesDir
        val tempFile = java.io.File(storageDir, imageFileName)

        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val newImages = state.images + it.toString()
            onFieldChange("images", newImages)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUriRemember.value?.let { uri ->
                val newImages = state.images + uri.toString()
                onFieldChange("images", newImages)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions[Manifest.permission.CAMERA] == true
        if (hasPermissions) {
            if (lastImageSource == "CAMERA") {
                try {
                    val uri = createTempImageUri(context)
                    tempImageUriRemember.value = uri
                    cameraLauncher.launch(uri)
                } catch (e: Exception) {
                    println("Error creating temp file: ${e.message}")
                }
            } else if (lastImageSource == "GALLERY") {
                galleryLauncher.launch("image/*")
            }
            lastImageSource = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Part Type Selection
        Text(
            text = "Part type",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90()
        )
        Text(
            text = "Choose part for repair",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.partTypes.isEmpty() && state.progressBarState == ProgressBarState.Idle) {
            Text(
                text = "Loading part types...",
                color = Color.Gray,
                fontSize = 12.sp
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.partTypes) { partType ->
                    PartTypeCard(
                        icon = getPartTypeIcon(partType.slug),
                        title = partType.name,
                        isSelected = state.selectedPartType?.id == partType.id,
                        onClick = { onPartTypeSelect(partType) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Part Photos Section
        Text(
            text = "Part photos",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90()
        )
        Text(
            text = "Upload photos of the part (Max 10 photos, 10MB each)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.images) { imageUri ->
                Box {
                    AsyncImage(
                        model = Uri.parse(imageUri),
                        contentDescription = "Selected Photo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete Photo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .padding(end = 5.dp, top = 5.dp)
                            .clickable {
                                val newImages = state.images.toMutableList()
                                    .apply { remove(imageUri) }
                                onFieldChange("images", newImages)
                            }
                    )
                }
            }

            // Add photo button (only show if less than 10 photos)
            if (state.images.size < 10) {
                item {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showImagePickerDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = "Add Photo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Add Photo", fontSize = 10.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Vehicle Details Section
        Text(
            text = "Vehicle Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90(),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Select your vehicle",
            fontSize = 12.sp,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Vehicle Make Dropdown
        DropdownField(
            label = "",
            value = state.selectedVehicleMake,
            onValueChange = { onFieldChange("selectedVehicleMake", it) },
            onDropdownExpand = { vehicleMakeExpanded = true },
            expanded = vehicleMakeExpanded,
            options = state.vehicleMakes.map { it },
            placeholder = "Select Car Make",
            onDismissRequest = { vehicleMakeExpanded = false },
            labelComposable = { LabelWithAsterisk("Car Make*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Model Dropdown (enabled only if make is selected)
        DropdownField(
            label = "",
            value = state.selectedVehicleModel,
            onValueChange = { onFieldChange("selectedVehicleModel", it) },
            onDropdownExpand = {
                if (state.selectedVehicleMake.isNotBlank()) {
                    vehicleModelExpanded = true
                }
            },
            expanded = vehicleModelExpanded,
            options = state.vehicleModels.map { it },
            placeholder = if (state.isLoadingModels) "Loading..." else "Select Car Model",
            onDismissRequest = { vehicleModelExpanded = false },
            labelComposable = { LabelWithAsterisk("Car Model*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fuel Type Dropdown (enabled only if model is selected)
        DropdownField(
            label = "",
            value = state.selectedFuelType,
            onValueChange = { onFieldChange("selectedFuelType", it) },
            onDropdownExpand = {
                if (state.selectedVehicleModel.isNotBlank()) {
                    fuelTypeExpanded = true
                }
            },
            expanded = fuelTypeExpanded,
            options = state.fuelTypes,
            placeholder = "Select Fuel Type",
            onDismissRequest = { fuelTypeExpanded = false },
            labelComposable = { LabelWithAsterisk("Fuel Type*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Variant Dropdown (enabled only if fuel type is selected)
        DropdownField(
            label = "",
            value = state.selectedVehicleVariant?.variant ?: "",
            onValueChange = { /* Handle through selection */ },
            onDropdownExpand = {
                if (state.selectedFuelType.isNotBlank()) {
                    vehicleVariantExpanded = true
                }
            },
            expanded = vehicleVariantExpanded,
            options = state.vehicleVariants.map { it.variant },
            placeholder = if (state.isLoadingVariants) "Loading..." else "Select Car Variant",
            onDismissRequest = { vehicleVariantExpanded = false },
            labelComposable = { LabelWithAsterisk("Car Variant*") }
        )

        // Handle variant selection in the dropdown
        if (vehicleVariantExpanded) {
            state.vehicleVariants.forEach { variant ->
                if (variant.variant == state.selectedVehicleVariant?.variant) {
                    // Already selected
                } else {
                    // Update selection when clicked
                    LaunchedEffect(variant) {
                        // This will be handled in the dropdown onClick
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Registration and Year Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TransparentInputField(
                modifier = Modifier.weight(1f),
                label = "",
                value = state.registrationNumber,
                onValueChange = {
                    // Format registration number as user types
                    val formatted = it.uppercase().take(13)
                    onFieldChange("registrationNumber", formatted)
                },
                placeholder = "DL01AB1234",
                labelComposable = { LabelWithAsterisk("Registration Number*") }
            )

            TransparentInputField(
                modifier = Modifier.weight(1f),
                label = "",
                value = state.makeYear,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 4) {
                        onFieldChange("makeYear", it)
                    }
                },
                placeholder = "2024",
                keyboardType = KeyboardType.Number,
                labelComposable = { LabelWithAsterisk("Make Year*") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Workshop Details Section
        Text(
            text = "Workshop Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90(),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Select workshop location and dealer",
            fontSize = 12.sp,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Inventory Pickup Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = state.isInventoryPickup,
                onCheckedChange = { onFieldChange("isInventoryPickup", it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color00954D,
                    uncheckedColor = Color00954D,
                    checkmarkColor = Color.White
                )
            )
            Text(
                text = "Is this an Inventory Pick up?",
                style = fontMediumMontserrat().copy(fontSize = 12.sp, color = Color1A1A1A_90())
            )
        }

        // Workshop Location Dropdown
        DropdownField(
            label = "",
            value = state.selectedWorkshopLocation,
            onValueChange = { onFieldChange("selectedWorkshopLocation", it) },
            onDropdownExpand = {
                if (state.selectedVehicleMake.isNotBlank()) {
                    workshopLocationExpanded = true
                }
            },
            expanded = workshopLocationExpanded,
            options = state.workshopLocations.map { it },
            placeholder = if (state.isLoadingLocations) "Loading..." else "Select Location",
            onDismissRequest = { workshopLocationExpanded = false },
            labelComposable = { LabelWithAsterisk("Workshop Location*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Workshop Dealer Dropdown
        DropdownField(
            label = "",
            value = state.selectedWorkshopDealer?.dealerName ?: "",
            onValueChange = { /* Handle through selection */ },
            onDropdownExpand = {
                if (state.selectedWorkshopLocation.isNotBlank()) {
                    workshopDealerExpanded = true
                }
            },
            expanded = workshopDealerExpanded,
            options = state.workshopDealers.map { it.dealerName },
            placeholder = if (state.isLoadingDealers) "Loading..." else "Select Dealer",
            onDismissRequest = { workshopDealerExpanded = false },
            labelComposable = { LabelWithAsterisk("Workshop Dealer*") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Advisor Details Section
        Text(
            text = "Advisor Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90(),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Enter advisor information",
            fontSize = 12.sp,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TransparentInputField(
            label = "",
            value = state.advisorName,
            onValueChange = { onFieldChange("advisorName", it) },
            placeholder = "Enter Advisor Name",
            labelComposable = { LabelWithAsterisk("Advisor Name*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransparentInputField(
            label = "",
            value = state.advisorContactNumber,
            onValueChange = {
                if (it.all { char -> char.isDigit() } && it.length <= 10) {
                    onFieldChange("advisorContactNumber", it)
                }
            },
            placeholder = "Enter 10-digit number",
            keyboardType = KeyboardType.Phone,
            labelComposable = { LabelWithAsterisk("Advisor Contact Number*") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Policy Details Section
        Text(
            text = "Policy Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90(),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Enter policy information",
            fontSize = 12.sp,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DropdownField(
            label = "",
            value = state.selectedPolicyType,
            onValueChange = { onFieldChange("selectedPolicyType", it) },
            onDropdownExpand = { policyTypeExpanded = true },
            expanded = policyTypeExpanded,
            options = state.policyTypes,
            placeholder = "Select Policy Type",
            onDismissRequest = { policyTypeExpanded = false },
            labelComposable = { LabelWithAsterisk("Policy Type*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransparentInputField(
            label = "",
            value = state.claimNumber,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    onFieldChange("claimNumber", it)
                }
            },
            placeholder = "Enter Claim Number",
            keyboardType = KeyboardType.Number,
            labelComposable = { LabelWithAsterisk("Claim Number*") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Submit Button
        RegoButton(
            onClick = onSubmit,
            text = if (state.progressBarState == ProgressBarState.Loading) "Submitting..." else "Submit",
            enabled = state.isFormValid && state.progressBarState != ProgressBarState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form validation hint
        if (!state.isFormValid) {
            Text(
                text = "* All fields marked with asterisk are required",
                fontSize = 10.sp,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Image picker dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Select Image") },
            text = { Text("Choose an option to add a photo") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        if (hasPermissions) {
                            try {
                                val uri = createTempImageUri(context)
                                tempImageUriRemember.value = uri
                                cameraLauncher.launch(uri)
                            } catch (e: Exception) {
                                println("Error creating temp file: ${e.message}")
                            }
                        } else {
                            lastImageSource = "CAMERA"
                            permissionLauncher.launch(
                                arrayOf(Manifest.permission.CAMERA)
                            )
                        }
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        lastImageSource = "GALLERY"
                        galleryLauncher.launch("image/*")
                        showImagePickerDialog = false
                    }
                ) {
                    Text("Gallery")
                }
            }
        )
    }
}

@Composable
fun LabelWithAsterisk(text: String) {
    val isRequired = text.endsWith("*")
    if (!isRequired) {
        Text(
            text = text,
            style = TextStyle(
                color = Color1A1A1A_60(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    } else {
        val label = text.removeSuffix("*")
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color1A1A1A_90(), fontSize = 12.sp)) {
                    append(label)
                }
                withStyle(SpanStyle(color = Color(0xFFE7503D))) {
                    append("*")
                }
            }
        )
    }
}

@Composable
fun PartTypeCard(
    icon: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp, 100.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isSelected) 1.dp else 1.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(24.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color00954D,
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 8.dp)
                    ),
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.tick),
                    contentDescription = title,
                    tint = Color.White
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(32.dp)
                .align(Alignment.TopCenter)
                .background(color = Color.Black.copy(alpha = 0.06f), shape = CircleShape),
        ) {
            Icon(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = if (isSelected) Color(0xFF4CAF50) else Color1A1A1A_60()
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(34.dp)
                .fillMaxWidth()
                .background(
                    if (isSelected) Color(0xFFEFFDEF) else ColorFBFBFB,
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color1A1A1A_87()
            )
        }
    }
}

// Helper function to get icon based on part type slug
fun getPartTypeIcon(slug: String): Int {
    return when (slug.uppercase()) {
        "ALLOY_WHEELS" -> R.drawable.alloy_wheel
        "HEADLAMPS" -> R.drawable.car_light
        "PLASTIC" -> R.drawable.car_bumper
        "LEATHER_FABRIC" -> R.drawable.car_seat
        else -> R.drawable.alloy_wheel
    }
}

@Preview(showBackground = true)
@Composable
fun RaiseRequestScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        RaiseRequestScreen()
    }
}
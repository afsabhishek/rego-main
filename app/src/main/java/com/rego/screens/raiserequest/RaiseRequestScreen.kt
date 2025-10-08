package com.rego.screens.raiserequest

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.rego.screens.components.DropdownField
import com.rego.screens.components.RegoButton
import com.rego.screens.components.TransparentInputField
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_87
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.ColorF9F9F9
import com.rego.ui.theme.ColorFBFBFB
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontMediumMontserrat
import org.koin.androidx.compose.koinViewModel

data class PartType(
    val id: String,
    val title: String,
    val icon: Int
)

@Composable
fun RaiseRequestScreen(
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    val viewModel: RaiseRequestViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(RaiseRequestEvent.Init)
    }

    DefaultScreenUI(progressBarState = state.progressBarState) { paddingValues ->
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
            onSubmit = onSubmit
        )
    }
}

@Composable
fun RaiseRequestScreenContent(
    modifier: Modifier = Modifier,
    state: RaiseRequestViewState,
    onFieldChange: (String, Any) -> Unit,
    onSubmit: () -> Unit = {},
) {
    val context = LocalContext.current

    var carMakeExpanded by remember { mutableStateOf(false) }
    var carModelExpanded by remember { mutableStateOf(false) }
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    var carVariantExpanded by remember { mutableStateOf(false) }
    var dealerLocationExpanded by remember { mutableStateOf(false) }
    var policyTypeExpanded by remember { mutableStateOf(false) }

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var hasPermissions by remember { mutableStateOf(false) }
    val tempImageUriRemember = remember { mutableStateOf<Uri?>(null) }

    var lastImageSource by remember { mutableStateOf<String?>(null) }

    val selectedImages = remember(state.images) {
        mutableListOf<Uri>().apply { addAll(state.images.mapNotNull { runCatching { Uri.parse(it) }.getOrNull() }) }
    }

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
                    e.printStackTrace()
                }
            } else if (lastImageSource == "GALLERY") {
                galleryLauncher.launch("image/*")
            }
            lastImageSource = null // Reset so next time intent is fresh
        }
    }

    val opts = state.formOptions ?: RaiseRequestFormOptions(
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList()
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
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

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(opts.partTypes) { partType ->
                PartTypeCard(
                    icon = partType.icon,
                    title = partType.title,
                    isSelected = state.selectedPartType == partType.id,
                    onClick = { onFieldChange("selectedPartType", partType.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Part photos",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90()
        )
        Text(
            text = "Upload photos of the part",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(selectedImages) { imageUri ->
                Box {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Photo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.camera),
                        error = painterResource(id = R.drawable.camera),
                        fallback = painterResource(id = R.drawable.camera)
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
                                    .apply { remove(imageUri.toString()) }
                                onFieldChange("images", newImages)
                            }
                    )
                }
            }
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
                                    e.printStackTrace()
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Other Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_90(),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Fill other details",
            fontSize = 12.sp,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DropdownField(
            label = "",
            value = state.selectedCarMake,
            onValueChange = { onFieldChange("selectedCarMake", it) },
            onDropdownExpand = { carMakeExpanded = true },
            expanded = carMakeExpanded,
            options = opts.carMakes,
            placeholder = "Select Car Make",
            onDismissRequest = { carMakeExpanded = false },
            labelComposable = { LabelWithAsterisk("Car Make*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "",
            value = state.selectedCarModel,
            onValueChange = { onFieldChange("selectedCarModel", it) },
            onDropdownExpand = { carModelExpanded = true },
            expanded = carModelExpanded,
            options = opts.carModels,
            placeholder = "Select Car Model",
            onDismissRequest = { carModelExpanded = false },
            labelComposable = { LabelWithAsterisk("Car Model*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "",
            value = state.selectedFuelType,
            onValueChange = { onFieldChange("selectedFuelType", it) },
            onDropdownExpand = { fuelTypeExpanded = true },
            expanded = fuelTypeExpanded,
            options = opts.fuelTypes,
            placeholder = "Select Fuel Type",
            onDismissRequest = { fuelTypeExpanded = false },
            labelComposable = { LabelWithAsterisk("Fuel Type*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "",
            value = state.selectedCarVariant,
            onValueChange = { onFieldChange("selectedCarVariant", it) },
            onDropdownExpand = { carVariantExpanded = true },
            expanded = carVariantExpanded,
            options = opts.carVariants,
            placeholder = "Select Car Variant",
            onDismissRequest = { carVariantExpanded = false },
            labelComposable = { LabelWithAsterisk("Car Variant*") }
        )

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

        TransparentInputField(
            label = "",
            value = state.dealerName,
            onValueChange = { onFieldChange("dealerName", it) },
            placeholder = "Enter Dealer Name",
            labelComposable = { LabelWithAsterisk("Dealer Name*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "",
            value = state.selectedDealerLocation,
            onValueChange = { onFieldChange("selectedDealerLocation", it) },
            onDropdownExpand = { dealerLocationExpanded = true },
            expanded = dealerLocationExpanded,
            options = opts.dealerLocations,
            placeholder = "Select Dealer Location",
            onDismissRequest = { dealerLocationExpanded = false },
            labelComposable = { LabelWithAsterisk("Dealer Location*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            onValueChange = { onFieldChange("advisorContactNumber", it) },
            placeholder = "Enter Advisor Contact Number",
            keyboardType = KeyboardType.Phone,
            labelComposable = { LabelWithAsterisk("Advisor Contact Number*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "",
            value = state.selectedPolicyType,
            onValueChange = { onFieldChange("selectedPolicyType", it) },
            onDropdownExpand = { policyTypeExpanded = true },
            expanded = policyTypeExpanded,
            options = opts.policyTypes,
            placeholder = "Select Policy Type",
            onDismissRequest = { policyTypeExpanded = false },
            labelComposable = { LabelWithAsterisk("Policy Type*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransparentInputField(
            label = "Claim Number",
            value = state.claimNumber,
            onValueChange = { onFieldChange("claimNumber", it) },
            placeholder = "Enter Claim Number"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TransparentInputField(
                modifier = Modifier.weight(1f),
                label = "Car Reg Number",
                value = state.carRegNumber,
                onValueChange = { onFieldChange("carRegNumber", it) },
                placeholder = "Registration Number"
            )

            TransparentInputField(
                modifier = Modifier.weight(1f),
                label = "Make Year",
                value = state.makeYear,
                onValueChange = { onFieldChange("makeYear", it) },
                placeholder = "Enter Make year",
                keyboardType = KeyboardType.Number
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        RegoButton(
            onClick = { onSubmit() },
            text = "Submit"
        )

        Spacer(modifier = Modifier.height(16.dp))
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

@Preview(showBackground = true)
@Composable
fun RaiseRequestScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        RaiseRequestScreen()
    }
}
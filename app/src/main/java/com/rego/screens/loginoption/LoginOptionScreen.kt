package com.rego.screens.loginoption

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.components.RegoButton
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme

@Composable
fun LoginOptionScreen(
    onLogin: () -> Unit,
    onSignUp: () -> Unit,
    onTermsClicked: () -> Unit = {},
    onPrivacyClicked: () -> Unit = {}
) {
    val loginViewModel: LoginOptionViewModel = koinViewModel()
    val errors = loginViewModel.errors
    val state = loginViewModel.state.collectAsState()
    val events = loginViewModel::onTriggerEvent

    LaunchedEffect(key1 = Unit) {
        events(LoginOptionEvent.Init)
    }

    DefaultScreenUI(
        progressBarState = state.value.progressBarState,
        addToolBarPadding = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.singin_up_bg),
                    contentScale = ContentScale.Crop
                )
        ) {
            // Dark overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Bottom section with content and buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rego_brandmark),
                    contentDescription = "Rego Logo",
                )
                // Welcome text
                Text(
                    text = "Join our exclusive Insurance\npartner network!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                )

                // Sign In Button
                RegoButton(onClick = onLogin, text = "Sign In")

                Spacer(modifier = Modifier.height(16.dp))

                // Join Us Button
                OutlinedButton(
                    onClick = onSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Join Us",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Terms and Privacy Policy text
                val context = LocalContext.current
                val agreementText = buildAnnotatedString {
                    append("by continuing, you agree to our app's ")
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "TERMS",
                            styles = TextLinkStyles(style = SpanStyle(fontWeight = FontWeight.Bold)),
                            linkInteractionListener = { _ ->
                                Toast.makeText(
                                    context,
                                    "Terms of Service clicked",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onTermsClicked()
                            }
                        )
                    ) {
                        append("Terms of Service")
                    }
                    append("\nand acknowledge that you've read our ")
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "PRIVACY",
                            styles = TextLinkStyles(style = SpanStyle(fontWeight = FontWeight.Bold)),
                            linkInteractionListener = { _ ->
                                Toast.makeText(
                                    context,
                                    "Privacy Policy clicked",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onPrivacyClicked()
                            }
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    append(".")
                }
                Text(
                    text = agreementText,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        LoginOptionScreen(onLogin = {}, onSignUp = {}, onTermsClicked = {}, onPrivacyClicked = {})
    }
}
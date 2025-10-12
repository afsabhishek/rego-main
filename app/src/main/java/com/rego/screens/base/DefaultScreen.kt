package com.rego.screens.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScreenUI(
    errors: Flow<UIComponent> = MutableSharedFlow(),
    progressBarState: ProgressBarState = ProgressBarState.Idle,
    networkState: NetworkState = NetworkState.Good,
    addToolBarPadding: Boolean = true,
    isBottomBarInScreen: Boolean = false,
    isRefreshingEnabled: Boolean = false,
    onRefresh: () -> Unit = {},
    isBackButtonEnabled: Boolean = false,
    onBackButtonClicked: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val refreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ Add error dialog state
    val showErrorDialog = remember { mutableStateOf(false) }
    val errorDialogTitle = remember { mutableStateOf("") }
    val errorDialogMessage = remember { mutableStateOf("") }

    val errorQueue = remember {
        mutableStateOf<Queue<UIComponent>>(Queue(mutableListOf()))
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        val bottomPadding = if (isBottomBarInScreen) 80.dp else it.calculateBottomPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    state = refreshState,
                    isRefreshing = progressBarState == ProgressBarState.Refreshing,
                    enabled = isRefreshingEnabled,
                    onRefresh = {
                        errorQueue.clear()
                        onRefresh()
                    }
                )
                .padding(bottom = bottomPadding)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.background(Color.Transparent),
            ) {
                if (networkState == NetworkState.Good) {
                    content(it)
                }
            }

            // ✅ Process error queue
            LaunchedEffect(errors) {
                errors.collect { uiComponent ->
                    when (uiComponent) {
                        is UIComponent.Dialog -> {
                            errorDialogTitle.value = uiComponent.title
                            errorDialogMessage.value = uiComponent.message
                            showErrorDialog.value = true
                        }
                        is UIComponent.Snackbar -> {
                            snackbarHostState.showSnackbar(
                                message = uiComponent.message,
                                actionLabel = uiComponent.buttonText,
                                duration = SnackbarDuration.Short
                            )
                        }
                        is UIComponent.Toast -> {
                            snackbarHostState.showSnackbar(
                                message = uiComponent.message,
                                duration = SnackbarDuration.Short
                            )
                        }
                        is UIComponent.ErrorData -> {
                            errorDialogTitle.value = uiComponent.title
                            errorDialogMessage.value = uiComponent.message
                            showErrorDialog.value = true
                        }
                        else -> {}
                    }
                }
            }

            // ✅ Show error dialog
            if (showErrorDialog.value) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog.value = false },
                    title = { Text(text = errorDialogTitle.value) },
                    text = { Text(text = errorDialogMessage.value) },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog.value = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            if (networkState == NetworkState.Failed && progressBarState == ProgressBarState.Idle) {
                // Network error screen
            }

            if (progressBarState is ProgressBarState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            PullToRefreshDefaults.Indicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = it.calculateTopPadding()),
                isRefreshing = progressBarState == ProgressBarState.Refreshing,
                state = refreshState,
            )
        }
    }
}

// Helper extensions
private fun MutableState<Queue<UIComponent>>.appendToMessageQueue(uiComponent: UIComponent) {
    if (uiComponent is UIComponent.None) {
        return
    }
    val queue = this.value
    queue.add(uiComponent)
    this.value = Queue(mutableListOf())
    this.value = queue
}

private fun MutableState<Queue<UIComponent>>.removeHeadMessage() {
    if (this.value.isEmpty()) {
        return
    }
    val queue = this.value
    queue.remove()
    this.value = Queue(mutableListOf())
    this.value = queue
}

private fun MutableState<Queue<UIComponent>>.clear() {
    if (this.value.isEmpty()) {
        return
    }
    val queue = this.value
    queue.clear()
    this.value = Queue(mutableListOf())
}

@Composable
fun <Effect : ViewSingleAction> EffectHandler(
    effectFlow: Flow<Effect>,
    onHandleEffect: (Effect) -> Unit
) {
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            onHandleEffect(effect)
        }
    }
}
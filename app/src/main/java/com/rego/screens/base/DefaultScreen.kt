package com.rego.screens.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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

/**
 * Default screen will handle your progressbar,
 * error state screens and click on the error state button
 */
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

    val errorQueue = remember {
        mutableStateOf<Queue<UIComponent>>(Queue(mutableListOf()))
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(Color.White)
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

            // process the queue
            if (!errorQueue.value.isEmpty()) {
                errorQueue.value.peek()?.let { uiComponent ->
                    if (uiComponent is UIComponent.Dialog) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            /*GenericDialog(
                                title = uiComponent.title,
                                description = uiComponent.message,
                                onRemoveHeadFromQueue = { errorQueue.removeHeadMessage() }
                            )*/
                        }
                    }
                    if (uiComponent is UIComponent.Snackbar) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            /*ShowSnackBar(
                                message = uiComponent.message,
                                snackBarVisibleState = true,
                                onDismiss = { errorQueue.removeHeadMessage() },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )*/
                        }
                    }
                    /*if (uiComponent is UIComponent.EmptyData) {
                        EmptyScreen(uiComponent.buttonText) {
                            errorQueue.removeHeadMessage()
                            onEmptyStateButtonClick()
                        }
                    }*/
                }
            }

            if (networkState == NetworkState.Failed && progressBarState == ProgressBarState.Idle) {
                /* Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     FailedNetworkScreen(onTryAgain = onTryAgain)
                 }*/
            }

            if (progressBarState is ProgressBarState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            LaunchedEffect(errors) {
                errors.collect { errors ->
                    errorQueue.appendToMessageQueue(errors)
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


private fun MutableState<Queue<UIComponent>>.appendToMessageQueue(uiComponent: UIComponent) {
    if (uiComponent is UIComponent.None) {
        return
    }

    val queue = this.value
    queue.add(uiComponent)

    this.value = Queue(mutableListOf()) // force to recompose
    this.value = queue
}

private fun MutableState<Queue<UIComponent>>.removeHeadMessage() {
    if (this.value.isEmpty()) {
        return
    }
    val queue = this.value
    queue.remove() // can throw exception if empty
    this.value = Queue(mutableListOf()) // force to recompose
    this.value = queue
}

private fun MutableState<Queue<UIComponent>>.clear() {
    if (this.value.isEmpty()) {
        return
    }
    val queue = this.value
    queue.clear()
    this.value = Queue(mutableListOf()) // force to recompose
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














package com.rego.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rego.screens.joinus.JoinUsParentScreen
import com.rego.screens.loginoption.LoginOptionScreen
import com.rego.screens.main.home.HomeScreen
import com.rego.screens.main.profile.ProfileAction
import com.rego.screens.main.profile.ProfileScreen
import com.rego.screens.main.profile.ProfileViewModel
import com.rego.screens.mobileverification.MobileVerificationScreen
import com.rego.screens.notifications.NotificationScreen
import com.rego.screens.orderdetails.OrderDetailsScreen
import com.rego.screens.orderdetails.OrderListScreen
import com.rego.screens.raiserequest.RaiseRequestParentScreen
import com.rego.screens.setpassword.SetPasswordParentScreen
import com.rego.screens.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.Splash.route) {
        composable(Destinations.Splash.route) {
            SplashScreen(
                gotoLoginOptionScreen = {
                    navController.navigate(Destinations.LoginOptions.route) {
                        popUpTo(Destinations.Splash.route) { inclusive = true }
                    }
                },
                gotoHomeScreen = {
                    // Navigate directly to home when session is valid
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.LoginOptions.route) {
            LoginOptionScreen(
                onLogin = { navController.navigate(Destinations.Login.route) },
                onSignUp = { navController.navigate(Destinations.Signup.route) }
            )
        }

        composable(Destinations.Login.route) {
            MobileVerificationScreen(
                onVerificationComplete = {
                    // Navigate to home after successful login
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.LoginOptions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.Signup.route) {
            JoinUsParentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = { userId, firebaseUid ->
                    // ✅ After signup success, go back to login screen
                    navController.navigate(Destinations.LoginOptions.route) {
                        popUpTo(Destinations.LoginOptions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Destinations.Home.routeWithArgs,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("firebaseUid") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val firebaseUid = backStackEntry.arguments?.getString("firebaseUid")

            HomeScreen(
                userId = userId,
                firebaseUid = firebaseUid,
                onProfileClick = {
                    if (navController.currentDestination?.route != Destinations.Profile.route) {
                        navController.navigate(Destinations.Profile.route)
                    }
                },
                onRaiseRequest = {
                    navController.navigate(Destinations.RaiseRequest.route)
                },
                onGridOptionClick = { },
                onOrderClick = {
                    navController.navigate(Destinations.OrderDetails.route)
                },
                onSearchClick = { },
                onOrderListClick = { orderType ->
                    // ✅ FIXED: Navigate to OrdersList with the card label/filter
                    navController.navigate(Destinations.OrdersList.createRoute(orderType))
                },
                onNotificationClick = {
                    navController.navigate(Destinations.Notification.route)
                }
            )
        }

        composable(Destinations.RaiseRequest.route) {
            RaiseRequestParentScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.OrdersList.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "default"
            OrderListScreen(
                orderType = type,
                onOrderClick = { orderId ->
                    navController.navigate(Destinations.OrderDetails.createRoute(orderId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.OrderDetails.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "-1"
            OrderDetailsScreen(
                orderId = orderId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.Notification.route) {
            NotificationScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ✅ FIXED: Removed duplicate Profile route - keeping only this one
        composable(Destinations.Profile.route) { backStackEntry ->
            val viewModel: ProfileViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()

            // Handle logout navigation
            LaunchedEffect(Unit) {
                viewModel.action.collect { action ->
                    when (action) {
                        is ProfileAction.NavigateToLogin -> {
                            navController.navigate(Destinations.LoginOptions.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        is ProfileAction.ShowError -> {
                            // Error is handled through snackbar/dialog
                        }
                    }
                }
            }

            ProfileScreen(
                onChangePasswordClick = {
                    navController.navigate(Destinations.ResetPassword.route)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.ResetPassword.route) {
            SetPasswordParentScreen(
                onLoginClick = {
                    navController.navigate(Destinations.LoginOptions.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
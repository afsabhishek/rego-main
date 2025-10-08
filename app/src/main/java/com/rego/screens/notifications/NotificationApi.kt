package com.rego.screens.notifications

interface NotificationApi {
    suspend fun getNotifications(): List<NotificationItem>
}

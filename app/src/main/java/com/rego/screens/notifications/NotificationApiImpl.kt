package com.rego.screens.notifications

class NotificationApiImpl : NotificationApi {
    override suspend fun getNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = "1",
            title = "New Order Request",
            message = "You have a new order request for Alloy Wheel repair of Hyundai Creta, 2023",
            timestamp = "2 hours ago",
            isRead = false,
            actionText = "View Order >>",
            notificationType = NotificationType.ORDER
        ),
        NotificationItem(
            id = "2",
            title = "Order Completed",
            message = "Your order for Headlamp replacement of Maruti Swift has been completed successfully",
            timestamp = "5 hours ago",
            isRead = true,
            actionText = "View Details",
            notificationType = NotificationType.SUCCESS
        ),
        NotificationItem(
            id = "3",
            title = "Payment Pending",
            message = "Payment for order #ORD12345 is still pending. Please complete the payment to proceed",
            timestamp = "1 day ago",
            isRead = false,
            actionText = "Pay Now",
            notificationType = NotificationType.WARNING
        ),
        NotificationItem(
            id = "4",
            title = "Order Dispatched",
            message = "Your order for Plastic repair parts has been dispatched and will reach you soon",
            timestamp = "2 days ago",
            isRead = true,
            actionText = "Track Order",
            notificationType = NotificationType.INFO
        ),
        NotificationItem(
            id = "5",
            title = "Order Cancelled",
            message = "Your order #ORD12343 for Toyota Camry bumper repair has been cancelled due to unavailability",
            timestamp = "3 days ago",
            isRead = false,
            actionText = "Reorder",
            notificationType = NotificationType.ERROR
        ),
        NotificationItem(
            id = "6",
            title = "New Parts Available",
            message = "New genuine parts for Honda Civic 2022 model are now available for order",
            timestamp = "1 week ago",
            isRead = true,
            actionText = "Browse Parts",
            notificationType = NotificationType.INFO
        ),
        NotificationItem(
            id = "7",
            title = "Service Reminder",
            message = "It's time for your vehicle's regular service checkup. Book your appointment now",
            timestamp = "1 week ago",
            isRead = true,
            actionText = "Book Service",
            notificationType = NotificationType.INFO
        ),
        NotificationItem(
            id = "8",
            title = "Quote Ready",
            message = "Your quote for Tata Nexon door panel repair is ready for review",
            timestamp = "2 weeks ago",
            isRead = true,
            actionText = "View Quote",
            notificationType = NotificationType.SUCCESS
        )
    )
}

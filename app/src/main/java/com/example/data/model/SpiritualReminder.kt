package com.example.data.model

data class SpiritualReminder(
    val reminderId: String,
    val title: String,
    val subtitle: String,
    val frequencyArabic: String, // "كل 30 يوم", "أسبوعياً"
    val daysLeft: Int,
    val isCompleted: Boolean = false
)

data class Trip(
    val tripId: String,
    val title: String,
    val destination: String,
    val dateDisplay: String,
    val priceDisplay: String,
    val capacity: Int,
    val availableSeats: Int,
    val coordinatorName: String,
    val coordinatorPhone: String,
    val coordinatorWhatsApp: String,
    val status: String = "متاح للحجز"
)

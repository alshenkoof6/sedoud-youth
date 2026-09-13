package com.example.data.model

enum class UserRole(val displayNameArabic: String) {
    YOUTH("شاب"),
    SERVANT("خادم"),
    ADMIN("أمين الخدمة / مسؤول"),
    SUPERVISOR("أب اعتراف / مشرف")
}

data class User(
    val userId: String, // e.g. "YT_000101"
    val firebaseUid: String,
    val fullName: String,
    val phone: String,
    val role: UserRole = UserRole.YOUTH,
    val isActive: Boolean = true
)

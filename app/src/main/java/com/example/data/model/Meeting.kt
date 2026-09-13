package com.example.data.model

enum class MeetingStatus(val arabicTitle: String) {
    SCHEDULED("مجدول قادم"),
    ACTIVE("جارٍ الآن (تسجيل الحضور مفعل)"),
    COMPLETED("منتهي"),
    CANCELLED("ملغي")
}

data class QRSecurityToken(
    val token: String,
    val activationTime: String,
    val expirationTime: String,
    val expiresAtEpochMs: Long
)

data class Meeting(
    val meetingId: String,
    val title: String,
    val description: String,
    val dateDisplay: String, // e.g. "الجمعة 18 سبتمبر 2026"
    val timeDisplay: String, // e.g. "07:30 مساءً"
    val location: String, // e.g. "قاعة القديس أثناسيوس - الكنيسة"
    val status: MeetingStatus = MeetingStatus.SCHEDULED,
    val pointsAwarded: Int = 10,
    val qrToken: QRSecurityToken? = null,
    val attendedCount: Int = 0
)

package com.example.data.model

enum class AttendanceMethod(val arabicName: String) {
    QR_SCAN("مسح رمز QR"),
    MANUAL_SERVANT("تسجيل استثنائي بواسطة الخادم")
}

data class AttendanceRecord(
    val attendanceId: String, // "${meetingId}_${userId}"
    val userId: String,
    val userName: String,
    val meetingId: String,
    val meetingTitle: String,
    val timestamp: String,
    val method: AttendanceMethod = AttendanceMethod.QR_SCAN,
    val pointsAwarded: Int = 10,
    val verified: Boolean = true
)

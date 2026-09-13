package com.example.data.model

enum class AgeGroup(val arabicName: String) {
    MIDDLE_SCHOOL("إعدادي"),
    SECONDARY("ثانوي"),
    UNIVERSITY("جامعة"),
    GRADUATES("خريجين")
}

enum class ScheduleDay(val arabicName: String) {
    SATURDAY("السبت"),
    SUNDAY("الأحد"),
    MONDAY("الاثنين"),
    TUESDAY("الثلاثاء"),
    WEDNESDAY("الأربعاء"),
    THURSDAY("الخميس"),
    FRIDAY("الجمعة")
}

enum class AvailabilityType(val arabicName: String) {
    AVAILABLE("متاح"),
    STUDY("دراسة"),
    WORK("عمل"),
    BUSY("مشغول")
}

data class ScheduleEntry(
    val day: ScheduleDay,
    val type: AvailabilityType,
    val note: String = ""
)

enum class FollowupStatus(val titleArabic: String, val descriptionArabic: String) {
    GREEN("حضور منتظم", "ملتزم بحضور الاجتماعات"),
    YELLOW("يحتاج تواصل", "غياب لقائين متتاليين - مستحب الافتقاد"),
    ORANGE("انقطاع ملحوظ", "غياب 3 لقاءات متتالية"),
    RED("أولوية افتقاد قصوى", "انقطاع ممتد (4 لقاءات أو أكثر)")
}

data class AttendanceStats(
    val totalAttended: Int = 0,
    val yearAttended: Int = 0,
    val monthAttended: Int = 0,
    val attendanceRate: Int = 0, // 0 to 100%
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastAttendDate: String = "",
    val consecutiveAbsences: Int = 0,
    val followupStatus: FollowupStatus = FollowupStatus.GREEN
)

data class YouthProfile(
    val userId: String,
    val fullName: String,
    val nickname: String,
    val phone: String,
    val whatsAppNumber: String,
    val gender: String = "ذكر",
    val ageGroup: AgeGroup = AgeGroup.UNIVERSITY,
    val university: String = "",
    val school: String = "",
    val faculty: String = "",
    val department: String = "",
    val academicYear: String = "",
    val job: String = "",
    val workplace: String = "",
    val weeklySchedule: List<ScheduleEntry> = emptyList(),
    val assignedServantId: String? = null,
    val assignedServantName: String? = null,
    val pointsBalance: Int = 0,
    val stats: AttendanceStats = AttendanceStats(),
    val createdAt: String = "2026-09-01"
)

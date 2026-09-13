package com.example.data.model

enum class ContactMethod(val arabicName: String) {
    PHONE("مكالمة هاتفية"),
    WHATSAPP("رسالة واتساب"),
    IN_PERSON("لقاء شخصي بالكنسية"),
    HOME_VISIT("زيارة منزلية"),
    OTHER("أخرى")
}

enum class FollowupReason(val arabicName: String) {
    CONTACTED("تم الاطمئنان والمحبة"),
    NO_ANSWER("لم يتم الرد / مشغول"),
    TRAVELING("سفر خارج البلاد أو المحافظة"),
    STUDY("فترة امتحانات أو دراسة"),
    WORK("مواعيد وتحديات العمل"),
    FAMILY("ظروف عائلية خاصة"),
    HEALTH("وعكة صحية / مرض"),
    OTHER("أسباب أخرى")
}

data class FollowupRecord(
    val followupId: String,
    val youthId: String,
    val youthName: String,
    val servantId: String,
    val servantName: String,
    val dateDisplay: String,
    val contactMethod: ContactMethod,
    val reasonStatus: FollowupReason,
    val generalNotes: String, // Delicate pastoral care note
    val nextFollowupDate: String? = null
)

package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class YouthMeetingRepository {

    // Initial Mock Servants
    val servants = listOf(
        User("SRV_01", "uid_srv_1", "الخادم مينا عادل", "01223456789", UserRole.SERVANT),
        User("SRV_02", "uid_srv_2", "الخادم كيرلس نبيل", "01098765432", UserRole.SERVANT),
        User("SRV_03", "uid_srv_3", "الخادمة مريم فؤاد", "01122334455", UserRole.SERVANT),
        User("ADM_01", "uid_adm_1", "د. بيشوي حنا (أمين الخدمة)", "01200001111", UserRole.ADMIN)
    )

    private val _currentUser = MutableStateFlow(
        User("YT_000101", "uid_yt_101", "جورج سامي رزق", "01234567890", UserRole.YOUTH)
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _youthProfiles = MutableStateFlow<List<YouthProfile>>(initialYouthList())
    val youthProfiles: StateFlow<List<YouthProfile>> = _youthProfiles.asStateFlow()

    private val _meetings = MutableStateFlow<List<Meeting>>(initialMeetingsList())
    val meetings: StateFlow<List<Meeting>> = _meetings.asStateFlow()

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(initialAttendanceList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    private val _followupRecords = MutableStateFlow<List<FollowupRecord>>(initialFollowups())
    val followupRecords: StateFlow<List<FollowupRecord>> = _followupRecords.asStateFlow()

    private val _spiritualReminders = MutableStateFlow<List<SpiritualReminder>>(
        listOf(
            SpiritualReminder("rem_1", "استعداد للاعتراف الدوري", "أب اعترافك بانتظارك بمحبة", "كل 30 يوم", 4, false),
            SpiritualReminder("rem_2", "مراجعة الأسبوع الروحية", "جلسة هدوء ومحاسبة النفس الإيجابية", "أسبوعياً", 2, false),
            SpiritualReminder("rem_3", "قراءة الأصحاح اليومي", "إنجيل القديس يوحنا", "يومياً", 1, true),
            SpiritualReminder("rem_4", "صلاة باكر والغروب", "رفع القلب لله قبل بداية اليوم", "يومياً", 1, false)
        )
    )
    val spiritualReminders: StateFlow<List<SpiritualReminder>> = _spiritualReminders.asStateFlow()

    private val _trips = MutableStateFlow<List<Trip>>(
        listOf(
            Trip(
                tripId = "TRIP_01",
                title = "رحلة دير الأنبا أنطونيوس والأنبا بولا بالبحر الأحمر",
                destination = "البحر الأحمر - الزعفرانة",
                dateDisplay = "الجمعة 25 سبتمبر 2026",
                priceDisplay = "250 ج.م",
                capacity = 50,
                availableSeats = 14,
                coordinatorName = "مينا عادل",
                coordinatorPhone = "01223456789",
                coordinatorWhatsApp = "201223456789"
            ),
            Trip(
                tripId = "TRIP_02",
                title = "يوم روحي ومعسكر خدمة في وادي النطرون",
                destination = "أديرة وادي النطرون",
                dateDisplay = "السبت 10 أكتوبر 2026",
                priceDisplay = "180 ج.م",
                capacity = 45,
                availableSeats = 6,
                coordinatorName = "كيرلس نبيل",
                coordinatorPhone = "01098765432",
                coordinatorWhatsApp = "201098765432"
            )
        )
    )
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    fun switchRole(newRole: UserRole) {
        val user = when (newRole) {
            UserRole.YOUTH -> User("YT_000101", "uid_yt_101", "جورج سامي رزق", "01234567890", UserRole.YOUTH)
            UserRole.SERVANT -> servants[0] // Mina Adel
            UserRole.ADMIN -> servants[3] // Bishoy Hanna
            UserRole.SUPERVISOR -> User("SUP_01", "uid_sup_1", "أبونا أنطونيوس (مشرف الاجتماع)", "01233445566", UserRole.SUPERVISOR)
        }
        _currentUser.value = user
    }

    fun getActiveMeeting(): Meeting? {
        return _meetings.value.firstOrNull { it.status == MeetingStatus.ACTIVE }
            ?: _meetings.value.firstOrNull { it.status == MeetingStatus.SCHEDULED }
    }

    fun activateMeetingQR(meetingId: String): Meeting? {
        val currentList = _meetings.value.toMutableList()
        val index = currentList.indexOfFirst { it.meetingId == meetingId }
        if (index == -1) return null

        val tokenString = "QR_AUTH_${meetingId}_${System.currentTimeMillis().toString().takeLast(6)}"
        val qrToken = QRSecurityToken(
            token = tokenString,
            activationTime = "7:15 م",
            expirationTime = "8:30 م",
            expiresAtEpochMs = System.currentTimeMillis() + (75 * 60 * 1000)
        )

        val updated = currentList[index].copy(
            status = MeetingStatus.ACTIVE,
            qrToken = qrToken
        )
        currentList[index] = updated
        _meetings.value = currentList
        return updated
    }

    fun createMeeting(title: String, description: String, dateDisplay: String, timeDisplay: String, location: String): Meeting {
        val newId = "MTG_${System.currentTimeMillis().toString().takeLast(4)}"
        val tokenString = "QR_AUTH_${newId}_${UUID.randomUUID().toString().take(6)}"
        val meeting = Meeting(
            meetingId = newId,
            title = title,
            description = description,
            dateDisplay = dateDisplay,
            timeDisplay = timeDisplay,
            location = location,
            status = MeetingStatus.ACTIVE,
            pointsAwarded = 10,
            qrToken = QRSecurityToken(
                token = tokenString,
                activationTime = timeDisplay,
                expirationTime = "بعد ساعة من البدء",
                expiresAtEpochMs = System.currentTimeMillis() + (60 * 60 * 1000)
            ),
            attendedCount = 0
        )
        _meetings.value = listOf(meeting) + _meetings.value
        return meeting
    }

    fun recordAttendance(youthId: String, qrScannedToken: String): Result<AttendanceRecord> {
        val activeMeeting = _meetings.value.firstOrNull { it.status == MeetingStatus.ACTIVE }
            ?: return Result.failure(Exception("لا يوجد اجتماع نشط حالياً لاستقبال الحضور."))

        val validToken = activeMeeting.qrToken?.token
        if (validToken == null || qrScannedToken.trim() != validToken.trim()) {
            return Result.failure(Exception("رمز الحضور غير صحيح أو انتهت صلاحيته الزمانية."))
        }

        val existing = _attendanceRecords.value.firstOrNull {
            it.meetingId == activeMeeting.meetingId && it.userId == youthId
        }
        if (existing != null) {
            return Result.failure(Exception("لقد تم تسجيل حضورك في هذا الاجتماع مسبقاً ✓"))
        }

        val youth = _youthProfiles.value.firstOrNull { it.userId == youthId }
            ?: return Result.failure(Exception("لم يتم العثور على ملف الشاب."))

        val record = AttendanceRecord(
            attendanceId = "${activeMeeting.meetingId}_${youthId}",
            userId = youthId,
            userName = youth.fullName,
            meetingId = activeMeeting.meetingId,
            meetingTitle = activeMeeting.title,
            timestamp = "الآن - 7:35 م",
            method = AttendanceMethod.QR_SCAN,
            pointsAwarded = activeMeeting.pointsAwarded,
            verified = true
        )

        // Update attendance records
        _attendanceRecords.value = listOf(record) + _attendanceRecords.value

        // Update Meeting attendance counter
        val updatedMeetings = _meetings.value.map {
            if (it.meetingId == activeMeeting.meetingId) it.copy(attendedCount = it.attendedCount + 1) else it
        }
        _meetings.value = updatedMeetings

        // Recalculate youth streak & attendance stats!
        val newCurrentStreak = youth.stats.currentStreak + 1
        val newBestStreak = maxOf(youth.stats.bestStreak, newCurrentStreak)
        val newTotalAttended = youth.stats.totalAttended + 1
        val newYearAttended = youth.stats.yearAttended + 1
        val newPoints = youth.pointsBalance + activeMeeting.pointsAwarded

        val updatedStats = youth.stats.copy(
            totalAttended = newTotalAttended,
            yearAttended = newYearAttended,
            currentStreak = newCurrentStreak,
            bestStreak = newBestStreak,
            lastAttendDate = "اليوم (${activeMeeting.dateDisplay})",
            consecutiveAbsences = 0,
            followupStatus = FollowupStatus.GREEN
        )

        _youthProfiles.value = _youthProfiles.value.map {
            if (it.userId == youthId) it.copy(pointsBalance = newPoints, stats = updatedStats) else it
        }

        return Result.success(record)
    }

    fun addFollowup(
        youthId: String,
        servantId: String,
        servantName: String,
        method: ContactMethod,
        reason: FollowupReason,
        notes: String,
        nextDate: String?
    ): FollowupRecord {
        val youth = _youthProfiles.value.first { it.userId == youthId }
        val record = FollowupRecord(
            followupId = "FOL_${System.currentTimeMillis().toString().takeLast(6)}",
            youthId = youthId,
            youthName = youth.fullName,
            servantId = servantId,
            servantName = servantName,
            dateDisplay = "اليوم",
            contactMethod = method,
            reasonStatus = reason,
            generalNotes = notes,
            nextFollowupDate = nextDate
        )

        _followupRecords.value = listOf(record) + _followupRecords.value
        return record
    }

    fun assignServantToYouth(youthId: String, servantId: String, servantName: String) {
        _youthProfiles.value = _youthProfiles.value.map {
            if (it.userId == youthId) {
                it.copy(assignedServantId = servantId, assignedServantName = servantName)
            } else it
        }
    }

    fun toggleReminder(reminderId: String) {
        _spiritualReminders.value = _spiritualReminders.value.map {
            if (it.reminderId == reminderId) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    private fun initialMeetingsList(): List<Meeting> {
        val nowToken = "QR_AUTH_MTG_2026_0918"
        return listOf(
            Meeting(
                meetingId = "MTG_2026_0918",
                title = "اجتماع الشباب الأسبوعي - قوة القيامة في حياتنا",
                description = "دراسة كتابية وتأمل روحي مع فقرة تسبيح وفقرة حوارية مفتوحة.",
                dateDisplay = "الجمعة 18 سبتمبر 2026",
                timeDisplay = "07:30 م",
                location = "قاعة القديس أثناسيوس - الكنيسة الكبرى",
                status = MeetingStatus.ACTIVE,
                pointsAwarded = 10,
                qrToken = QRSecurityToken(
                    token = nowToken,
                    activationTime = "07:15 م",
                    expirationTime = "08:30 م",
                    expiresAtEpochMs = System.currentTimeMillis() + (60 * 60 * 1000)
                ),
                attendedCount = 28
            ),
            Meeting(
                meetingId = "MTG_2026_0911",
                title = "لقاء روحي: الثبات في عالم متغير",
                description = "كلمة لأبونا المشرف وورش عمل شبابية.",
                dateDisplay = "الجمعة 11 سبتمبر 2026",
                timeDisplay = "07:30 م",
                location = "قاعة القديس كيرلس",
                status = MeetingStatus.COMPLETED,
                pointsAwarded = 10,
                attendedCount = 42
            ),
            Meeting(
                meetingId = "MTG_2026_0904",
                title = "مؤتمر بداية العام الدراسي والجامعي",
                description = "كيف ننظم أوقاتنا ونشهد للمسيح في دراستنا وعملنا.",
                dateDisplay = "الجمعة 4 سبتمبر 2026",
                timeDisplay = "06:00 م",
                location = "المسرح الكبير بالكنسية",
                status = MeetingStatus.COMPLETED,
                pointsAwarded = 15,
                attendedCount = 65
            )
        )
    }

    private fun initialAttendanceList(): List<AttendanceRecord> {
        return listOf(
            AttendanceRecord("REC_01", "YT_000101", "جورج سامي رزق", "MTG_2026_0911", "لقاء روحي: الثبات في عالم متغير", "الجمعة 11 سبتمبر - 7:35 م", AttendanceMethod.QR_SCAN, 10, true),
            AttendanceRecord("REC_02", "YT_000101", "جورج سامي رزق", "MTG_2026_0904", "مؤتمر بداية العام الدراسي", "الجمعة 4 سبتمبر - 6:20 م", AttendanceMethod.QR_SCAN, 15, true),
            AttendanceRecord("REC_03", "YT_000102", "مينا سامي عزيز", "MTG_2026_0911", "لقاء روحي: الثبات في عالم متغير", "الجمعة 11 سبتمبر - 7:40 م", AttendanceMethod.QR_SCAN, 10, true)
        )
    }

    private fun initialFollowups(): List<FollowupRecord> {
        return listOf(
            FollowupRecord(
                followupId = "FOL_01",
                youthId = "YT_000104",
                youthName = "كيرلس مجدي نبيل",
                servantId = "SRV_01",
                servantName = "الخادم مينا عادل",
                dateDisplay = "أمس 7:00 م",
                contactMethod = ContactMethod.PHONE,
                reasonStatus = FollowupReason.STUDY,
                generalNotes = "اطمأننا عليه، لديه امتحانات عملية هذا الأسبوع وسيعود للاجتماع القادم بمحبة.",
                nextFollowupDate = "الجمعة القادمة"
            ),
            FollowupRecord(
                followupId = "FOL_02",
                youthId = "YT_000105",
                youthName = "بيتر فادي إبراهيم",
                servantId = "SRV_02",
                servantName = "الخادم كيرلس نبيل",
                dateDisplay = "منذ 3 أيام",
                contactMethod = ContactMethod.WHATSAPP,
                reasonStatus = FollowupReason.WORK,
                generalNotes = "تم إرسال رسالة محبة، دوام عمله تغير لمساء الجمعة مؤقتاً وسنحاول ترتيب اللقاء معه.",
                nextFollowupDate = "الأربعاء"
            )
        )
    }

    private fun initialYouthList(): List<YouthProfile> {
        val scheduleGeorge = listOf(
            ScheduleEntry(ScheduleDay.SATURDAY, AvailabilityType.AVAILABLE, "متاح مساءً"),
            ScheduleEntry(ScheduleDay.SUNDAY, AvailabilityType.AVAILABLE, "قداس صباحي وتفرغ بعد الظهر"),
            ScheduleEntry(ScheduleDay.MONDAY, AvailabilityType.STUDY, "محاضرات حتى 4 م"),
            ScheduleEntry(ScheduleDay.TUESDAY, AvailabilityType.STUDY, "محاضرات ومعامل"),
            ScheduleEntry(ScheduleDay.WEDNESDAY, AvailabilityType.AVAILABLE, "متاح بعد 2 م"),
            ScheduleEntry(ScheduleDay.THURSDAY, AvailabilityType.STUDY, "مذاكرة ومكتبة"),
            ScheduleEntry(ScheduleDay.FRIDAY, AvailabilityType.AVAILABLE, "يوم الاجتماع والخدمة")
        )

        return listOf(
            YouthProfile(
                userId = "YT_000101",
                fullName = "جورج سامي رزق",
                nickname = "جو",
                phone = "01234567890",
                whatsAppNumber = "201234567890",
                ageGroup = AgeGroup.UNIVERSITY,
                university = "جامعة عين شمس",
                faculty = "هندسة",
                department = "حاسبات ونظم",
                academicYear = "الفرقة الثالثة",
                weeklySchedule = scheduleGeorge,
                assignedServantId = "SRV_01",
                assignedServantName = "الخادم مينا عادل",
                pointsBalance = 160,
                stats = AttendanceStats(
                    totalAttended = 26,
                    yearAttended = 20,
                    monthAttended = 3,
                    attendanceRate = 88,
                    currentStreak = 4,
                    bestStreak = 9,
                    lastAttendDate = "11 سبتمبر 2026",
                    consecutiveAbsences = 0,
                    followupStatus = FollowupStatus.GREEN
                )
            ),
            YouthProfile(
                userId = "YT_000102",
                fullName = "مينا سامي عزيز",
                nickname = "مينوش",
                phone = "01011122233",
                whatsAppNumber = "201011122233",
                ageGroup = AgeGroup.UNIVERSITY,
                university = "جامعة القاهرة",
                faculty = "تجارة",
                department = "إنجليزي",
                academicYear = "الفرقة الثانية",
                weeklySchedule = scheduleGeorge,
                assignedServantId = "SRV_01",
                assignedServantName = "الخادم مينا عادل",
                pointsBalance = 90,
                stats = AttendanceStats(
                    totalAttended = 18,
                    yearAttended = 14,
                    monthAttended = 2,
                    attendanceRate = 72,
                    currentStreak = 2,
                    bestStreak = 6,
                    lastAttendDate = "11 سبتمبر 2026",
                    consecutiveAbsences = 0,
                    followupStatus = FollowupStatus.GREEN
                )
            ),
            YouthProfile(
                userId = "YT_000103",
                fullName = "مارينا عماد زكي",
                nickname = "رينو",
                phone = "01144556677",
                whatsAppNumber = "201144556677",
                gender = "أنثى",
                ageGroup = AgeGroup.UNIVERSITY,
                university = "جامعة حلوان",
                faculty = "فنون تطبيقية",
                department = "تصميم داخلي",
                academicYear = "الفرقة الرابعة",
                assignedServantId = "SRV_03",
                assignedServantName = "الخادمة مريم فؤاد",
                pointsBalance = 210,
                stats = AttendanceStats(
                    totalAttended = 31,
                    yearAttended = 24,
                    monthAttended = 4,
                    attendanceRate = 94,
                    currentStreak = 8,
                    bestStreak = 12,
                    lastAttendDate = "11 سبتمبر 2026",
                    consecutiveAbsences = 0,
                    followupStatus = FollowupStatus.GREEN
                )
            ),
            YouthProfile(
                userId = "YT_000104",
                fullName = "كيرلس مجدي نبيل",
                nickname = "كيرو",
                phone = "01288990011",
                whatsAppNumber = "201288990011",
                ageGroup = AgeGroup.SECONDARY,
                school = "مدرسة سان جوزيف الثانوية",
                academicYear = "الصف الثاني الثانوي",
                assignedServantId = "SRV_01",
                assignedServantName = "الخادم مينا عادل",
                pointsBalance = 70,
                stats = AttendanceStats(
                    totalAttended = 12,
                    yearAttended = 9,
                    monthAttended = 1,
                    attendanceRate = 60,
                    currentStreak = 0,
                    bestStreak = 5,
                    lastAttendDate = "28 أغسطس 2026",
                    consecutiveAbsences = 2,
                    followupStatus = FollowupStatus.YELLOW // Yellow: Needs contact
                )
            ),
            YouthProfile(
                userId = "YT_000105",
                fullName = "بيتر فادي إبراهيم",
                nickname = "بيبو",
                phone = "01555667788",
                whatsAppNumber = "201555667788",
                ageGroup = AgeGroup.GRADUATES,
                job = "مهندس برمجيات",
                workplace = "القرية الذكية",
                assignedServantId = "SRV_02",
                assignedServantName = "الخادم كيرلس نبيل",
                pointsBalance = 40,
                stats = AttendanceStats(
                    totalAttended = 10,
                    yearAttended = 8,
                    monthAttended = 0,
                    attendanceRate = 45,
                    currentStreak = 0,
                    bestStreak = 4,
                    lastAttendDate = "21 أغسطس 2026",
                    consecutiveAbsences = 3,
                    followupStatus = FollowupStatus.ORANGE // Orange: 3 absences
                )
            ),
            YouthProfile(
                userId = "YT_000106",
                fullName = "ديفيد نشأت كامل",
                nickname = "ديف",
                phone = "01009988776",
                whatsAppNumber = "201009988776",
                ageGroup = AgeGroup.GRADUATES,
                job = "محاسب مالي",
                workplace = "بنك القاهرة",
                assignedServantId = "SRV_02",
                assignedServantName = "الخادم كيرلس نبيل",
                pointsBalance = 20,
                stats = AttendanceStats(
                    totalAttended = 6,
                    yearAttended = 4,
                    monthAttended = 0,
                    attendanceRate = 28,
                    currentStreak = 0,
                    bestStreak = 3,
                    lastAttendDate = "7 أغسطس 2026",
                    consecutiveAbsences = 5,
                    followupStatus = FollowupStatus.RED // Red: Extended absence
                )
            ),
            YouthProfile(
                userId = "YT_000107",
                fullName = "سارة يوسف بشارة",
                nickname = "سوسو",
                phone = "01244332211",
                whatsAppNumber = "201244332211",
                gender = "أنثى",
                ageGroup = AgeGroup.UNIVERSITY,
                university = "جامعة القاهرة",
                faculty = "طب بشري",
                department = "باطنة",
                academicYear = "الفرقة الخامسة",
                assignedServantId = "SRV_03",
                assignedServantName = "الخادمة مريم فؤاد",
                pointsBalance = 130,
                stats = AttendanceStats(
                    totalAttended = 22,
                    yearAttended = 18,
                    monthAttended = 2,
                    attendanceRate = 80,
                    currentStreak = 3,
                    bestStreak = 7,
                    lastAttendDate = "11 سبتمبر 2026",
                    consecutiveAbsences = 0,
                    followupStatus = FollowupStatus.GREEN
                )
            )
        )
    }
}

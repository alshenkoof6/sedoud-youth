package com.example.ui.screens.youth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MeetingStatus
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.theme.*

@Composable
fun YouthHomeScreen(
    repository: YouthMeetingRepository,
    onNavigateToScan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val youthList by repository.youthProfiles.collectAsState()
    val meetings by repository.meetings.collectAsState()
    val reminders by repository.spiritualReminders.collectAsState()
    val trips by repository.trips.collectAsState()

    val youth = youthList.firstOrNull { it.userId == currentUser.userId } ?: youthList.first()
    val activeMeeting = meetings.firstOrNull { it.status == MeetingStatus.ACTIVE } ?: meetings.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Greeting & Profile Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سلام ونعمة، أهلاً يا ${youth.nickname.ifEmpty { youth.fullName.split(" ").first() }} ❤️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )
                    Text(
                        text = "${youth.ageGroup.arabicName} • ${youth.university.ifEmpty { youth.school }}",
                        fontSize = 13.sp,
                        color = TextSecondaryLight
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ChurchPrimary.copy(alpha = 0.1f))
                        .clickable(onClick = onNavigateToProfile)
                        .testTag("profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "الملف الشخصي",
                        tint = ChurchPrimary
                    )
                }
            }
        }

        // 2. Next Meeting Live Card
        if (activeMeeting != null) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ChurchNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (activeMeeting.status == MeetingStatus.ACTIVE) StatusGreen else ChurchSecondary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (activeMeeting.status == MeetingStatus.ACTIVE) "• جارٍ الآن - تسجيل الحضور مفتوح" else "الاجتماع القادم",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "نقاط",
                                    tint = ChurchSecondaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+${activeMeeting.pointsAwarded} نقطة",
                                    color = ChurchSecondaryLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = activeMeeting.title,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${activeMeeting.dateDisplay} (${activeMeeting.timeDisplay})",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeMeeting.location,
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        }

                        // Quick Scan Button
                        Button(
                            onClick = onNavigateToScan,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChurchSecondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("quick_scan_attendance_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تسجيل الحضور بمسح الـ QR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 3. Attendance Streak & Stats Highlight
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "سلسلة الحضور",
                                    tint = ChurchSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "سلسلة الحضور المتتالية",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "أفضل سلسلة: ${youth.stats.bestStreak} لقاءات متصلة",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Text(
                            text = "${youth.stats.currentStreak} لقاءات 🔥",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchSecondary
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // 3 KPI columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${youth.stats.totalAttended}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChurchPrimary
                            )
                            Text(
                                text = "إجمالي الحضور",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${youth.stats.yearAttended}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChurchNavy
                            )
                            Text(
                                text = "هذا العام",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${youth.stats.attendanceRate}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                            Text(
                                text = "نسبة الالتزام",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${youth.pointsBalance}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChurchSecondary
                            )
                            Text(
                                text = "رصيد النقاط",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }
            }
        }

        // 4. Spiritual Reminders
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التنبيهات الروحية الشخصية",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )
                    Text(
                        text = "خصوصية تامة ✝️",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }

                reminders.forEach { reminder ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { repository.toggleReminder(reminder.reminderId) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = reminder.isCompleted,
                                    onCheckedChange = { repository.toggleReminder(reminder.reminderId) },
                                    colors = CheckboxDefaults.colors(checkedColor = StatusGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = reminder.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (reminder.isCompleted) Color.Gray else ChurchNavy
                                    )
                                    Text(
                                        text = "${reminder.subtitle} • (${reminder.frequencyArabic})",
                                        fontSize = 12.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (reminder.daysLeft <= 2) Color(0xFFFEF2F2) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = "باقي ${reminder.daysLeft} أيام",
                                    fontSize = 11.sp,
                                    color = if (reminder.daysLeft <= 2) StatusRed else TextSecondaryLight,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Upcoming Trips Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "الرحلات والأنشطة القادمة",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )

                trips.take(2).forEach { trip ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trip.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDBEAFE)
                                ) {
                                    Text(
                                        text = trip.priceDisplay,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ChurchPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الميعاد: ${trip.dateDisplay}",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = "المقاعد المتبقية: ${trip.availableSeats} من ${trip.capacity}",
                                    fontSize = 12.sp,
                                    color = if (trip.availableSeats <= 10) StatusOrange else StatusGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

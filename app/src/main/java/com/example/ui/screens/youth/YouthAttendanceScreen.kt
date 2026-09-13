package com.example.ui.screens.youth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MeetingStatus
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.theme.*

@Composable
fun YouthAttendanceScreen(
    repository: YouthMeetingRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val youthList by repository.youthProfiles.collectAsState()
    val meetings by repository.meetings.collectAsState()
    val attendanceRecords by repository.attendanceRecords.collectAsState()

    val youth = youthList.firstOrNull { it.userId == currentUser.userId } ?: youthList.first()
    val activeMeeting = meetings.firstOrNull { it.status == MeetingStatus.ACTIVE }

    var scanResultSuccess by remember { mutableStateOf<String?>(null) }
    var scanResultError by remember { mutableStateOf<String?>(null) }
    var isSimulatingScan by remember { mutableStateOf(false) }

    val myRecords = attendanceRecords.filter { it.userId == youth.userId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Title
        item {
            Column {
                Text(
                    text = "تسجيل وسجل الحضور",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )
                Text(
                    text = "امسح رمز الـ QR داخل قاعة الاجتماع لتثبيت حضورك وحصد النقاط",
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // 2. Active Meeting Attendance Scanner Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (activeMeeting != null) Color.White else Color(0xFFF1F5F9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (activeMeeting != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusGreenBg
                        ) {
                            Text(
                                text = "• الاجتماع نشط الآن - نافذة تسجيل الحضور مفتوحة",
                                color = StatusGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = activeMeeting.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )

                        Text(
                            text = "المكان: ${activeMeeting.location}",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )

                        // Visual simulated Camera viewfinder / scan target
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ChurchNavy)
                                .border(2.dp, ChurchSecondary, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scanner",
                                tint = ChurchSecondaryLight,
                                modifier = Modifier.size(72.dp)
                            )
                        }

                        // Success / Error Alerts
                        AnimatedVisibility(visible = scanResultSuccess != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StatusGreenBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusGreen
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = scanResultSuccess ?: "",
                                        fontSize = 13.sp,
                                        color = StatusGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = scanResultError != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StatusRedBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = StatusRed
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = scanResultError ?: "",
                                        fontSize = 13.sp,
                                        color = StatusRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Scan Action Button
                        Button(
                            onClick = {
                                val token = activeMeeting.qrToken?.token ?: ""
                                val result = repository.recordAttendance(youth.userId, token)
                                if (result.isSuccess) {
                                    scanResultSuccess = "تم تسجيل الحضور بنجاح ✓ (+10 نقاط)"
                                    scanResultError = null
                                } else {
                                    scanResultError = result.exceptionOrNull()?.message ?: "خطأ في التسجيل"
                                    scanResultSuccess = null
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChurchPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("scan_qr_action_button")
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "محاكاة مسح الكود وتسجيل الحضور",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = TextMutedLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "لا يوجد اجتماع نشط حالياً لتسجيل الحضور",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )
                        Text(
                            text = "يتم فتح التسجيل تلقائياً مع بداية موعد الاجتماع وتوليد الخدام لكود الـ QR.",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }
        }

        // 3. Stats Strip
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${youth.stats.currentStreak}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchSecondary
                        )
                        Text(
                            text = "السلسلة الحالية",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${youth.stats.totalAttended}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )
                        Text(
                            text = "إجمالي الحضور",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${youth.stats.attendanceRate}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                        Text(
                            text = "نسبة الالتزام",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }
        }

        // 4. Past Attendance Records List
        item {
            Text(
                text = "سجل اللقاءات التي حضرتها (${myRecords.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ChurchNavy
            )
        }

        if (myRecords.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لم تسجل حضورك في أي اجتماع بعد. نتطلع لرؤيتك قريباً! ❤️",
                            fontSize = 13.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }
        } else {
            items(myRecords) { record ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(StatusGreenBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = StatusGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = record.meetingTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "${record.timestamp} • ${record.method.arabicName}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "+${record.pointsAwarded} نقطة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

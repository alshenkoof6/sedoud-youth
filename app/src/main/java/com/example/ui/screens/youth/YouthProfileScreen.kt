package com.example.ui.screens.youth

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvailabilityType
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.theme.*

@Composable
fun YouthProfileScreen(
    repository: YouthMeetingRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val youthList by repository.youthProfiles.collectAsState()
    val youth = youthList.firstOrNull { it.userId == currentUser.userId } ?: youthList.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ChurchPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = youth.fullName.take(2),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = youth.fullName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDBEAFE)
                    ) {
                        Text(
                            text = "كود العضوية: ${youth.userId}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ChurchPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. Pastoral Care Servant Contact
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(ChurchPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "الخادم المسؤول عن متابعتك",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                            Text(
                                text = youth.assignedServantName ?: "جاري التعيين بواسطة أمين الخدمة",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChurchNavy
                            )
                        }
                    }

                    IconButton(onClick = { /* WhatsApp intent simulation */ }) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "محادثة",
                            tint = StatusGreen
                        )
                    }
                }
            }
        }

        // 3. Academic & Employment Info
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "البيانات الدراسية والمهنية",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )

                    ProfileInfoRow(label = "المرحلة العمرية", value = youth.ageGroup.arabicName)
                    if (youth.university.isNotEmpty()) {
                        ProfileInfoRow(label = "الجامعة", value = youth.university)
                        ProfileInfoRow(label = "الكلية والقسم", value = "${youth.faculty} - ${youth.department}")
                        ProfileInfoRow(label = "السنة الدراسية", value = youth.academicYear)
                    }
                    if (youth.school.isNotEmpty()) {
                        ProfileInfoRow(label = "المدرسة", value = youth.school)
                    }
                    if (youth.job.isNotEmpty()) {
                        ProfileInfoRow(label = "الوظيفة ومكان العمل", value = "${youth.job} (${youth.workplace})")
                    }
                    ProfileInfoRow(label = "رقم الهاتف والواتساب", value = youth.phone)
                }
            }
        }

        // 4. Weekly Availability Schedule
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "جدول التفرغ الأسبوعي",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )
                        Text(
                            text = "يساعد الخدام في اختيار وقت الافتقاد",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    youth.weeklySchedule.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.day.arabicName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = ChurchNavy
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (entry.note.isNotEmpty()) {
                                    Text(
                                        text = entry.note,
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (entry.type) {
                                        AvailabilityType.AVAILABLE -> StatusGreenBg
                                        AvailabilityType.STUDY -> Color(0xFFDBEAFE)
                                        AvailabilityType.WORK -> Color(0xFFFEF3C7)
                                        AvailabilityType.BUSY -> StatusOrangeBg
                                    }
                                ) {
                                    Text(
                                        text = entry.type.arabicName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (entry.type) {
                                            AvailabilityType.AVAILABLE -> StatusGreen
                                            AvailabilityType.STUDY -> ChurchPrimary
                                            AvailabilityType.WORK -> Color(0xFF92400E)
                                            AvailabilityType.BUSY -> StatusOrange
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondaryLight)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ChurchNavy)
    }
}

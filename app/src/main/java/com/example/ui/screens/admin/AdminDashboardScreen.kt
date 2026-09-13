package com.example.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FollowupStatus
import com.example.data.model.MeetingStatus
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.components.FollowupStatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    repository: YouthMeetingRepository,
    onNavigateToMeetings: () -> Unit,
    onNavigateToFollowup: () -> Unit,
    onNavigateToYouth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val youthList by repository.youthProfiles.collectAsState()
    val meetings by repository.meetings.collectAsState()

    val activeMeeting = meetings.firstOrNull { it.status == MeetingStatus.ACTIVE }

    val urgentFollowup = youthList.filter {
        it.stats.followupStatus == FollowupStatus.RED || it.stats.followupStatus == FollowupStatus.ORANGE
    }
    val yellowFollowup = youthList.filter { it.stats.followupStatus == FollowupStatus.YELLOW }
    val regularYouth = youthList.filter { it.stats.followupStatus == FollowupStatus.GREEN }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dashboard Header
        item {
            Column {
                Text(
                    text = "لوحة المتابعة والخدمة الرعوية",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )
                Text(
                    text = "${currentUser.fullName} (${currentUser.role.displayNameArabic})",
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // 2. High-level KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "إجمالي الشباب",
                    value = "${youthList.size}",
                    subtitle = "${regularYouth.size} منتظم",
                    icon = Icons.Default.Groups,
                    color = ChurchPrimary,
                    modifier = Modifier.weight(1f)
                )

                KpiCard(
                    title = "حضور اليوم",
                    value = "${activeMeeting?.attendedCount ?: 0}",
                    subtitle = activeMeeting?.status?.arabicTitle ?: "لا اجتماع الآن",
                    icon = Icons.Default.HowToReg,
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "أولوية افتقاد",
                    value = "${urgentFollowup.size + yellowFollowup.size}",
                    subtitle = "${urgentFollowup.size} حالات حرجة",
                    icon = Icons.Default.Favorite,
                    color = StatusRed,
                    modifier = Modifier.weight(1f)
                )

                KpiCard(
                    title = "نقاط موزعة",
                    value = "${youthList.sumOf { it.pointsBalance }}",
                    subtitle = "تحفيز والتزام",
                    icon = Icons.Default.Stars,
                    color = ChurchSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Quick Operations
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "إجراءات الخدمة السريعة",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )

                // Manage Meetings & QR
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ChurchNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToMeetings)
                        .testTag("admin_qr_meeting_action")
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
                                    .background(ChurchSecondary, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "إدارة الاجتماع وتوليد كود الـ QR",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (activeMeeting != null) "الاجتماع نشط: ${activeMeeting.title}" else "بدء تسجيل حضور اجتماع جديد",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                // Pastoral Follow-up Hub
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToFollowup)
                        .testTag("admin_followup_hub_action")
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
                                    .background(StatusRedBg, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolunteerActivism,
                                    contentDescription = null,
                                    tint = StatusRed
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "غرفة الافتقاد الرعوي",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "متابعة الغياب وتدوين التواصل الرعوي",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = TextSecondaryLight
                        )
                    }
                }

                // Youth Directory & Assignments
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToYouth)
                        .testTag("admin_youth_directory_action")
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
                                    .background(Color(0xFFDBEAFE), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonSearch,
                                    contentDescription = null,
                                    tint = ChurchPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "دليل الشباب وتوزيع الخدام",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "بحث، فلترة، وتعيين المخدومين",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = TextSecondaryLight
                        )
                    }
                }
            }
        }

        // 4. Urgent Pastoral Attention Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "يوصى بالاطمئنان عليهم هذا الأسبوع ❤️",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )
                    Text(
                        text = "${urgentFollowup.size} حالات",
                        fontSize = 12.sp,
                        color = StatusRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                urgentFollowup.forEach { youth ->
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
                            Column {
                                Text(
                                    text = youth.fullName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "غياب ${youth.stats.consecutiveAbsences} لقاءات • الخادم: ${youth.assignedServantName ?: "غير معين"}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }

                            FollowupStatusBadge(status = youth.stats.followupStatus, compact = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = TextSecondaryLight)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ChurchNavy
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

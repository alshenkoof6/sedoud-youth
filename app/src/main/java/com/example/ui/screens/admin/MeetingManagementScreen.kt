package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Meeting
import com.example.data.model.MeetingStatus
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.components.QRCodeDisplay
import com.example.ui.theme.*

@Composable
fun MeetingManagementScreen(
    repository: YouthMeetingRepository,
    modifier: Modifier = Modifier
) {
    val meetings by repository.meetings.collectAsState()
    val activeMeeting = meetings.firstOrNull { it.status == MeetingStatus.ACTIVE }

    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Title & New Meeting Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "إدارة الاجتماع وكود الـ QR",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )
                    Text(
                        text = "عرض الكود المشفر على شاشة القاعة لتسجيل حضور الشباب",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChurchPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("create_meeting_dialog_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "اجتماع جديد", fontSize = 12.sp)
                }
            }
        }

        // 2. Active Meeting Live Display Card with QR Code
        if (activeMeeting != null) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusGreenBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(StatusGreen, RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "كود الحضور الحي نشط الآن",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }

                        Text(
                            text = activeMeeting.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )

                        Text(
                            text = "${activeMeeting.dateDisplay} • ${activeMeeting.location}",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )

                        // Visual Animated QR Code Canvas
                        val token = activeMeeting.qrToken?.token ?: "QR_TOKEN_DEMO"
                        QRCodeDisplay(
                            token = token,
                            sizeDp = 200,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${activeMeeting.attendedCount} شباب",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                                Text(text = "سجلوا الحضور اليوم", fontSize = 11.sp, color = TextSecondaryLight)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = activeMeeting.qrToken?.expirationTime ?: "مفتوح",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchSecondary
                                )
                                Text(text = "ينتهي التسجيل في", fontSize = 11.sp, color = TextSecondaryLight)
                            }
                        }

                        // Refresh / Re-generate Token
                        OutlinedButton(
                            onClick = { repository.activateMeetingQR(activeMeeting.meetingId) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("refresh_qr_token_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = ChurchNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تجديد الرمز الأمني للكود (تحديث الـ Salt)",
                                color = ChurchNavy,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. Past Meetings List
        item {
            Text(
                text = "سجل الاجتماعات السابقة",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ChurchNavy
            )
        }

        items(meetings.filter { it.status != MeetingStatus.ACTIVE }) { meeting ->
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = meeting.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )
                        Text(
                            text = "${meeting.dateDisplay} • ${meeting.location}",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "حضر ${meeting.attendedCount} شاب",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ChurchPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateMeetingDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, date, time, loc ->
                repository.createMeeting(title, desc, date, time, loc)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateMeetingDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("الجمعة القادمة") }
    var time by remember { mutableStateOf("07:30 مساءً") }
    var location by remember { mutableStateOf("قاعة القديس أثناسيوس") }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        title = { Text(text = "إنشاء وتفعيل اجتماع جديد", fontWeight = FontWeight.Bold, color = ChurchNavy) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان اللقاء / الموضوع") },
                    placeholder = { Text("مثال: حياة التسليم والسلام الداخلي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_meeting_title")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف ومحاور اللقاء") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("التاريخ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("الموعد") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("مكان الانعقاد") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, description, date, time, location)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChurchPrimary),
                modifier = Modifier.testTag("submit_create_meeting_button")
            ) {
                Text("تفعيل الكود والبدء")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondaryLight)
            }
        }
    )
}

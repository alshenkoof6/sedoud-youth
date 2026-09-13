package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.example.data.model.*
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.components.FollowupStatusBadge
import com.example.ui.theme.*

@Composable
fun FollowupDashboardScreen(
    repository: YouthMeetingRepository,
    modifier: Modifier = Modifier
) {
    val youthList by repository.youthProfiles.collectAsState()
    val followups by repository.followupRecords.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf<FollowupStatus?>(null) }
    var selectedServantFilter by remember { mutableStateOf(false) } // true: only my assigned youth

    var selectedYouthForFollowup by remember { mutableStateOf<YouthProfile?>(null) }

    val filteredList = youthList.filter { youth ->
        val statusMatches = selectedStatusFilter == null || youth.stats.followupStatus == selectedStatusFilter
        val servantMatches = !selectedServantFilter || youth.assignedServantId == currentUser.userId
        statusMatches && servantMatches
    }.sortedByDescending {
        when (it.stats.followupStatus) {
            FollowupStatus.RED -> 4
            FollowupStatus.ORANGE -> 3
            FollowupStatus.YELLOW -> 2
            FollowupStatus.GREEN -> 1
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = "غرفة الافتقاد والرعاية الكنسية",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )
                Text(
                    text = "هدفنا المحبة والاطمئنان الروحي على كل شاب وشابة ❤️",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // 2. Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("الكل (${youthList.size})") }
                )

                FilterChip(
                    selected = selectedStatusFilter == FollowupStatus.RED,
                    onClick = { selectedStatusFilter = FollowupStatus.RED },
                    label = {
                        Text(
                            "أولوية قصوى (${youthList.count { it.stats.followupStatus == FollowupStatus.RED }})",
                            color = if (selectedStatusFilter == FollowupStatus.RED) Color.White else StatusRed
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusRed
                    )
                )

                FilterChip(
                    selected = selectedStatusFilter == FollowupStatus.ORANGE,
                    onClick = { selectedStatusFilter = FollowupStatus.ORANGE },
                    label = {
                        Text(
                            "انقطاع ملحوظ (${youthList.count { it.stats.followupStatus == FollowupStatus.ORANGE }})",
                            color = if (selectedStatusFilter == FollowupStatus.ORANGE) Color.White else StatusOrange
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusOrange
                    )
                )

                FilterChip(
                    selected = selectedStatusFilter == FollowupStatus.YELLOW,
                    onClick = { selectedStatusFilter = FollowupStatus.YELLOW },
                    label = {
                        Text(
                            "يحتاج تواصل (${youthList.count { it.stats.followupStatus == FollowupStatus.YELLOW }})",
                            color = if (selectedStatusFilter == FollowupStatus.YELLOW) Color.White else StatusYellow
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusYellow
                    )
                )

                FilterChip(
                    selected = selectedStatusFilter == FollowupStatus.GREEN,
                    onClick = { selectedStatusFilter = FollowupStatus.GREEN },
                    label = { Text("حضور منتظم (${youthList.count { it.stats.followupStatus == FollowupStatus.GREEN }})") }
                )
            }
        }

        // 3. Filter by Servant Toggle
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عرض المخدومين المخصصين لي فقط",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ChurchNavy
                    )
                    Switch(
                        checked = selectedServantFilter,
                        onCheckedChange = { selectedServantFilter = it }
                    )
                }
            }
        }

        // 4. Youth Follow-up Cards
        item {
            Text(
                text = "قائمة المتابعة حسب الأولوية (${filteredList.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ChurchNavy
            )
        }

        items(filteredList) { youth ->
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = youth.fullName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChurchNavy
                            )
                            Text(
                                text = "${youth.ageGroup.arabicName} • ${youth.university.ifEmpty { youth.school }}",
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                        }

                        FollowupStatusBadge(status = youth.stats.followupStatus)
                    }

                    HorizontalDivider(color = Color(0xFFF8FAFC))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مرات الغياب المتتالي: ${youth.stats.consecutiveAbsences} لقاءات",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (youth.stats.consecutiveAbsences >= 2) StatusRed else TextSecondaryLight
                            )
                            Text(
                                text = "آخر حضور: ${youth.stats.lastAttendDate}",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                            Text(
                                text = "الخادم المسؤول: ${youth.assignedServantName ?: "غير محدد"}",
                                fontSize = 11.sp,
                                color = ChurchPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Log followup button
                        Button(
                            onClick = { selectedYouthForFollowup = youth },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChurchPrimary),
                            modifier = Modifier.testTag("log_followup_button_${youth.userId}")
                        ) {
                            Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "تدوين افتقاد", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. Recent Follow-up Logs
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "آخر تقارير الافتقاد المسجلة (${followups.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ChurchNavy
            )
        }

        items(followups) { log ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${log.youthName} (بواسطة ${log.servantName})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchNavy
                        )
                        Text(text = log.dateDisplay, fontSize = 11.sp, color = TextSecondaryLight)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEFF6FF)) {
                            Text(
                                text = log.contactMethod.arabicName,
                                fontSize = 11.sp,
                                color = ChurchPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                            Text(
                                text = log.reasonStatus.arabicName,
                                fontSize = 11.sp,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = log.generalNotes,
                        fontSize = 12.sp,
                        color = Color(0xFF334155)
                    )

                    if (!log.nextFollowupDate.isNullOrEmpty()) {
                        Text(
                            text = "موعد المتابعة القادم: ${log.nextFollowupDate}",
                            fontSize = 11.sp,
                            color = StatusGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Follow-up Entry Dialog
    selectedYouthForFollowup?.let { youth ->
        AddFollowupDialog(
            youth = youth,
            currentServant = currentUser,
            onDismiss = { selectedYouthForFollowup = null },
            onSubmit = { method, reason, notes, nextDate ->
                repository.addFollowup(
                    youthId = youth.userId,
                    servantId = currentUser.userId,
                    servantName = currentUser.fullName,
                    method = method,
                    reason = reason,
                    notes = notes,
                    nextDate = nextDate
                )
                selectedYouthForFollowup = null
            }
        )
    }
}

@Composable
fun AddFollowupDialog(
    youth: YouthProfile,
    currentServant: User,
    onDismiss: () -> Unit,
    onSubmit: (ContactMethod, FollowupReason, String, String?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(ContactMethod.PHONE) }
    var selectedReason by remember { mutableStateOf(FollowupReason.CONTACTED) }
    var notes by remember { mutableStateOf("") }
    var nextDate by remember { mutableStateOf("الجمعة القادمة") }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        title = {
            Text(
                text = "تدوين افتقاد: ${youth.fullName}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ChurchNavy
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "وسيلة التواصل:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ContactMethod.values().forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method.arabicName, fontSize = 11.sp) }
                        )
                    }
                }

                Text(text = "حالة ونتيجة التواصل:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FollowupReason.values().take(4).forEach { reason ->
                        FilterChip(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason },
                            label = { Text(reason.arabicName, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الافتقاد ومحاور الاطمئنان") },
                    placeholder = { Text("مثال: تم الاطمئنان عليه واعتذر بسبب ظروف امتحانات...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_followup_notes")
                )

                OutlinedTextField(
                    value = nextDate,
                    onValueChange = { nextDate = it },
                    label = { Text("الموعد المقترح للمتابعة القادمة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (notes.isNotBlank()) {
                        onSubmit(selectedMethod, selectedReason, notes, nextDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChurchPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_followup_record_button")
            ) {
                Text("حفظ تقرير الافتقاد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondaryLight) }
        }
    )
}

package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.AgeGroup
import com.example.data.model.YouthProfile
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.components.FollowupStatusBadge
import com.example.ui.theme.*

@Composable
fun YouthManagementScreen(
    repository: YouthMeetingRepository,
    modifier: Modifier = Modifier
) {
    val youthList by repository.youthProfiles.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedAgeGroup by remember { mutableStateOf<AgeGroup?>(null) }
    var youthForAssigning by remember { mutableStateOf<YouthProfile?>(null) }

    val filteredList = youthList.filter { youth ->
        val queryMatches = searchQuery.isBlank() ||
                youth.fullName.contains(searchQuery, ignoreCase = true) ||
                youth.phone.contains(searchQuery) ||
                youth.university.contains(searchQuery, ignoreCase = true) ||
                youth.job.contains(searchQuery, ignoreCase = true)

        val ageMatches = selectedAgeGroup == null || youth.ageGroup == selectedAgeGroup
        queryMatches && ageMatches
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
                    text = "دليل مخدومي الاجتماع",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )
                Text(
                    text = "إدارة البيانات، وتوزيع المخدومين على الخدام (${youthList.size} شاب)",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // 2. Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث بالاسم، الهاتف، الجامعة، أو الوظيفة...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ChurchPrimary) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("youth_search_input")
            )
        }

        // 3. Age Group Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedAgeGroup == null,
                    onClick = { selectedAgeGroup = null },
                    label = { Text("كل المراحل (${youthList.size})") }
                )

                AgeGroup.values().forEach { group ->
                    FilterChip(
                        selected = selectedAgeGroup == group,
                        onClick = { selectedAgeGroup = group },
                        label = { Text("${group.arabicName} (${youthList.count { it.ageGroup == group }})") }
                    )
                }
            }
        }

        // 4. Youth Cards
        items(filteredList) { youth ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(ChurchPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = youth.fullName.take(2),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = youth.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "كود: ${youth.userId} • هاتف: ${youth.phone}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        FollowupStatusBadge(status = youth.stats.followupStatus, compact = true)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "المرحلة: ${youth.ageGroup.arabicName} • ${youth.university.ifEmpty { youth.school }}",
                                fontSize = 12.sp,
                                color = Color(0xFF334155)
                            )
                            if (youth.faculty.isNotEmpty()) {
                                Text(
                                    text = "الكلية: ${youth.faculty} (${youth.academicYear})",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                            if (youth.job.isNotEmpty()) {
                                Text(
                                    text = "العمل: ${youth.job} في ${youth.workplace}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = ChurchPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الخادم: ${youth.assignedServantName ?: "غير معين"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ChurchPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = { youthForAssigning = youth },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("reassign_servant_${youth.userId}")
                        ) {
                            Text("تغيير الخادم", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Servant Assignment Dialog
    youthForAssigning?.let { youth ->
        AssignServantDialog(
            youth = youth,
            servants = repository.servants,
            onDismiss = { youthForAssigning = null },
            onSelect = { srv ->
                repository.assignServantToYouth(youth.userId, srv.userId, srv.fullName)
                youthForAssigning = null
            }
        )
    }
}

@Composable
fun AssignServantDialog(
    youth: YouthProfile,
    servants: List<com.example.data.model.User>,
    onDismiss: () -> Unit,
    onSelect: (com.example.data.model.User) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تعيين خادم لمتابعة: ${youth.fullName}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ChurchNavy
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "اختر الخادم المسؤول عن الافتقاد الدوري:",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
                servants.forEach { servant ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (youth.assignedServantId == servant.userId) Color(0xFFDBEAFE) else Color(0xFFF8FAFC)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(servant) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = servant.fullName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChurchNavy
                                )
                                Text(
                                    text = "هاتف: ${servant.phone}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                            if (youth.assignedServantId == servant.userId) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ChurchPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondaryLight) }
        }
    )
}

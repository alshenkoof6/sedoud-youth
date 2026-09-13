package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.FollowupDashboardScreen
import com.example.ui.screens.admin.MeetingManagementScreen
import com.example.ui.screens.admin.YouthManagementScreen
import com.example.ui.screens.youth.YouthAttendanceScreen
import com.example.ui.screens.youth.YouthHomeScreen
import com.example.ui.screens.youth.YouthProfileScreen
import com.example.ui.theme.ChurchNavy
import com.example.ui.theme.ChurchPrimary
import com.example.ui.theme.ChurchSecondary

enum class YouthTab(val titleArabic: String) {
    HOME("الرئيسية"),
    ATTENDANCE("تسجيل الحضور"),
    PROFILE("الملف والتفرغ")
}

enum class AdminTab(val titleArabic: String) {
    DASHBOARD("المتابعة"),
    MEETING_QR("كود الاجتماع"),
    PASTORAL_CARE("الافتقاد"),
    YOUTH_DIRECTORY("دليل الشباب")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    repository: YouthMeetingRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()

    var currentYouthTab by remember { mutableStateOf(YouthTab.HOME) }
    var currentAdminTab by remember { mutableStateOf(AdminTab.DASHBOARD) }
    var showRoleSwitcherSheet by remember { mutableStateOf(false) }

    // Enforce Arabic RTL Layout Direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "اجتماع الشباب",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "✝️",
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "كنيسة السيدة العذراء والشهيد مارجرجس",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    },
                    actions = {
                        // Quick Role Switcher Chip for preview & testing
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ChurchSecondary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { showRoleSwitcherSheet = true }
                                .testTag("role_switcher_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "تبديل الصلاحية",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser.role.displayNameArabic,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ChurchNavy,
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    if (currentUser.role == UserRole.YOUTH) {
                        NavigationBarItem(
                            selected = currentYouthTab == YouthTab.HOME,
                            onClick = { currentYouthTab = YouthTab.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text(YouthTab.HOME.titleArabic) },
                            modifier = Modifier.testTag("nav_youth_home")
                        )
                        NavigationBarItem(
                            selected = currentYouthTab == YouthTab.ATTENDANCE,
                            onClick = { currentYouthTab = YouthTab.ATTENDANCE },
                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                            label = { Text(YouthTab.ATTENDANCE.titleArabic) },
                            modifier = Modifier.testTag("nav_youth_attendance")
                        )
                        NavigationBarItem(
                            selected = currentYouthTab == YouthTab.PROFILE,
                            onClick = { currentYouthTab = YouthTab.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text(YouthTab.PROFILE.titleArabic) },
                            modifier = Modifier.testTag("nav_youth_profile")
                        )
                    } else {
                        // Admin / Servant tabs
                        NavigationBarItem(
                            selected = currentAdminTab == AdminTab.DASHBOARD,
                            onClick = { currentAdminTab = AdminTab.DASHBOARD },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                            label = { Text(AdminTab.DASHBOARD.titleArabic) },
                            modifier = Modifier.testTag("nav_admin_dashboard")
                        )
                        NavigationBarItem(
                            selected = currentAdminTab == AdminTab.MEETING_QR,
                            onClick = { currentAdminTab = AdminTab.MEETING_QR },
                            icon = { Icon(Icons.Default.QrCode2, contentDescription = null) },
                            label = { Text(AdminTab.MEETING_QR.titleArabic) },
                            modifier = Modifier.testTag("nav_admin_meeting_qr")
                        )
                        NavigationBarItem(
                            selected = currentAdminTab == AdminTab.PASTORAL_CARE,
                            onClick = { currentAdminTab = AdminTab.PASTORAL_CARE },
                            icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null) },
                            label = { Text(AdminTab.PASTORAL_CARE.titleArabic) },
                            modifier = Modifier.testTag("nav_admin_pastoral_care")
                        )
                        NavigationBarItem(
                            selected = currentAdminTab == AdminTab.YOUTH_DIRECTORY,
                            onClick = { currentAdminTab = AdminTab.YOUTH_DIRECTORY },
                            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            label = { Text(AdminTab.YOUTH_DIRECTORY.titleArabic) },
                            modifier = Modifier.testTag("nav_admin_youth_directory")
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = modifier.imePadding()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                if (currentUser.role == UserRole.YOUTH) {
                    when (currentYouthTab) {
                        YouthTab.HOME -> YouthHomeScreen(
                            repository = repository,
                            onNavigateToScan = { currentYouthTab = YouthTab.ATTENDANCE },
                            onNavigateToProfile = { currentYouthTab = YouthTab.PROFILE }
                        )
                        YouthTab.ATTENDANCE -> YouthAttendanceScreen(repository = repository)
                        YouthTab.PROFILE -> YouthProfileScreen(repository = repository)
                    }
                } else {
                    when (currentAdminTab) {
                        AdminTab.DASHBOARD -> AdminDashboardScreen(
                            repository = repository,
                            onNavigateToMeetings = { currentAdminTab = AdminTab.MEETING_QR },
                            onNavigateToFollowup = { currentAdminTab = AdminTab.PASTORAL_CARE },
                            onNavigateToYouth = { currentAdminTab = AdminTab.YOUTH_DIRECTORY }
                        )
                        AdminTab.MEETING_QR -> MeetingManagementScreen(repository = repository)
                        AdminTab.PASTORAL_CARE -> FollowupDashboardScreen(repository = repository)
                        AdminTab.YOUTH_DIRECTORY -> YouthManagementScreen(repository = repository)
                    }
                }
            }
        }

        // Role Switcher Modal BottomSheet
        if (showRoleSwitcherSheet) {
            ModalBottomSheet(
                onDismissRequest = { showRoleSwitcherSheet = false },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "تبديل وضع المعاينة والصلاحية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchNavy
                    )
                    Text(
                        text = "يمكنك اختبار تجربة الشاب، أو تجربة الخادم وأمين الخدمة فورياً:",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    RoleSwitchOption(
                        title = "تجربة الشاب (جورج سامي)",
                        roleDescription = "تسجيل الحضور بالـ QR، النقاط، السلسلة، الجدول الشخصي والتنبيهات",
                        isSelected = currentUser.role == UserRole.YOUTH,
                        onSelect = {
                            repository.switchRole(UserRole.YOUTH)
                            currentYouthTab = YouthTab.HOME
                            showRoleSwitcherSheet = false
                        }
                    )

                    RoleSwitchOption(
                        title = "تجربة الخادم (الخادم مينا عادل)",
                        roleDescription = "غرفة الافتقاد، متابعة الغياب، تدوين تقارير الافتقاد، وتوزيع الشباب",
                        isSelected = currentUser.role == UserRole.SERVANT,
                        onSelect = {
                            repository.switchRole(UserRole.SERVANT)
                            currentAdminTab = AdminTab.DASHBOARD
                            showRoleSwitcherSheet = false
                        }
                    )

                    RoleSwitchOption(
                        title = "تجربة أمين الخدمة / المسؤول (د. بيشوي حنا)",
                        roleDescription = "توليد كود الـ QR الحي، تفعيل الاجتماعات، الإحصائيات الكاملة وإدارة الخدمة",
                        isSelected = currentUser.role == UserRole.ADMIN,
                        onSelect = {
                            repository.switchRole(UserRole.ADMIN)
                            currentAdminTab = AdminTab.DASHBOARD
                            showRoleSwitcherSheet = false
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun RoleSwitchOption(
    title: String,
    roleDescription: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFDBEAFE) else Color(0xFFF8FAFC)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
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
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchNavy
                )
                Text(
                    text = roleDescription,
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ChurchPrimary
                )
            }
        }
    }
}

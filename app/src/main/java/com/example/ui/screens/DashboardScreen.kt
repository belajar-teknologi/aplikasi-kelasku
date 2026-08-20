package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Student
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClass.collectAsStateWithLifecycle()
    val alerts by viewModel.allPushAlerts.collectAsStateWithLifecycle()
    val scheduleEvents by viewModel.allScheduleEvents.collectAsStateWithLifecycle()
    val homeworkList by viewModel.allHomework.collectAsStateWithLifecycle()

    var showQuickNotificationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dasbor Guru Pintar",
                subtitle = "Analisis Kemajuan Belajar & Kelas",
                actions = {
                    IconButton(
                        onClick = { viewModel.exportWeeklyReportPdf(context, selectedClass) },
                        modifier = Modifier.testTag("dashboard_export_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Unduh PDF", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Class Filter
            item {
                ClassFilterChipRow(
                    selectedClass = selectedClass,
                    onClassSelected = { viewModel.setSelectedClass(it) }
                )
            }

            // Hero Banner with Teacher Welcome & Overall Stats
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF005A43), Color(0xFF1E3A8A))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Selamat Mengajar, Guru!",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Semester Ganjil 2026/2027 • $selectedClass",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD1FAE5))
                                    )
                                }
                                Surface(
                                    color = Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Real-time AI",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Rata-rata Nilai", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFE2E8F0)))
                                    Text(
                                        text = String.format("%.1f", analytics.averageClassScore),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = Color(0xFFFEF08A),
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                    Text(
                                        text = if (analytics.averageClassScore >= 75) "Di atas standar KKM" else "Perlu Remedial",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF86EFAC))
                                    )
                                }

                                Column {
                                    Text("Tingkat Kehadiran", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFE2E8F0)))
                                    Text(
                                        text = "${String.format("%.0f", analytics.attendanceRatePercentage)}%",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                    Text(
                                        text = "${analytics.totalHadir} Hadir / ${analytics.totalStudents} Siswa",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF86EFAC))
                                    )
                                }

                                Column {
                                    Text("Tugas Aktif", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFE2E8F0)))
                                    Text(
                                        text = "${homeworkList.count { !it.isCompleted }} PR",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                    Text(
                                        text = "Tersinkronisasi",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF93C5FD))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Action Bar
            item {
                SectionHeader(title = "Aksi Cepat Guru")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.FactCheck,
                        label = "Presensi Harian",
                        bgColor = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF15803D),
                        onClick = { viewModel.navigateTo(AppNavDestination.ATTENDANCE) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Quiz,
                        label = "Kuis Daring",
                        bgColor = Color(0xFFE0E7FF),
                        iconTint = Color(0xFF4338CA),
                        onClick = { viewModel.navigateTo(AppNavDestination.QUIZ) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Assignment,
                        label = "Tugas Rumah",
                        bgColor = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFB45309),
                        onClick = { viewModel.navigateTo(AppNavDestination.HOMEWORK) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Chat,
                        label = "Chat Ortu",
                        bgColor = Color(0xFFFCE7F3),
                        iconTint = Color(0xFFBE185D),
                        onClick = { viewModel.navigateTo(AppNavDestination.CHAT) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Real-time Attendance Breakdown
            item {
                SectionHeader(
                    title = "Rekapitulasi Kehadiran Kelas",
                    actionText = "Kelola Presensi",
                    onActionClick = { viewModel.navigateTo(AppNavDestination.ATTENDANCE) }
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttendanceStatItem(label = "Hadir", count = analytics.totalHadir, color = PresentGreen)
                        AttendanceStatItem(label = "Sakit", count = analytics.totalSakit, color = SickYellow)
                        AttendanceStatItem(label = "Izin", count = analytics.totalIzin, color = PermitBlue)
                        AttendanceStatItem(label = "Alpa", count = analytics.totalAlpa, color = AbsentRed)
                    }
                }
            }

            // Subject Grade Analytics & Mastery
            item {
                SectionHeader(
                    title = "Analisis Penguasaan Materi",
                    actionText = "Buku Nilai",
                    onActionClick = { viewModel.navigateTo(AppNavDestination.GRADES) }
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        analytics.subjectAverages.forEach { (subject, avgScore) ->
                            SubjectProgressRow(
                                subject = subject,
                                score = avgScore,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Top Students & At Risk Students Tabs / Lists
            item {
                SectionHeader(title = "Peringkat Prestasi & Evaluasi Belajar")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top 3 Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Siswa Terbaik", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            analytics.topStudents.take(3).forEachIndexed { index, (student, score) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}. ${student.name.take(12)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = String.format("%.1f", score),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF92400E)
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // At-Risk (Perlu Bimbingan) Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Perlu Remedial", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (analytics.atRiskStudents.isEmpty()) {
                                Text(
                                    "Semua siswa tuntas KKM (>=75)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF16A34A))
                                )
                            } else {
                                analytics.atRiskStudents.take(3).forEach { student ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = student.name.take(12),
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB91C1C))
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.sendWhatsAppGrades(context, student)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Send,
                                                contentDescription = "WhatsApp Ortu",
                                                tint = WhatsAppGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Upcoming Schedule & Exams Reminder
            item {
                SectionHeader(
                    title = "Agenda & Ujian Mendatang",
                    actionText = "Lihat Kalender",
                    onActionClick = { viewModel.navigateTo(AppNavDestination.SCHEDULE) }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scheduleEvents.take(3).forEach { event ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${event.date} • ${event.startTime} - ${event.endTime} (${event.location})",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun QuickActionTile(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AttendanceStatItem(
    label: String,
    count: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun SubjectProgressRow(
    subject: String,
    score: Double,
    modifier: Modifier = Modifier
) {
    val progress = (score / 100.0).toFloat().coerceIn(0f, 1f)
    val color = when {
        score >= 85 -> Color(0xFF10B981)
        score >= 75 -> Color(0xFF3B82F6)
        score >= 65 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subject,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "${String.format("%.1f", score)} / 100",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
